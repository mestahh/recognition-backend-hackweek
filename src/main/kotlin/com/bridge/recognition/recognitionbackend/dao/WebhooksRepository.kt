package com.bridge.recognition.recognitionbackend.dao

import com.bridge.recognition.recognitionbackend.model.Webhook
import org.springframework.data.jpa.repository.JpaRepository

interface WebhooksRepository : JpaRepository<Webhook, Long>