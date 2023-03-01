package com.bridge.recognition.recognitionbackend.service

import com.bridge.recognition.recognitionbackend.dao.WebhooksRepository
import com.bridge.recognition.recognitionbackend.factory.SlackMessageFactory
import com.bridge.recognition.recognitionbackend.model.Webhook
import com.bridge.recognition.recognitionbackend.model.WebhookMessage
import com.bridge.recognition.recognitionbackend.model.createRecognitionMessage
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`
import java.time.LocalDateTime

class RecognitionServiceTest {

    val slackService = mock(SlackService::class.java)
    val webhooksRepository = mock(WebhooksRepository::class.java)
    val slackMessageFactory = mock(SlackMessageFactory::class.java)

    @Test
    fun `it does not send a notification if the message is not public`() {
        val testObj = RecognitionService(webhooksRepository, slackService, slackMessageFactory)
        val message = createRecognitionMessage().copy(public = false)

        testObj.sendNotifications(message)

        verifyNoInteractions(slackService)
        verifyNoInteractions(webhooksRepository)
    }

    @Test
    fun `it does not send a notification if there is no webhook`() {
        val testObj = RecognitionService(webhooksRepository, slackService, slackMessageFactory)
        `when`(webhooksRepository.findAll()).thenReturn(mutableListOf<Webhook>())

        val message = createRecognitionMessage()

        testObj.sendNotifications(message)

        verify(webhooksRepository).findAll()
        verifyNoInteractions(slackService)
        verifyNoMoreInteractions(webhooksRepository)
    }

    @Test
    fun `it sends a notification if there is a webhook`() {
        val testObj = RecognitionService(webhooksRepository, slackService, slackMessageFactory)
        val webhook = Webhook(1, "https://webhook.test", createdAt = LocalDateTime.now())
        val webhookMessage = mock(WebhookMessage::class.java)

        val message = createRecognitionMessage()

        `when`(webhooksRepository.findAll()).thenReturn(mutableListOf<Webhook>(webhook))
        `when`(slackMessageFactory.createSlackMessage(message)).thenReturn(webhookMessage)

        testObj.sendNotifications(message)

        verify(slackService).sendWebhookmessage("https://webhook.test", webhookMessage)
    }
}