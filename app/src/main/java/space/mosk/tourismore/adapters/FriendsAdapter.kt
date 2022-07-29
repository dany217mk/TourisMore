package space.mosk.tourismore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import space.mosk.tourismore.R
import space.mosk.tourismore.fragments.OtherUserFragment
import space.mosk.tourismore.models.User

class FriendsAdapter(private val listener: Listener, private val requireActivity: FragmentActivity) : RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {
    private var mPositions = mapOf<String, Int>()
    private var mFollows = mapOf<String, Boolean>()
    private var mUsers = listOf<User>()

    class ViewHolder(val view: View): RecyclerView.ViewHolder(view)

    interface Listener{
        fun follow(uid: String)
        fun unfollow(uid:String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friend_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = mUsers[position]
        if (user.profileImage != "No Image"){
            Glide.with(holder.view).load(user.profileImage).centerCrop().into(holder.view.findViewById(
                R.id.profile_img
            ))
        } else{
            Glide.with(holder.view).load(R.drawable.profile).fallback(R.drawable.profile).centerCrop().into(holder.view.findViewById(
                R.id.profile_img
            ))
        }
        holder.view.findViewById<TextView>(R.id.username_text).text = user.name + " " + user.surname
        holder.view.findViewById<Button>(R.id.follow_btn).setOnClickListener {
            listener.follow(user.uid!!)
        }
        holder.view.findViewById<Button>(R.id.unfollow_btn).setOnClickListener {
            listener.unfollow(user.uid!!)
        }
        holder.view.findViewById<LinearLayout>(R.id.friend_btn).setOnClickListener{
            loadFragment(OtherUserFragment(user, "friends"))
        }
        if (mFollows[user.uid] ?: false){
            holder.view.findViewById<Button>(R.id.follow_btn).visibility = View.GONE
            holder.view.findViewById<Button>(R.id.unfollow_btn).visibility = View.VISIBLE
        } else{
            holder.view.findViewById<Button>(R.id.follow_btn).visibility = View.VISIBLE
            holder.view.findViewById<Button>(R.id.unfollow_btn).visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
       return mUsers.size
    }

    fun update(users: List<User>, follows: Map<String, Boolean>) {
        mUsers = users
        mPositions = users.withIndex().map { (idx,user)->user.uid!! to idx }.toMap()
        mFollows = follows
        notifyDataSetChanged()
    }

    fun followed(uid: String) {
        mFollows += (uid to true)
        notifyItemChanged(mPositions[uid]!!)
    }

    fun unfollowed(uid: String) {
        mFollows -= uid
        notifyItemChanged(mPositions[uid]!!)
    }

    private fun loadFragment(fragment: Fragment){
        requireActivity.supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.slide_left, R.animator.slide_right)
            .replace(R.id.container, fragment)
            .commit()
    }
}