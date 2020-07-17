package com.shaun.letschat.adapterClasses

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shaun.letschat.MessaageChat
import com.shaun.letschat.ModelClasses.Users
import com.shaun.letschat.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.user_search_item_layout.view.*
class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    var userName:TextView=itemView.username_search
    var profileImageView:CircleImageView=itemView.profile_image_search
    var onlineImageView:CircleImageView=itemView.image_online
    var offlineImageView:CircleImageView=itemView.image_offline
    var lastMsg=itemView.message_last
}

class UserAdapter(
    context: Context, mUsers: List<Users>,
    isChatCheck: Boolean
) :RecyclerView.Adapter<ViewHolder?>(){

    var musers=mUsers
    var mcontext=context
    var isChatCheck=isChatCheck

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
      val view:View=LayoutInflater.from(mcontext).inflate(R.layout.user_search_item_layout,parent,false)
        return  ViewHolder(view)
     }

    override fun getItemCount(): Int {
        return musers.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user:Users=musers[position]
        holder.userName.text=user!!.getUserName()
        Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile).into(holder.profileImageView)
        holder.itemView.setOnClickListener {
            val options= arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
            )
            val builder:AlertDialog.Builder=AlertDialog.Builder(mcontext)
            builder.setTitle("What u want")
            builder.setItems(options,DialogInterface.OnClickListener{dialog, which ->
                if(which==0){
                   val intent=Intent(mcontext,MessaageChat::class.java)
                    intent.putExtra("visit_id",user.getUID())
                    mcontext.startActivity(intent)

                }else{

                }
            })
            builder.show()
        }
    }

}