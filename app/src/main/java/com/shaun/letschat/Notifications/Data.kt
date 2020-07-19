package com.shaun.letschat.Notifications

class Data {
    private var user: String = ""
    private var icon = 0
    private var body = ""
    private var title = ""
    private var sent = ""

    constructor()
    constructor(user: String, icon: Int, body: String, title: String, sent: String) {
        this.user = user
        this.icon = icon
        this.body = body
        this.title = title
        this.sent = sent
    }

    fun getUser(): String {
        return user
    }

    fun setUser(user: String) {
        this.user = user
    }

    fun getIcon(): Int? {
        return icon
    }

    fun setIcon(icon: Int) {
        this.icon = icon
    }

    fun getBody(): String? {
        return body
    }

    fun setBody(body: String) {
        this.body = body
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun getSent(): String? {
        return sent
    }

    fun setSent(sent: String) {
        this.sent = sent
    }
}