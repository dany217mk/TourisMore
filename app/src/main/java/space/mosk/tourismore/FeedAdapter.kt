package space.mosk.tourismore

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import space.mosk.tourismore.models.FeedPost

class FeedAdapter(private val posts: List<FeedPost>) : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        holder.view.findViewById<TextView>(R.id.username).text = post.name + " " + post.surname
        if (post.likesCount == 0){
            holder.view.findViewById<TextView>(R.id.like_text).visibility = View.GONE
        } else {
            holder.view.findViewById<TextView>(R.id.like_text).visibility = View.VISIBLE
            holder.view.findViewById<TextView>(R.id.like_text).text = post.likesCount.toString() + " likes"
        }

        holder.view.findViewById<ImageView>(R.id.like_img).setOnClickListener{

        }


        holder.view.findViewById<TextView>(R.id.title_text).text = post.caption
        if (post.profileImage != "No Image"){
            Glide.with(holder.view).load(post.profileImage).centerCrop().into(holder.view.findViewById(R.id.user_photo_image))
        } else{
            Glide.with(holder.view).load(R.drawable.profile).fallback(R.drawable.profile).centerCrop().into(holder.view.findViewById(R.id.user_photo_image))
        }
        Glide.with(holder.view).load(post.image).centerCrop().into(holder.view.findViewById(R.id.post_image))
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}