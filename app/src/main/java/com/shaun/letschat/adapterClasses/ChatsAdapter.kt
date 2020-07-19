package com.shaun.letschat.adapterClasses

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.shaun.letschat.ModelClasses.Chat
import com.shaun.letschat.R
import com.shaun.letschat.TAG
import com.shaun.letschat.View_Full_Image
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.message_item_left.view.*
import kotlinx.android.synthetic.main.message_item_left.view.show_text_message
import kotlinx.android.synthetic.main.message_item_left.view.text_seen
import kotlinx.android.synthetic.main.message_item_right.view.*

class ChatsAdapter(
    mContext: Context,
    mChatList: List<Chat>,
    imageUrl: String
) : RecyclerView.Adapter<ChatsAdapter.ViewHolder?>() {
    private val context = mContext
    private val mChatList = mChatList
    private var imageUrl = imageUrl

    var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser!!

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profile_image_message_item_left: CircleImageView? = null
        var show_text_message: TextView? = null
        var left_image_view: ImageView? = null
        var text_seen: TextView? = null
        var right_image_view: ImageView? = null

        init {
            profile_image_message_item_left =
                itemView.findViewById(R.id.profile_image_message_item_left)
            show_text_message = itemView.show_text_message
            left_image_view = itemView.left_image_view
            text_seen = itemView.text_seen
            right_image_view = itemView.right_image_view
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return if (position == 1) {
            val View =
                LayoutInflater.from(context).inflate(R.layout.message_item_right, parent, false)
            ViewHolder(View)
        } else {
            val View =
                LayoutInflater.from(context).inflate(R.layout.message_item_left, parent, false)
            ViewHolder(View)
        }
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }

    override fun getItemViewType(position: Int): Int {
//        return super.getItemViewType(position)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        return if (mChatList[position].getSender().equals((firebaseUser!!.uid))) {
            1

        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val chat = mChatList[position]

        Log.d(TAG, "onBindViewHolder: $chat")
        Picasso.get().load(imageUrl).into(holder.profile_image_message_item_left)
        //image
        if (chat.getMessage().equals("send you an image") && chat.geturl().isNotEmpty()) {
            if (chat.getSender().equals((firebaseUser!!.uid))) {
                holder.show_text_message!!.visibility = View.GONE
                holder.right_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.geturl()).into(holder.right_image_view)
                holder.right_image_view!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "View Image"
                        , "Delete Image"
                        , "Cancel"
                    )

                    val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("Options")
                    builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                        if (which == 0) {
                            val intent = Intent(context, View_Full_Image::class.java)
                            intent.putExtra("url", chat.geturl())
                            context.startActivity(intent)
                        } else if (which == 1) {
                            deleteSentMessage(position, holder)
                        }
                    })
                    builder.show()
                }
            } else if (!chat.getSender().equals((firebaseUser!!.uid))) {
                holder.show_text_message!!.visibility = View.GONE
                holder.left_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.geturl()).into(holder.left_image_view)
                holder.left_image_view!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "View Image"
                        , "Cancel"
                    )

                    val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("Options")
                    builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                        if (which == 0) {
                            val intent = Intent(context, View_Full_Image::class.java)
                            intent.putExtra("url", chat.geturl())
                            context.startActivity(intent)
                        }
                    })
                    builder.show()
                }
            }
        } else {
            holder.show_text_message!!.text = chat.getMessage()

            holder.show_text_message!!.setOnLongClickListener {
                val options = arrayOf<CharSequence>(
                    "Delete Message"

                )

                val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                builder.setTitle("Options")

                builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                    if (which == 0) {
                        deleteSentMessage(position, holder)
                    }
                })
                if (firebaseUser!!.uid == chat.getSender())
                    builder.show()
                true
            }

        }

        if (position == mChatList.size - 1) {

            if (chat.IsSeen()) {
                holder.text_seen!!.text = "Seen"
                if (chat.getMessage().equals("send you an image") && chat.geturl().isNotEmpty()) {
                    val lp = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.text_seen!!.layoutParams = lp
                }

            } else {
                holder.text_seen!!.text = "Sent"
                if (chat.getMessage().equals("send you an image") && chat.geturl().isNotEmpty()) {
                    val lp = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.text_seen!!.layoutParams = lp
                }

            }

        } else {
            holder.text_seen!!.visibility = View.GONE
        }
    }

    private fun deleteSentMessage(position: Int, holder: ViewHolder) {
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
            .child(mChatList.get(position).getmessageId())
            .removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(holder.itemView.context, "Message Deleted", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        holder.itemView.context,
                        "Some Error Occured while deleting Message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }
}