package com.shaun.letschat

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.shaun.letschat.Fragments.ChatFragment
import com.shaun.letschat.Fragments.SearchFragment
import com.shaun.letschat.Fragments.SettingsFragment
import com.shaun.letschat.ModelClasses.Chat
import com.shaun.letschat.ModelClasses.Users
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var refUsers: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        Log.d("Main", "firebas $firebaseUser")
        supportActionBar!!.title = ""

//
//
//        viewPagerAdapter.addFragment(SearchFragment(),"Search")
//        viewPagerAdapter.addFragment(SettingsFragment(),"Settings")
//
//
//        view_pager.adapter=viewPagerAdapter
//        tab_layout.setupWithViewPager(view_pager)
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
                var countUnreadMsg = 0
                for (dataSnapshot in snapshot.children) {
                    val chat = dataSnapshot.getValue(Chat::class.java)

                    chat!!.setIseen(dataSnapshot.child("isseen").value.toString().toBoolean())
                    chat.setReciever(dataSnapshot.child("receiver").value.toString())
                    Log.d(TAG, "mainaa: $chat")
                    if (chat.getReciever().equals(firebaseUser!!.uid) && !chat.IsSeen()) {

                        countUnreadMsg++
                    }

                }
                if (countUnreadMsg == 0) {
                    viewPagerAdapter.addFragment(ChatFragment(), "Chats")
                } else {
                    viewPagerAdapter.addFragment(ChatFragment(), "($countUnreadMsg) Chats")

                }
                viewPagerAdapter.addFragment(SearchFragment(), "Search")
                viewPagerAdapter.addFragment(SettingsFragment(), "Settings")
                view_pager.adapter = viewPagerAdapter
                tab_layout.setupWithViewPager(view_pager)
            }

        })

        //Displaying Username
        refUsers!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    Log.d("Main", "User is $user")
                    user_name.text = user!!.getUserName()
                    Picasso.get().load(user.getProfile())
                        .placeholder(R.drawable.profile)
                        .into(profile_image)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                return true
            }

        }
        return false
    }

    internal class ViewPagerAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager) {
        private val fragments: ArrayList<Fragment>
        private val titles: ArrayList<String>

        init {
            fragments = ArrayList<Fragment>()
            titles = ArrayList<String>()
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }
    }

    private fun updateStatus(status: String) {
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        ref.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        updateStatus("online")
    }

    override fun onPause() {
        super.onPause()
        Handler().postDelayed({
            updateStatus("offline")
        }, 500L)

    }
}