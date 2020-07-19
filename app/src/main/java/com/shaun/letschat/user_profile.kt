package com.shaun.letschat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shaun.letschat.ModelClasses.Users
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_profile.*

class user_profile : AppCompatActivity() {
    //    private var userId=""
    var user: Users? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.ThemeOverlay_AppCompat_Dark_ActionBar)
        setContentView(R.layout.activity_user_profile)
        setSupportActionBar(toolbar_profile)
        val userId = intent.getStringExtra("visit_id")
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    user = snapshot.getValue(Users::class.java)
                    Log.e(TAG, "onprofile: $user")

                    username_display.text = user!!.getUserName()
                    Picasso.get().load(user!!.getProfile()).into(profile_display)
                    Picasso.get().load(user!!.getCover()).into(cover_display)

                }

            }
        })
        set_facebook_display.setOnClickListener {
            val uri = Uri.parse(user!!.getFacebook())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        set_instagram_display.setOnClickListener {
            val uri = Uri.parse(user!!.getInstagram())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        set_website_display.setOnClickListener {

            val uri = Uri.parse(user!!.getWebsite())
            if (user!!.getWebsite().isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            } else {
                Toast.makeText(this, "No Website Specified", Toast.LENGTH_SHORT).show()
            }
        }

        send_msg.setOnClickListener {
            val intent = Intent(this@user_profile, MessaageChat::class.java)
            intent.putExtra("visit_id", user!!.getUID())
            startActivity(intent)
        }
    }
}