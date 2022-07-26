package space.mosk.tourismore

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        Glide.with(holder.view).load(posts[position].image).centerCrop().into(holder.view.findViewById(R.id.post_image))
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}