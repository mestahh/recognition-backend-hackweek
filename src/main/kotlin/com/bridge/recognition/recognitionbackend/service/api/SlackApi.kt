package com.bridge.recognition.recognitionbackend.service.api

import com.slack.api.Slack
import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.emoji.EmojiListRequest
import com.slack.api.methods.request.users.UsersInfoRequest
import com.slack.api.methods.request.users.UsersLookupByEmailRequest
import com.slack.api.methods.response.users.UsersInfoResponse
import com.slack.api.methods.response.users.UsersLookupByEmailResponse
import org.springframework.stereotype.Component

@Component
class SlackApi(val slack: Slack) {

    //    private val SLACK_TOKEN = "xoxb-4887669717264-4863878395427-NYiVfrYuxoDPxMYrxGmT5TrV"  //Test
    private val SLACK_TOKEN = "xoxb-199397134436-4862721027111-yuu7GfOrDR4SpwLPDNNFLEMt" //Bridge

    fun getEmailForSlackId(slackId: String): UsersInfoResponse {
        val methods: MethodsClient = slack.methods(SLACK_TOKEN)
        val usersInfoRequest = UsersInfoRequest.builder().user(slackId).build()
        return methods.usersInfo(usersInfoRequest)
    }

    fun getSlackUserIdForEmail(userEmail: String): UsersLookupByEmailResponse {
        val methods: MethodsClient = slack.methods(SLACK_TOKEN)
        val userEmailRequest = UsersLookupByEmailRequest.builder().email(userEmail).build()
        return methods.usersLookupByEmail(userEmailRequest)
    }

    fun loadCustomEmojis(): MutableMap<String, String> {
        val methods: MethodsClient = slack.methods(SLACK_TOKEN)
        val emojiRequest = EmojiListRequest.builder().token(SLACK_TOKEN).build()
        return methods.emojiList(emojiRequest).emoji
    }
}