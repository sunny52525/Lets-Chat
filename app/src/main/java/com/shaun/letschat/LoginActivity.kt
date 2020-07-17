package com.shaun.letschat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setSupportActionBar(toolbar_login)
        supportActionBar!!.title="Sign in"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar_login.setNavigationOnClickListener {
           onBackPressed()
        }

            mAuth=FirebaseAuth.getInstance()
        button_login.setOnClickListener{
            loginUser()
        }
    }

    private fun loginUser() {
        val email:String=email_login.text.toString()
        val password:String=password_login.text.toString()
        if(email.isEmpty()){
            Toast.makeText(this, "Please,Enter Email", Toast.LENGTH_SHORT).show()
        }
        else if(password.isEmpty()) {
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show()
        }else{
            mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener { 
                    if(it.isSuccessful){
                        val intent=Intent(this,MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this, "Error ${it.exception}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onBackPressed() {

        val intent= Intent(this,WelcomeActivity::class.java)
        startActivity(intent)
        finish()
        super.onBackPressed()
    }
}