package com.shaun.letschat.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.shaun.letschat.ModelClasses.Users
import com.shaun.letschat.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SettingsFragment : Fragment() {
    var userRef: DatabaseReference? = null
    private val RequestCode = 438
    private var imageuri: Uri? = null
    private var isCover = ""
    private var storageRef: StorageReference? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_settings, container, false)
        userRef = FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")
        userRef!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user: Users? = p0.getValue(Users::class.java)
                    if (context != null) {
                        view.username_settings.text = user!!.getUserName()
                        Picasso
                            .get()
                            .load(user.getProfile())
                            .into(view.profile_image_setting)
                        Picasso
                            .get()
                            .load(user.getCover())
                            .into(view.cover_image_settings)

                    }
                }
            }
        })
        view.profile_image_setting.setOnClickListener {
            Log.d("setting", "here")
            pickImg()

        }
        view.cover_image_settings.setOnClickListener {
            isCover = "cover"
            pickImg()
        }

        view.set_facebook.setOnClickListener {
            Log.d("setting", "here")

            setSocialLinks("facebook")
        }
        view.set_instagram.setOnClickListener {
            Log.d("setting", "here")
            setSocialLinks("instagram")
        }
        view.set_website.setOnClickListener {
            Log.d("setting", "here")

            setSocialLinks("website")
        }

        return view
    }

    private fun setSocialLinks(social: String) {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(context, R.style.Theme_AppCompat_DayNight_Dialog)
        val editText=EditText(context)
        if (social == "facebook") {
            builder.setTitle("Enter Facebook UserName")


        } else if (social == "instagram") {
            builder.setTitle("Enter Instgram username")
        } else {
            builder.setTitle("Enter your website url")
        }
        
        builder.setPositiveButton("Create",DialogInterface.OnClickListener{
            dialog, which -> 
            val str=editText.text.toString()
            if(str.isEmpty()){
                Toast.makeText(context, "Enter Please", Toast.LENGTH_SHORT).show()
            }else{
                
                saveSocial(str,social)
            }
        })

        if(social=="website"){
            editText.hint="Enter Url here"
        }
        else{
            editText.hint="Enter Username"
        }
        builder.setView(editText)

        builder.setNegativeButton("Cancel",DialogInterface.OnClickListener{
            dialog, which ->
        })
        builder.show()
    }

    private fun saveSocial(str: String, social: String) {
        val mapSocial=HashMap<String,Any>()

        when(social){
            "facebook" ->{
                mapSocial["facebook"]="https://facebook.com/$str"
            }
            "instagram"->{
                mapSocial["instagram"]="https://instagram.com/$str"
            }
            "website"->{
                mapSocial["website"]="https://$str"
            }
        }
        userRef!!.updateChildren(mapSocial).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(context, "Saved Succesfully", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Some Error Occured", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun pickImg() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode && resultCode == Activity.RESULT_OK && data!!.data != null) {
            imageuri = data.data
            Toast.makeText(context, "Image Uploading", Toast.LENGTH_SHORT).show()
            uploadImg()
        }
    }

    private fun uploadImg() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("Image Uploading.Please wait..........")
        progressBar.show()
        if (imageuri != null) {
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")
            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageuri!!)
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                if (!it.isSuccessful) {
                    it.exception?.let {
                        throw  it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener {
                if (it.isSuccessful) {
                    val downloadUrl = it.result
                    val url = downloadUrl.toString()
                    if (isCover == "cover") {
                        val map = HashMap<String, Any>()
                        map["cover"] = url
                        userRef!!.updateChildren(map)
                    } else {
                        val map = HashMap<String, Any>()
                        map["profile"] = url
                        userRef!!.updateChildren(map)
                        isCover = ""
                    }
                    progressBar.dismiss()
                }
            }
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}