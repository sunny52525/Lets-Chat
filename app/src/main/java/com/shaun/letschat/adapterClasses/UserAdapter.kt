package com.shaun.letschat.adapterClasses

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shaun.letschat.MessaageChat
import com.shaun.letschat.ModelClasses.Chat
import com.shaun.letschat.ModelClasses.Users
import com.shaun.letschat.R
import com.shaun.letschat.TAG
import com.shaun.letschat.user_profile
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.user_search_item_layout.view.*


class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var userName: TextView = itemView.username_search
    var profileImageView: CircleImageView = itemView.profile_image_search
    var onlineImageView: CircleImageView = itemView.image_online
    var offlineImageView: CircleImageView = itemView.image_offline
    var lastMsg = itemView.message_last
}

class UserAdapter(
    context: Context, mUsers: List<Users>,
    isChatCheck: Boolean
) : RecyclerView.Adapter<ViewHolder?>() {

    var musers = mUsers
    var mcontext = context
    var isChatCheck = isChatCheck
    var lastMessage = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(mcontext).inflate(R.layout.user_search_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return musers.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: Users = musers[position]
        holder.userName.text = user.getUserName()
        Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile)
            .into(holder.profileImageView)







        if (isChatCheck) {
            retrieveLatMessage(user.getUID(), holder.lastMsg)
        } else {
            holder.lastMsg.visibility = View.GONE
        }

        if (isChatCheck) {
            if (user.getStatus() == "online") {
                holder.offlineImageView.visibility = View.GONE
                holder.onlineImageView.visibility = View.VISIBLE
            } else {
                holder.onlineImageView.visibility = View.GONE
                holder.offlineImageView.visibility = View.VISIBLE
            }
        } else {

            holder.offlineImageView.visibility = View.GONE
            holder.onlineImageView.visibility = View.GONE
        }
        holder.itemView.setOnLongClickListener {
            val options = arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(mcontext)
            builder.setTitle("What u want")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                if (which == 0) {
                    val intent = Intent(mcontext, MessaageChat::class.java)
                    intent.putExtra("visit_id", user.getUID())
                    mcontext.startActivity(intent)

                } else if (which == 1) {
                    val intent = Intent(mcontext, user_profile::class.java)
                    intent.putExtra("visit_id", user.getUID())
                    mcontext.startActivity(intent)

                }
            })
            builder.show()
            true
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(mcontext, MessaageChat::class.java)
            intent.putExtra("visit_id", user.getUID())
            mcontext.startActivity(intent)
        }
    }

    private fun retrieveLatMessage(uid: String, lastMsg: TextView?) {
        lastMessage = "default"
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (datasnap in snapshot.children) {
                    val chat: Chat? = datasnap.getValue(Chat::class.java)
                    chat!!.setReciever(datasnap.child("receiver").value.toString())
                    Log.d(TAG, "last: $chat")
                    if (firebaseUser != null && chat != null) {
                        if (chat.getReciever() == firebaseUser.uid && chat.getSender() == uid || chat.getReciever() == uid
                            && chat.getSender() == firebaseUser.uid
                        ) {
                            lastMessage = chat.getMessage()
                        }
                    }
                }
                if (lastMessage == "default") {
                    lastMsg?.text = "you suck"
                } else if (lastMessage == "send you an image") {
                    lastMsg?.text = "Image"
                } else {
                    lastMsg?.text = lastMessage
                }
                lastMessage = "default"
            }

        })
    }


}