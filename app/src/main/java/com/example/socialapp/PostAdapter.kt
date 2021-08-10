package com.example.socialapp

import android.opengl.Visibility
import android.text.InputFilter
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialapp.daos.UserDao
import com.example.socialapp.models.Post
import com.example.socialapp.models.User
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.coroutineContext


class PostAdapter(options: FirestoreRecyclerOptions<Post>, val listener: MainActivity) : FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder>(
        options
) {
    class PostViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val postText: TextView = itemView.findViewById(R.id.postTitle)
        val imgpost : ImageView = itemView.findViewById(R.id.post)
        val userText: TextView = itemView.findViewById(R.id.userName)
        val createdAt: TextView = itemView.findViewById(R.id.createdAt)
        val likeCount: TextView = itemView.findViewById(R.id.likeCount)
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val likeButton: ImageView = itemView.findViewById(R.id.likeButton)
        val commentCount : TextView =itemView.findViewById(R.id.CommentCount)
        val editText : EditText=itemView.findViewById(R.id.comment)
        var postbutton: ImageView= itemView.findViewById(R.id.postbutton)
        var commentuserimage : ImageView = itemView.findViewById(R.id.commentuserimage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val viewHolder =  PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false))

        viewHolder.likeButton.setOnClickListener{
            listener.onLikeClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        viewHolder.postbutton.setOnClickListener {
            val commentinput=viewHolder.editText.text.toString()
            if(commentinput.isNotEmpty())
            {
                if(commentinput.length <= 300) {
                    listener.onCommentButton(snapshots.getSnapshot(viewHolder.adapterPosition).id, commentinput)
                    viewHolder.editText.text.clear()
                }
                else
                {
                    Toast.makeText(this.listener,"Please Compress Your Post! You cannot Enter more than 40 words",Toast.LENGTH_LONG).show()
                }
            }
            else {
                Toast.makeText(parent.context, "Please Enter Your comment", Toast.LENGTH_SHORT).show()
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
        holder.postText.text = model.text
        holder.userText.text = model.createdBy.displayName
        Glide.with(holder.userImage.context).load(model.createdBy.imageUrl).circleCrop().into(holder.userImage)
        holder.likeCount.text = model.likedBy.size.toString()
        holder.commentCount.text = model.commentedBy.size.toString()
        holder.createdAt.text = Utils.getTimeAgo(model.createdAt)

        if(model.url!=null)
        {
//            holder.postText.visibility =View.GONE
            Glide.with(holder.imgpost.context).load(model.url).circleCrop().into(holder.imgpost)
            holder.imgpost.visibility = View.VISIBLE
        }
        else
        {
            holder.imgpost.visibility = View.GONE
        }


        val auth = Firebase.auth
        val currentUserId = auth.currentUser!!.uid

        var currentuser = User()
        GlobalScope.launch (Dispatchers.Main){
            currentuser=UserDao().getUserById(currentUserId).await().toObject(User::class.java)!!
            Glide.with(holder.commentuserimage.context).load(currentuser.imageUrl).into(holder.commentuserimage)
        }

        val isLiked = model.likedBy.contains(currentUserId)
        if(isLiked) {
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context, R.drawable.ic_liked))
        } else {
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context, R.drawable.ic_unliked))
        }
    }

}

interface IPostAdapter {
    fun onLikeClicked(postId: String)
    fun onCommentButton(postId: String, text: String)
}