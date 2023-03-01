package com.bridge.recognition.recognitionbackend.model

import java.time.LocalDateTime

fun createRecognitionMessage(): RecognitionMessage = RecognitionMessage(
    id = 1L,
    message = "abcdefg",
    recipients = "john@test.com,alice@test.com",
    senderEmail = "bob@test.com",
    public = true,
    tags = null,
    createdAt = LocalDateTime.now()
)
