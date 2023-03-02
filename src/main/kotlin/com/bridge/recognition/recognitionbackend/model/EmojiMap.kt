package com.bridge.recognition.recognitionbackend.model

import org.springframework.stereotype.Component

@Component
class EmojiMap(val emojis: MutableMap<String, String> = mutableMapOf())