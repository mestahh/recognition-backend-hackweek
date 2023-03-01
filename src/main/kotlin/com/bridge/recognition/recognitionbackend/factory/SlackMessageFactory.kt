package com.bridge.recognition.recognitionbackend.factory

import com.bridge.recognition.recognitionbackend.model.MessageField
import com.bridge.recognition.recognitionbackend.model.MessageSection
import com.bridge.recognition.recognitionbackend.model.MessageText
import com.bridge.recognition.recognitionbackend.model.RecognitionMessage
import com.bridge.recognition.recognitionbackend.model.WebhookMessage
import com.bridge.recognition.recognitionbackend.service.SlackService
import org.springframework.stereotype.Component

@Component
class SlackMessageFactory(val slackService: SlackService) {

    fun createSlackMessage(recognitionMessage: RecognitionMessage): WebhookMessage {
        val recipients = getRecipientsSlackIds(recognitionMessage.recipients)
        val headerText = MessageText(text = ":champagne: New gush arrived! :heart:")
        val header = MessageSection("header", headerText)

        val section1Fields =
            mutableListOf(MessageField(text = "> ${getSenderSlackId(recognitionMessage.senderEmail)} sent a gush to $recipients"))
        val section1 = MessageSection("section", fields = section1Fields)

        val section2Fields = mutableListOf(MessageField(text = "${recognitionMessage.message}"))
        val section2 = MessageSection("section", fields = section2Fields)
        val sections = mutableListOf<MessageSection>(header, section1, section2)
        return WebhookMessage(sections)
    }

    private fun getSenderSlackId(name: String): String = "<@${slackService.getSlackUserIdForEmail(name)}>"

    private fun getRecipientsSlackIds(recipients: String): String {
        return recipients.split(",").map {
            "<@${slackService.getSlackUserIdForEmail(it)}>"
        }.toList().joinToString(" ")
    }
}