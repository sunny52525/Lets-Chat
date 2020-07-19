package com.shaun.letschat.ModelClasses

class Chat{
    private var isseen:Boolean=false
    private var message:String=""
    private var messageId:String=""

    private var receiver:String=""
    private var sender:String =""

    private var url:String=""



    constructor()
    constructor(
        isseen: Boolean,
        message: String,
        messageId: String,
        receiver: String,
        sender: String,
        url: String

    ) {

        this.sender = sender
        this.message = message
        this.receiver = receiver
        this.isseen = isseen
        this.url = url
        this.messageId = messageId
    }

    override fun toString(): String {
        return """
            message is $message
            reciver is $receiver
            sender is $sender
            seen is $isseen
            messid is $messageId
            message is ${message}
            
        """.trimIndent()
    }

    fun getSender():String{
        return sender

    }
    fun setSender(sender: String?){
        this.sender=sender!!

    }
    fun getMessage():String{
        return message

    }
    fun setMessage(message: String){
        this.message = message

    }
    fun getReciever():String{
        return receiver

    }
    fun setReciever(receiver: String){
        this.receiver = receiver

    }
    fun IsSeen():Boolean{
        return isseen

    }
    fun setIseen(isseen: Boolean){
        this.isseen=isseen

    }
    fun geturl():String{
        return url

    }
    fun seturl(url: String){
        this.url = url

    }
    fun getmessageId():String{
        return messageId

    }
    fun setmessageId(messageId: String?){
        this.messageId=messageId!!

    }





}