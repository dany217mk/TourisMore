package space.mosk.tourismore


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import space.mosk.tourismore.fragments.NewsFragment
import space.mosk.tourismore.models.FeedPost


class FeedAdapter(private val listener: Listener, private val posts: List<FeedPost>) : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private  var postLikes: Map<Int, NewsFragment.FeedPostLikes> = emptyMap()

    interface Listener{
        fun toggleLike(postId: String)
        fun loadLikes(id: String, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_item, parent, false)
        return ViewHolder(view)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        val likes = postLikes[position] ?: NewsFragment.FeedPostLikes(0, false)
        holder.view.findViewById<TextView>(R.id.username).text = post.name + " " + post.surname
        if (likes.likesCount == 0){
            holder.view.findViewById<TextView>(R.id.like_text).visibility = View.GONE
        } else {
            holder.view.findViewById<TextView>(R.id.like_text).visibility = View.VISIBLE
            holder.view.findViewById<TextView>(R.id.like_text).text = likes.likesCount.toString() + " likes"
        }

        holder.view.findViewById<ImageView>(R.id.like_img).setOnClickListener{
            listener.toggleLike(post.id)
        }
        holder.view.findViewById<ImageView>(R.id.like_img).setImageResource(if (likes.likes) R.drawable.ic_baseline_favorite else R.drawable.ic_baseline_favorite_border)

        listener.loadLikes(post.id, position)


        holder.view.findViewById<TextView>(R.id.title_text).text = post.caption
        if (post.profileImage != "No Image"){
            Glide.with(holder.view).load(post.profileImage).centerCrop().into(holder.view.findViewById(R.id.user_photo_image))
        } else{
            Glide.with(holder.view).load(R.drawable.profile).fallback(R.drawable.profile).centerCrop().into(holder.view.findViewById(R.id.user_photo_image))
        }
        Glide.with(holder.view).load(post.image).centerCrop().into(holder.view.findViewById(R.id.post_image))
        holder.view.findViewById<ImageView>(R.id.location_img).setOnClickListener {
            val fragment = FeedMarkerFragment()
            val bundle : Bundle = Bundle()
            fragment.arguments = bundle
            bundle.putDouble("lng", post.longitude.toDouble())
            bundle.putDouble("lat", post.latitude.toDouble())
            (holder.view.context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun updatePostLikes(position: Int, likes: NewsFragment.FeedPostLikes) {
        postLikes += (position to likes)
        notifyItemChanged(position)
    }
}