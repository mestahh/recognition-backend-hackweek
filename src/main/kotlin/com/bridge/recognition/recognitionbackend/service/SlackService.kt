package com.bridge.recognition.recognitionbackend.service

import com.bridge.recognition.recognitionbackend.dao.MessagesRepository
import com.bridge.recognition.recognitionbackend.dao.WebhooksRepository
import com.bridge.recognition.recognitionbackend.factory.SlackMessageFactory
import com.bridge.recognition.recognitionbackend.model.EmojiMap
import com.bridge.recognition.recognitionbackend.model.RecognitionMessage
import com.bridge.recognition.recognitionbackend.model.WebhookMessage
import com.bridge.recognition.recognitionbackend.service.api.SlackApi
import com.fasterxml.jackson.databind.ObjectMapper
import com.slack.api.Slack
import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.emoji.EmojiListRequest
import com.slack.api.methods.request.users.UsersInfoRequest
import com.slack.api.methods.request.users.UsersLookupByEmailRequest
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

@Service
class SlackService(
    val slack: Slack,
    val messagesRepository: MessagesRepository,
    val objectMapper: ObjectMapper,
    val webhooksRepository: WebhooksRepository,
    val slackMessageFactory: SlackMessageFactory,
    val emojiMap: EmojiMap,
    val slackApi: SlackApi,
    val messageParser: MessageTextParser
) {
    init {
        loadEmojiList().forEach { (key, value) -> emojiMap.emojis[key] = value.replace("\\", "") }
    }

    @Async
    fun sendRecognitionMessage(values: MultiValueMap<String, String>): CompletableFuture<RecognitionMessage> {
        val senderUserId = values["user_id"]?.get(0)
        val senderEmail = senderUserId?.let { getEmailForUserId(it) }
        val text = values["text"]!![0]

        val m = RecognitionMessage(
            id = null,
            message = messageParser.getMessage(text),
            recipients = messageParser.getRecipients(text),
            senderEmail = "$senderEmail",
            public = true,
            createdAt = LocalDateTime.now(),
            tags = messageParser.getTags(text)
        )
        val storedMessage = messagesRepository.save(m)
        sendNotifications(storedMessage)
        return CompletableFuture.completedFuture(storedMessage);

    }

    fun getEmailForUserId(userId: String): String {
        val userInfo = slackApi.getEmailForSlackId(userId)
        userInfo.user?.let {
            return it.profile.email
        }
        return ""
    }

    fun sendWebhookmessage(url: String, message: WebhookMessage) {
        val payload = objectMapper.writeValueAsString(message)
        slack.send(url, payload)
    }

    fun getSlackUserIdForEmail(userEmail: String): String {
       val userId = slackApi.getSlackUserIdForEmail(userEmail)
        userId.user?.let {
            return userId.user.id
        }
        return "NOT_FOUND"
    }

    fun sendNotifications(recognitionMessage: RecognitionMessage) {
        if (recognitionMessage.public) {
            val senderSlackId = "<@${getSlackUserIdForEmail(recognitionMessage.senderEmail)}>"
            val recipientsSlackIds = getRecipientsSlackIds(recognitionMessage.recipients)
            webhooksRepository.findAll().map {
                val message = slackMessageFactory.createSlackMessage(senderSlackId, recipientsSlackIds, recognitionMessage)
                sendWebhookmessage(it.webhookUrl, message)
            }
        }
    }

     fun getRecipientsSlackIds(recipients: String): String {
        return recipients.split(",").map {
            "<@${getSlackUserIdForEmail(it)}>"
        }.toList().joinToString(" ")
    }

    fun loadEmojiList(): MutableMap<String, String> {
        return slackApi.loadCustomEmojis()
    }
}