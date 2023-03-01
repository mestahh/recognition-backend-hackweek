package com.bridge.recognition.recognitionbackend.model

import com.fasterxml.jackson.annotation.JsonInclude


open class WebhookMessage(val blocks: MutableList<MessageSection>)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class MessageSection(
    val type: String,
    val text: MessageText? = null,
    val fields: MutableList<MessageField>? = null
)

data class MessageField(
    val type: String = "mrkdwn",
    val text: String
)

data class MessageText(
    val type: String = "plain_text",
    val text: String
)