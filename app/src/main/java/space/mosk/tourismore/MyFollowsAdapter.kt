package space.mosk.tourismore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import space.mosk.tourismore.models.User

class MyFollowsAdapter(private val listener: Listener, private val requireActivity: FragmentActivity) : RecyclerView.Adapter<MyFollowsAdapter.ViewHolder>() {
    private var mPositions = mapOf<String, Int>()
    private var mFollows = mapOf<String, Boolean>()
    private var mUsers = listOf<User>()

    class ViewHolder(val view: View): RecyclerView.ViewHolder(view)

    interface Listener{
        fun unfollow(uid:String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFollowsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_follow_item, parent, false)
        return MyFollowsAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyFollowsAdapter.ViewHolder, position: Int) {
        val user = mUsers[position]
        if (user.profileImage != "No Image"){
            Glide.with(holder.view).load(user.profileImage).centerCrop().into(holder.view.findViewById(R.id.profile_img))
        } else{
            Glide.with(holder.view).load(R.drawable.profile).fallback(R.drawable.profile).centerCrop().into(holder.view.findViewById(R.id.profile_img))
        }
        holder.view.findViewById<TextView>(R.id.username_text).text = user.name + " " + user.surname
        holder.view.findViewById<Button>(R.id.unfollow_btn).setOnClickListener {
            listener.unfollow(user.uid!!)
        }
        holder.view.findViewById<Button>(R.id.chat).setOnClickListener {
            loadFragment(ChatFragment(user))
        }
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    fun update(users: List<User>) {
        mUsers = users
        mPositions = users.withIndex().map { (idx,user)->user.uid!! to idx }.toMap()
        notifyDataSetChanged()
    }

    private fun loadFragment(fragment: Fragment){
        requireActivity.supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.slide_left, R.animator.slide_right)
            .replace(R.id.container, fragment)
            .commit()
    }

}