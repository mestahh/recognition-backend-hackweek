package com.bridge.recognition.recognitionbackend.service

import com.bridge.recognition.recognitionbackend.dao.MessagesRepository
import com.bridge.recognition.recognitionbackend.model.RecognitionMessage
import com.slack.api.Slack
import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.users.UsersInfoRequest
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

@Service
class SlackService(val slack: Slack, val messagesRepository: MessagesRepository) {

    private val SLACK_TOKEN = "TOKEN"

    @Async
    fun sendRecognitionMessage(values: MultiValueMap<String, String>): CompletableFuture<RecognitionMessage> {
        val senderUserId = values["user_id"]?.get(0)
        val senderEmail = senderUserId?.let { getEmailForUserId(it) }
        val text = values["text"]!![0]

        val m = RecognitionMessage(
            id = null,
            message = getMessage(text),
            recipients = getRecipients(text),
            senderName = "$senderEmail",
            public = true,
            createdAt = LocalDateTime.now()
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
            if (!it.startsWith("<@")) {
                message.add(it)
            }
        }
        return message.joinToString(" ")
    }

    fun getEmailForUserId(userId: String): String {
        val methods: MethodsClient = slack.methods(SLACK_TOKEN)
        val usersInfoRequest = UsersInfoRequest.builder().user(userId).build()
        val userInfo = methods.usersInfo(usersInfoRequest)
        return userInfo.user.profile.email
    }
}