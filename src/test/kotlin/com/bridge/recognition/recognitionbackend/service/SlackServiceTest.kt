package com.bridge.recognition.recognitionbackend.service

import com.bridge.recognition.recognitionbackend.dao.MessagesRepository
import com.bridge.recognition.recognitionbackend.dao.WebhooksRepository
import com.bridge.recognition.recognitionbackend.factory.SlackMessageFactory
import com.bridge.recognition.recognitionbackend.model.EmojiMap
import com.bridge.recognition.recognitionbackend.model.MessageField
import com.bridge.recognition.recognitionbackend.model.MessageSection
import com.bridge.recognition.recognitionbackend.model.MessageText
import com.bridge.recognition.recognitionbackend.model.WebhookMessage
import com.fasterxml.jackson.databind.ObjectMapper
import com.slack.api.Slack
import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.emoji.EmojiListRequest
import com.slack.api.methods.request.users.UsersLookupByEmailRequest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock


class SlackServiceTest {
    private val SLACK_TOKEN = "xoxb-199397134436-4862721027111-yuu7GfOrDR4SpwLPDNNFLEMt"

    val messagesRepository: MessagesRepository = mock(MessagesRepository::class.java)
    val webhooksRepository: WebhooksRepository = mock(WebhooksRepository::class.java)



}