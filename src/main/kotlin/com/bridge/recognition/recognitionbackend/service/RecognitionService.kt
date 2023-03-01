package com.bridge.recognition.recognitionbackend.service

import com.bridge.recognition.recognitionbackend.dao.WebhooksRepository
import com.bridge.recognition.recognitionbackend.factory.SlackMessageFactory
import com.bridge.recognition.recognitionbackend.model.RecognitionMessage
import org.springframework.stereotype.Service

@Service
class RecognitionService(
    val webhooksRepository: WebhooksRepository,
    val slackService: SlackService,
    val slackMessageFactory: SlackMessageFactory
) {
    fun sendNotifications(recognitionMessage: RecognitionMessage) {
        if (recognitionMessage.public) {
            webhooksRepository.findAll().map {
                val message = slackMessageFactory.createSlackMessage(recognitionMessage)
                slackService.sendWebhookmessage(it.webhookUrl, message)
            }
        }
    }
}