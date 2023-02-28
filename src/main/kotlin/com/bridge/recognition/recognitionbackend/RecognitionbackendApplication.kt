package com.bridge.recognition.recognitionbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class RecognitionbackendApplication

fun main(args: Array<String>) {
	runApplication<RecognitionbackendApplication>(*args)
}
