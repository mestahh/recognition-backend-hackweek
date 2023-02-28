package com.bridge.recognition.recognitionbackend.configuration

import com.slack.api.Slack
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RecognitionConfiguration {

    @Bean
    fun createSlackClient(): Slack {
        return Slack.getInstance()
    }
}