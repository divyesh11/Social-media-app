package com.example.socialapp

import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.socialapp.daos.PostDao
import kotlinx.android.synthetic.main.activity_create_post.*

class CreatePostActivity : AppCompatActivity() {

    private lateinit var postDao: PostDao
    private lateinit var textinput : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        textinput=findViewById(R.id.postInput)

        postDao = PostDao()

        postButton.setOnClickListener {
            val input = postInput.text.toString().trim()
            if(input.isNotEmpty()) {
                if(input.length <= 2000) {
                    postDao.addPost(input)
                    finish()
                }
                else
                {
                    Toast.makeText(this,"Please Compress Your Post! You cannot Enter more than 300 words",Toast.LENGTH_LONG).show()
                }
            }
        }
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {

    }
}