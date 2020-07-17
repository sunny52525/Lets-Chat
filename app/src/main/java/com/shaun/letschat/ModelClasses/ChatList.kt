package com.shaun.letschat.ModelClasses

class ChatList {
    private var id =""
    constructor(
    )

    override fun toString(): String {
        return """
            id is $id
        """.trimIndent()
    }

    constructor(id:String){
        this.id=id

    }

    fun getId():String{
        return id
    }
    fun setId(id: String){
        this.id=id
    }
}