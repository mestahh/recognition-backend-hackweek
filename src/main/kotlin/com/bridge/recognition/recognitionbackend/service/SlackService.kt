package com.bridge.recognition.recognitionbackend.service

import com.bridge.recognition.recognitionbackend.dao.MessagesRepository
import com.bridge.recognition.recognitionbackend.model.RecognitionMessage
import com.bridge.recognition.recognitionbackend.model.WebhookMessage
import com.fasterxml.jackson.databind.ObjectMapper
import com.slack.api.Slack
import com.slack.api.methods.MethodsClient
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
    val objectMapper: ObjectMapper
) {
    private val TAGS_PREFIX = "##"
    private val SLACK_TOKEN = "token"

    @Async
    fun sendRecognitionMessage(values: MultiValueMap<String, String>): CompletableFuture<RecognitionMessage> {
        val senderUserId = values["user_id"]?.get(0)
        val senderEmail = senderUserId?.let { getEmailForUserId(it) }
        val text = values["text"]!![0]

        val m = RecognitionMessage(
            id = null,
            message = getMessage(text),
            recipients = getRecipients(text),
            senderEmail = "$senderEmail",
            public = true,
            createdAt = LocalDateTime.now(),
            tags = getTags(text)
        )
        val storedMessage = messagesRepository.save(m)
        return CompletableFuture.completedFuture(storedMessage);

    }

    fun getRecipients(text: String): String {
        val recipients = mutableListOf<String>()
        text.split(" ").forEach {
            if (it.startsWith("<@")) {
                var userId =
                    it.replace("<@", "").replace(">", "")
                userId = userId.substring(0, userId.indexOf("|"))
                recipients.add(getEmailForUserId(userId))
            }
        }
        return recipients.joinToString(",")
    }

    fun getMessage(text: String): String {
        val message = mutableListOf<String>()
        text.split(" ").forEach {
            if (!it.startsWith("<@") && !it.startsWith(TAGS_PREFIX)) {
                message.add(it)
            }
        }
        return message.joinToString(" ")
    }


    fun getTags(text: String): String {
        var tags = mutableListOf<String>()
        text.split(" ").forEach {
            if (it.startsWith(TAGS_PREFIX)) {
                tags.add(it.replace(TAGS_PREFIX, ""))
            }
        }
        return tags.joinToString(",")
    }

    fun getEmailForUserId(userId: String): String {
        val methods: MethodsClient = slack.methods(SLACK_TOKEN)
        val usersInfoRequest = UsersInfoRequest.builder().user(userId).build()
        val userInfo = methods.usersInfo(usersInfoRequest)
         userInfo.user.let {
            return it.profile.email
        }
        return ""
    }

    fun sendWebhookmessage(url: String, message: WebhookMessage) {
        val slack: Slack = Slack.getInstance()
        val payload = objectMapper.writeValueAsString(message)
        slack.send(url, payload)
    }

    fun getSlackUserIdForEmail(userEmail: String): String {
        val methods: MethodsClient = slack.methods(SLACK_TOKEN)
        val userEmailRequest = UsersLookupByEmailRequest.builder().email(userEmail).build()
        val userEmail = methods.usersLookupByEmail(userEmailRequest)
        userEmail.user?.let {
            return userEmail.user.id
        }
        return "NOT_FOUND"
    }
}