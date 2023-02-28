package com.bridge.recognition.recognitionbackend.controller

import com.bridge.recognition.recognitionbackend.dao.MessagesRepository
import com.bridge.recognition.recognitionbackend.dao.WebhooksRepository
import com.bridge.recognition.recognitionbackend.model.RecognitionMessage
import com.bridge.recognition.recognitionbackend.model.Webhook
import com.bridge.recognition.recognitionbackend.service.RecognitionService
import com.bridge.recognition.recognitionbackend.service.SlackService
import com.slack.api.Slack
import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.users.UsersInfoRequest
import com.slack.api.methods.request.users.UsersInfoRequest.UsersInfoRequestBuilder
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@CrossOrigin
class RecognitionController(
    val webhookRepository: WebhooksRepository,
    val messagesRepository: MessagesRepository,
    val recognitionService: RecognitionService,
    val slackService: SlackService
) {

    @PostMapping("/recognition/gush")
    fun slackCallback(@RequestBody values: MultiValueMap<String, String>): String {
        slackService.sendRecognitionMessage(values)
        return "Thanks for posting a gush!";
    }

    @PostMapping("/recognition/messages")
    fun createMessage(@RequestBody message: RecognitionMessage): RecognitionMessage {
        val message = messagesRepository.save(message)
        recognitionService.sendNotifications(message)
        return message
    }

    @DeleteMapping("/recognition/messages/{messageId}")
    fun deleteMessage(@PathVariable("messageId") messageId: Long) {
        messagesRepository.deleteById(messageId)
    }

    @GetMapping("/recognition/messages")
    fun getMessages(): List<RecognitionMessage> {
        return messagesRepository.findAllByOrderByCreatedAtDesc()
    }

    @GetMapping("/recognition/webhooks")
    fun getWebhooks(): List<Webhook> {
        return webhookRepository.findAll()
    }

    @PostMapping("/recognition/webhooks")
    fun createWebhook(@RequestBody webhookUrl: String) {
        val newWebhook = Webhook(id = null, webhookUrl = webhookUrl, createdAt = LocalDateTime.now())
        webhookRepository.save(newWebhook)
    }

    @DeleteMapping("/recognition/webhooks/{webhookId}")
    fun deleteWebhook(@PathVariable("webhookId") id: Long) {
        webhookRepository.deleteById(id);
    }
}