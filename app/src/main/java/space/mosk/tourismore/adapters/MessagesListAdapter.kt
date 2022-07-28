package space.mosk.tourismore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import space.mosk.tourismore.R
import space.mosk.tourismore.fragments.ChatFragment
import space.mosk.tourismore.models.LastMessage
import space.mosk.tourismore.models.User

class MessagesListAdapter(private val requireActivity: FragmentActivity) : RecyclerView.Adapter<MessagesListAdapter.ViewHolder>() {
    private var lastMessages = listOf<LastMessage>()

    class ViewHolder(val view: View): RecyclerView.ViewHolder(view)

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.last_message_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lastMessage = lastMessages[position]
        mDatabase.child("users").child(lastMessage.user_id.toString()).addValueEventListener(
            ValueEventListenerAdapter{
            val user = it.getValue(User::class.java)
            holder.view.findViewById<TextView>(R.id.info_text).text = lastMessage.lastMsg
            holder.view.findViewById<TextView>(R.id.username_text).text = user?.name + " " + user?.surname
            if (user?.profileImage != "No Image"){
                Glide.with(holder.view).load(user?.profileImage).centerCrop().into(holder.view.findViewById(
                    R.id.profile_img
                ))
            } else{
                Glide.with(holder.view).load(R.drawable.profile).fallback(R.drawable.profile).centerCrop().into(holder.view.findViewById(
                    R.id.profile_img
                ))
            }
            holder.view.findViewById<ConstraintLayout>(R.id.msg_user).setOnClickListener{
                loadFragment(ChatFragment(user!!))
            }
        })

    }

    override fun getItemCount(): Int {
        return lastMessages.size
    }

    private fun loadFragment(fragment: Fragment){
        requireActivity.supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.slide_left, R.animator.slide_right)
            .replace(R.id.container, fragment)
            .commit()
    }

    fun update(lastMessages: List<LastMessage>) {
        this.lastMessages = lastMessages
        notifyDataSetChanged()
    }

}