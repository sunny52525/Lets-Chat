package com.shaun.letschat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    private lateinit var mAuth:FirebaseAuth
    private lateinit var refUsers:DatabaseReference
    private var firebasUserID:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setSupportActionBar(toolbar_register)
        supportActionBar!!.title="Sign Up"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar_register.setNavigationOnClickListener {
          onBackPressed()
        }

        mAuth=FirebaseAuth.getInstance()
        button_register.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val userName:String=register_username.text.toString()
        val email:String=register_email.text.toString()
        val password:String=register_password.text.toString()
        
        if(userName.isEmpty()){
            Toast.makeText(this, "Please,Enter UserName", Toast.LENGTH_SHORT).show()
        }
        else if(email.isEmpty()){
            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show()           
        }else if(password.isEmpty()){
            Toast.makeText(this, "Password is Empty", Toast.LENGTH_SHORT).show()  //TODO Password Validation
        }else{
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { 
                if(it.isSuccessful){
                    firebasUserID=mAuth.currentUser!!.uid
                    refUsers=FirebaseDatabase.getInstance().reference.child("Users").child(firebasUserID)
                    val userHashMap=HashMap<String,Any>()
                    userHashMap["uid"]=firebasUserID
                    userHashMap["username"]=userName
                    userHashMap["profile"]="https://firebasestorage.googleapis.com/v0/b/chat-app-dec80.appspot.com/o/profile.png?alt=media&token=c2ceac5b-50e7-414d-8b67-fb49e04a8020"
                    userHashMap["cover"]="https://firebasestorage.googleapis.com/v0/b/chat-app-dec80.appspot.com/o/cover.jpg?alt=media&token=b841b02a-fb82-4420-bf0e-a4e8f1563f71"
                    userHashMap["status"]="offline"
                    userHashMap["search"]=userName.toLowerCase()
                    userHashMap["facebook"]="https://m.facebook.com"
                    userHashMap["twitter"]="https://m.twitter.com"
                    userHashMap["Instagram"]="https://m.instagram.com"
                    refUsers.updateChildren(userHashMap).addOnCompleteListener {
                        if(it.isSuccessful){
                            val intent=Intent(this,MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                    }
                }else{
                    Toast.makeText(this, "Something went Wrong : ${it.exception!!.message.toString()}", Toast.LENGTH_SHORT).show()
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