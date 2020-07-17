package com.shaun.letschat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {
    var firebasUser:FirebaseUser?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        button_welcome_Register.setOnClickListener{
            val intent=Intent(this@WelcomeActivity,SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
        button_welcome_Login.setOnClickListener{
            val intent=Intent(this@WelcomeActivity,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        firebasUser=FirebaseAuth.getInstance().currentUser
        if (firebasUser!=null){
            val intent=Intent(this@WelcomeActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}