package com.bridge.recognition.recognitionbackend.service

import com.bridge.recognition.recognitionbackend.dao.WebhooksRepository
import com.bridge.recognition.recognitionbackend.model.RecognitionMessage
import com.bridge.recognition.recognitionbackend.model.WebhookMessage
import com.fasterxml.jackson.databind.ObjectMapper
import com.slack.api.Slack
import com.slack.api.webhook.WebhookResponse
import org.springframework.stereotype.Service

@Service
class RecognitionService(val webhooksRepository: WebhooksRepository, val objectMapper: ObjectMapper) {
    fun sendNotifications(recognitionMessage: RecognitionMessage) {
        if (recognitionMessage.public) {
            webhooksRepository.findAll().map {
                val message =
                    WebhookMessage("${recognitionMessage.senderName} sent a gush to ${recognitionMessage.recipients} because: \"${recognitionMessage.message}\"")

                val slack: Slack = Slack.getInstance()

                val payload = objectMapper.writeValueAsString(message)
                slack.send(it.webhookUrl, payload)
            }
        }
    }
}