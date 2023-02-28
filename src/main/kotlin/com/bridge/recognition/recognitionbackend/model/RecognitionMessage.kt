package com.bridge.recognition.recognitionbackend.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "messages")
class RecognitionMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,
    val message: String,
    val recipients: String,
    val senderName: String,
    val public: Boolean = false,
    val createdAt: LocalDateTime
)
