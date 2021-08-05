package com.example.socialapp


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialapp.daos.PostDao
import com.example.socialapp.daos.UserDao
import com.example.socialapp.models.Post
import com.example.socialapp.models.User
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class MainActivity : AppCompatActivity(), IPostAdapter {

    private lateinit var postDao: PostDao
    private lateinit var adapter: PostAdapter
    private lateinit var auth : FirebaseAuth
    val TAG="MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener{
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivity(intent)
        }
        auth= FirebaseAuth.getInstance()

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        postDao = PostDao()
        val postsCollections = postDao.postCollections
        val query = postsCollections.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()

        adapter = PostAdapter(recyclerViewOptions, this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onLikeClicked(postId: String) {
        postDao.updateLikes(postId)
    }

    override fun onCommentButton(postId: String, text: String) {
        postDao.updateComments(postId,text)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.sign_out, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

//        Firebase.auth.signOut()
        val id=item.title
        Log.d("MainActivity.this",id.toString())
        if(id==getString(R.string.profile))
        {
//            val intent=Intent(this,prpfile_page_activity::class.java)
//            startActivity(intent)
//            finish()
            profile()
        }
        else
        {
            AuthUI.getInstance().signOut(this).addOnCompleteListener {
                if(it.isSuccessful)
                {
                    Toast.makeText(this,"You Are Successfully Sign Out",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this,"Error Occured! Please Try Again",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        return true
    }

    fun profile()
    {
        var user : User = User()
        GlobalScope.launch {
            val currentUserId = auth.currentUser!!.uid
            user=UserDao().getUserById(currentUserId).await().toObject(User::class.java)!!
        }
        val intent=Intent(this,prpfile_page_activity::class.java)
        var bundle : Bundle = Bundle()
        bundle.putString("username",user.displayName.toString())
        bundle.putString("userimg",user.imageUrl)
        Log.d(TAG,user.displayName.toString())
        Log.d(TAG,user.imageUrl)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}


