package com.example.socialapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.socialapp.daos.PostDao
import com.example.socialapp.daos.UserDao
import com.example.socialapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class prpfile_page_activity : AppCompatActivity() {

    private lateinit var username:EditText
    private lateinit var postcount : TextView
    private lateinit var user_profile_image:ImageView
    val TAG : String ="prpfile_page_activity"
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prpfile_page_activity)

        var user: User = User()
        auth = FirebaseAuth.getInstance()
        GlobalScope.launch {
            username = findViewById(R.id.username)
            user_profile_image = findViewById(R.id.imageView)
            val currentUserId = auth.currentUser!!.uid
            user = UserDao().getUserById(currentUserId).await().toObject(User::class.java)!!
            GlobalScope.launch(Dispatchers.Main)
            {
                username.text =Editable.Factory.getInstance().newEditable(user.displayName)
                Glide.with(user_profile_image.context).load(user.imageUrl).into(user_profile_image)
            }
        }
    }
}