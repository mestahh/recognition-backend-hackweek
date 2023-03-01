package com.bridge.recognition.recognitionbackend.service

import com.bridge.recognition.recognitionbackend.dao.MessagesRepository
import com.bridge.recognition.recognitionbackend.model.MessageField
import com.bridge.recognition.recognitionbackend.model.MessageSection
import com.bridge.recognition.recognitionbackend.model.MessageText
import com.bridge.recognition.recognitionbackend.model.WebhookMessage
import com.fasterxml.jackson.databind.ObjectMapper
import com.slack.api.Slack
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock


@Disabled
class SlackServiceTest {
    private val SLACK_TOKEN = "token"

    val messagesRepository: MessagesRepository = mock(MessagesRepository::class.java)

    @Test
    fun sendMessageToSlack() {
        val url = "https://hooks.slack.com/services/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"
        val testObj = SlackService(Slack.getInstance(), messagesRepository, ObjectMapper())
        val headerText = MessageText(text = ":champagne: New gush arrived! :heart:")
        val header = MessageSection("header", headerText)

        val section1Fields = mutableListOf(MessageField(text="> @user sent a gush to @user1, @user2"))
        val section1 = MessageSection("section", fields = section1Fields)

        val section2Fields = mutableListOf(MessageField(text="Thanks for your really valuable contribution."))
        val section2 = MessageSection("section", fields = section2Fields)
        val sections = mutableListOf<MessageSection>(header, section1, section2)

        testObj.sendWebhookmessage(url, WebhookMessage(sections))
    }
}