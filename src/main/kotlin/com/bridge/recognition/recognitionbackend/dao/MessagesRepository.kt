package com.bridge.recognition.recognitionbackend.dao

import com.bridge.recognition.recognitionbackend.model.RecognitionMessage
import org.springframework.data.jpa.repository.JpaRepository

interface MessagesRepository : JpaRepository<RecognitionMessage, Long> {
    fun findAllByOrderByCreatedAtDesc(): List<RecognitionMessage>
}