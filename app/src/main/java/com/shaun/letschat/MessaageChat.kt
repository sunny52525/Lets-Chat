package com.shaun.letschat

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.shaun.letschat.ModelClasses.Chat
import com.shaun.letschat.ModelClasses.Users
import com.shaun.letschat.adapterClasses.ChatsAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_messaage_chat.*

const val TAG = "CHAT ACTIVITY"

class MessaageChat : AppCompatActivity() {
    var userIDVisit: String = ""
    var firebaseUser: FirebaseUser? = null
    var chatsAdapter: ChatsAdapter? = null
    var mChatList: List<Chat>? = null
    lateinit var recycler_view: RecyclerView
    var reference:DatabaseReference?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messaage_chat)
        val toolbar = toolbar_chat
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        userIDVisit = intent.getStringExtra("visit_id")

        firebaseUser = FirebaseAuth.getInstance().currentUser

        recycler_view = recycler_view_chats
        recycler_view.setHasFixedSize(true)
        var linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recycler_view.layoutManager = linearLayoutManager

        reference = FirebaseDatabase.getInstance().reference
            .child("Users").child(userIDVisit)

        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                username_chat.text = user!!.getUserName()
                Picasso.get().load(user!!.getProfile()).into(profile_image_chat)
                retrieveMessage(firebaseUser!!.uid, userIDVisit, user.getProfile())
            }
        })

        attach_image_file_btn.setOnClickListener {
            Toast.makeText(this, "here", Toast.LENGTH_SHORT).show()
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Pick  img"), 438)

        }
        seenMessage(userIDVisit)
        send_message_btn.setOnClickListener {
            val message = text_message.text.toString()
            if (message.isNotEmpty()) {
                sendMessageToUser(firebaseUser!!.uid, userIDVisit, message)
            }
            text_message.setText("")
        }
    }

    private fun retrieveMessage(senderid: String, reciverId: String?, imageUrl: String) {
        mChatList = ArrayList()

        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {

                (mChatList as ArrayList<Chat>).clear()
                for (snapshot in p0.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    Log.d(TAG, "onmsg: ${snapshot.child("isseen")}")
                    chat!!.setIseen(snapshot.child("isseen").value.toString().toBoolean())
                    chat!!.setReciever(snapshot.child("receiver").value.toString())  //for some weird fucking reason this doesnt happen automatically
                    Log.d(TAG, "onDataChange:${snapshot.child("receiver").value} ")


                    if (chat!!.getReciever().equals(senderid) && chat.getSender().equals(reciverId)
                        || chat.getReciever().equals(reciverId) && chat.getSender().equals(senderid)
                    ) {

                        (mChatList as ArrayList<Chat>).add(chat)
                        chatsAdapter = ChatsAdapter(
                            this@MessaageChat,
                            (mChatList as ArrayList<Chat>),
                            imageUrl
                        )
                        recycler_view.adapter = chatsAdapter
                    }
                }
            }
        })
    }

    private fun sendMessageToUser(sender: String, reciver: String?, message: String) {

        var reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key
        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = sender
        messageHashMap["message"] = message
        messageHashMap["receiver"] = reciver
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey
        reference.child("Chats").child(messageKey!!).setValue(messageHashMap)

            .addOnCompleteListener {

                if (it.isSuccessful) {
                    val chatListReference = FirebaseDatabase.getInstance()
                        .reference
                        .child("ChatList")
                        .child(firebaseUser!!.uid)
                        .child(userIDVisit)

                    val chatlis=FirebaseDatabase.getInstance().reference.child("ChatList").child(userIDVisit).child(firebaseUser!!.uid)
                    chatlis.child("id").setValue(firebaseUser!!.uid)

                    chatListReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (!snapshot.exists()) {
                                chatListReference.child("id")
                                    .setValue(userIDVisit)
                            }


                            val chatLisRecivertReference = FirebaseDatabase.getInstance()
                                .reference
                                .child("ChatList")
                                .child(userIDVisit)
                                .child(firebaseUser!!.uid)
                            chatListReference.child("id")
                                .setValue(firebaseUser!!.uid)
//                            chatLisRecivertReference.addValueEventListener(object :ValueEventListener{
//                                override fun onCancelled(error: DatabaseError) {
//                                    TODO("Not yet implemented")
//                                }
//
//                                override fun onDataChange(snapshot: DataSnapshot) {
//                                  if(!snapshot.exists()){
//                                      chatLisRecivertReference.child("id").setValue(firebaseUser!!.uid)
//                                  }
//                                }
//
//                            })
                        }


                    })


                    //FCM part left

                    val reference = FirebaseDatabase.getInstance().reference
                        .child("Users").child(firebaseUser!!.uid)

                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null && data!!.data != null) {

            Toast.makeText(this, "Here2", Toast.LENGTH_SHORT).show()
            val progressBar = ProgressDialog(this)
            progressBar.setMessage("Image Uploading.Please wait..........")
            progressBar.show()

            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageReference.child("$messageId.jpg")

            var uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                if (!it.isSuccessful) {
                    it.exception?.let {
                        throw  it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageHashMap = HashMap<String, Any?>()
                    messageHashMap["sender"] = firebaseUser!!.uid
                    messageHashMap["message"] = "send you an image"
                    messageHashMap["receiver"] = userIDVisit
                    messageHashMap["isseen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messageId"] = messageId

                    ref.child("Chats").child(messageId!!).setValue(messageHashMap)

                }
                progressBar.dismiss()
            }

        }
    }

    var seenListener: ValueEventListener? = null

    private fun seenMessage(userId: String) {
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        seenListener = reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (datasnapshot in p0.children) {
                    val chat = datasnapshot.getValue(Chat::class.java)
                    chat!!.setReciever(datasnapshot.child("receiver").value.toString())

//                    Log.d(TAG, "iseen: $chat")
//                    Log.d(TAG, "iseen ${chat.getReciever()} ${firebaseUser!!.uid}")
                    if (chat!!.getReciever().equals(firebaseUser!!.uid) && chat!!.getSender()
                            .equals(userId)
                    ) {
                        val hashMap=HashMap<String,Any>()
                        hashMap["isseen"]=true
                        datasnapshot.ref.updateChildren(hashMap)


                    }
                }

            }

        })
    }

    override fun onPause() {
        super.onPause()
        reference!!.removeEventListener(seenListener!!)
    }

}