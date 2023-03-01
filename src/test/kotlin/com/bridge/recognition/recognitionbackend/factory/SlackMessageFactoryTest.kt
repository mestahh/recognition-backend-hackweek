package com.bridge.recognition.recognitionbackend.factory

import com.bridge.recognition.recognitionbackend.model.MessageField
import com.bridge.recognition.recognitionbackend.model.MessageText
import com.bridge.recognition.recognitionbackend.model.createRecognitionMessage
import com.bridge.recognition.recognitionbackend.service.SlackService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class SlackMessageFactoryTest {

    val slackService = mock(SlackService::class.java)

    @Test
    fun `it creates a Slack message with blocks`() {
        val testObj = SlackMessageFactory(slackService)
        val recognitionMessage = createRecognitionMessage()

        `when`(slackService.getSlackUserIdForEmail("bob@test.com")).thenReturn("ABCD1")
        `when`(slackService.getSlackUserIdForEmail("alice@test.com")).thenReturn("ABCD2")
        `when`(slackService.getSlackUserIdForEmail("john@test.com")).thenReturn("ABCD3")

        val result = testObj.createSlackMessage(recognitionMessage)
        assertEquals(3, result.blocks.size)
        assertEquals(
            MessageText(type = "plain_text", text = ":champagne: New gush arrived! :heart:"),
            result.blocks[0].text
        )
        assertEquals(
            MessageField(type = "mrkdwn", text = "> <@ABCD1> sent a gush to <@ABCD3> <@ABCD2>"),
            result.blocks[1].fields?.get(0)
        )
        assertEquals(MessageField(type = "mrkdwn", text = "abcdefg"), result.blocks[2].fields?.get(0))
    }
}

