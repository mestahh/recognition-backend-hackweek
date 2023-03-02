package com.bridge.recognition.recognitionbackend.factory

import com.bridge.recognition.recognitionbackend.model.EmojiMap
import com.bridge.recognition.recognitionbackend.model.MessageField
import com.bridge.recognition.recognitionbackend.model.MessageSection
import com.bridge.recognition.recognitionbackend.model.MessageText
import com.bridge.recognition.recognitionbackend.model.RecognitionMessage
import com.bridge.recognition.recognitionbackend.model.WebhookMessage
import org.springframework.stereotype.Component

private const val SECTION = "section"
private val EMOJI_REGEX = """:*:\w+""".toRegex()
private const val HEADER = "header"

@Component
class SlackMessageFactory(val emojis: EmojiMap) {

    fun createSlackMessage(
        senderSlackId: String,
        recipientsSlackIds: String,
        recognitionMessage: RecognitionMessage
    ): WebhookMessage {
        val headerText = MessageText(text = "Public praise was submitted in Bridge! :heart: :champagne:")
        val header = MessageSection(HEADER, headerText)

        val section1Fields =
            mutableListOf(MessageField(text = "> $senderSlackId sent a gush to $recipientsSlackIds"))
        val section1 = MessageSection(SECTION, fields = section1Fields)

        val section2Fields = mutableListOf(MessageField(text = recognitionMessage.message))
        val section2 = MessageSection(SECTION, fields = section2Fields)
        val sections = mutableListOf<MessageSection>(header, section1, section2)
        return WebhookMessage(sections)
    }

    fun replaceEmojiHtml(message: String): String {
        var matchResult = EMOJI_REGEX.findAll(message)
        var messageToReturn = message
        matchResult.iterator().forEach {
            val emojiKey = it.value.replace(":", "")
            val emojiUrl = emojis.emojis[emojiKey]
            val imageUrl = "<span><img src=\"$emojiUrl\" /></span>"
            messageToReturn = messageToReturn.replace(it.value + ":", imageUrl)
        }
        return messageToReturn
    }
}