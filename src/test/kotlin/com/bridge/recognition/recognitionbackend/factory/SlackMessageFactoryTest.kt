package com.bridge.recognition.recognitionbackend.factory

import com.bridge.recognition.recognitionbackend.model.EmojiMap
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

        val testObj = SlackMessageFactory(EmojiMap())
        val recognitionMessage = createRecognitionMessage().copy(
            message = "Thanks for your :dollar: contribution :smile:!"
        )

        `when`(slackService.getSlackUserIdForEmail("bob@test.com")).thenReturn("ABCD1")
        `when`(slackService.getSlackUserIdForEmail("alice@test.com")).thenReturn("ABCD2")
        `when`(slackService.getSlackUserIdForEmail("john@test.com")).thenReturn("ABCD3")

        val senderSlackId = "<@ABCD1>"
        val recipientsSlackIds = "<@ABCD3> <@ABCD2>"

        val result = testObj.createSlackMessage(senderSlackId, recipientsSlackIds, recognitionMessage)
        assertEquals(3, result.blocks.size)
        assertEquals(
            MessageText(type = "plain_text", text = "Public praise was submitted in Bridge! :heart: :champagne:"),
            result.blocks[0].text
        )
        assertEquals(
            MessageField(type = "mrkdwn", text = "> <@ABCD1> sent a gush to <@ABCD3> <@ABCD2>"),
            result.blocks[1].fields?.get(0)
        )
        assertEquals(
            MessageField(
                type = "mrkdwn",
                text = "Thanks for your :dollar: contribution :smile:!"
            ),
            result.blocks[2].fields?.get(0)
        )
    }

    @Test
    fun `test custom emoji replacement`() {
        val emojiMap = EmojiMap()
        emojiMap.emojis["smile"] = "http://emojis.com/smile.png"
        emojiMap.emojis["dollar"] = "http://emojis.com/dollar.png"

        val testObj = SlackMessageFactory(emojiMap)

        val result = testObj.replaceEmojiHtml("Thanks for your :dollar: contribution :smile:!")
        assertEquals(
            "Thanks for your <span><img src=\"http://emojis.com/dollar.png\" /></span> contribution <span><img src=\"http://emojis.com/smile.png\" /></span>!",
            result
        )
    }
}

