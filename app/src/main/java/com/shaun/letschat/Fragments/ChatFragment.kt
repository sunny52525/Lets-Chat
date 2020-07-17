package com.shaun.letschat.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shaun.letschat.ModelClasses.ChatList
import com.shaun.letschat.ModelClasses.Users
import com.shaun.letschat.R
import com.shaun.letschat.TAG
import com.shaun.letschat.adapterClasses.UserAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var usersChatList: List<ChatList>? = null
    lateinit var recyclerView_chatlist: RecyclerView
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        recyclerView_chatlist = view.findViewById(R.id.recycler_view_chatslist)
        recyclerView_chatlist.setHasFixedSize(true)
        recyclerView_chatlist.layoutManager = LinearLayoutManager(context)


        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersChatList = ArrayList()
        val ref =
            FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(p0: DataSnapshot) {
//                Log.d(TAG, "onDataChange: evrything ${p0}")
                (usersChatList as ArrayList).clear()

                for (datasnapshot in p0.children)
                {
                    Log.d(TAG, "onDataChange:1 $datasnapshot")
                    val chatlist=datasnapshot.getValue(ChatList::class.java)
//                    Log.d(TAG, "onDataChange: ${datasnapshot.key}")
                    chatlist!!.setId(datasnapshot.key!!)
//                    Log.d(TAG, "onDataChange: chatlist $chatlist")
//                    Log.d(TAG, "onDataChange: ${chatlist!!.javaClass}.")
                    (usersChatList as ArrayList).add(chatlist!!)
                }
//                Log.d(TAG, "onDataChange: chat ${usersChatList.}")
//                for(each in usersChatList!!){
//                    Log.d(TAG, "onDataChange: chatlist $each")
//                }
//                Log.d(TAG, "onDataChange: &&&&&&&&&&&&&&&&&&&&")
                    retrieveChatList()
             
            }

        })
        return view
    }

    private fun retrieveChatList() {
        mUsers = ArrayList()
        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList).clear()
//                Log.d(TAG, "onDataChange: $p0")
                for (dataSnapshot in p0.children) {
                    
                    
                    val user = dataSnapshot.getValue(Users::class.java)

                    Log.d(TAG, "onDataChange: user ${user!!}")
                    for (eachChat in usersChatList!!) {
                        Log.d(TAG, "onDataChange: each ${eachChat}")
//                        Log.d(TAG, "onDataChange: ${eachChat.getId()}")
                        if (user!!.getUID().equals(eachChat.getId())) {
                            (mUsers as ArrayList).add(user!!)
                        }
//                        Log.d(TAG, "onDataChange: *****************************")
                    }
                }
                userAdapter = UserAdapter(context!!, (mUsers!! as ArrayList<Users>), true)
                recyclerView_chatlist.adapter=userAdapter
            }

        })
    }
}