package com.bridge.recognition.recognitionbackend.service

import com.bridge.recognition.recognitionbackend.service.api.SlackApi
import org.springframework.stereotype.Component

@Component
class MessageTextParser(val slackApi: SlackApi) {

    private val TAGS_PREFIX = "##"

    fun getRecipients(text: String): String {
        val recipients = mutableListOf<String>()
        text.split(" ").forEach {
            if (it.startsWith("<@")) {
                var userId =
                    it.replace("<@", "").replace(">", "")
                userId = userId.substring(0, userId.indexOf("|"))
                recipients.add(slackApi.getEmailForSlackId(userId).user.profile.email)
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
        val tags = mutableListOf<String>()
        text.split(" ").forEach {
            if (it.startsWith(TAGS_PREFIX)) {
                tags.add(it.replace(TAGS_PREFIX, ""))
            }
        }
        return tags.joinToString(",")
    }
}