package space.mosk.tourismore.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import pl.droidsonroids.gif.GifImageView
import space.mosk.tourismore.R
import space.mosk.tourismore.adapters.MessagesListAdapter
import space.mosk.tourismore.adapters.ValueEventListenerAdapter
import space.mosk.tourismore.models.LastMessage
import space.mosk.tourismore.models.Message
import space.mosk.tourismore.models.User

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class MessagesFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var followsList: List<String>
    private lateinit var lastMessages:  List<LastMessage>
    private lateinit var mAdapter: MessagesListAdapter
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_messages, container, false)
        view.findViewById<Button>(R.id.addChatBtn).setOnClickListener {
            loadFragment(MyFollowsFragment())
        }

        val uid = mAuth.currentUser!!.uid
        mAdapter = MessagesListAdapter(requireActivity())
        val messages_recycler = view.findViewById<RecyclerView>(R.id.messages_recycler)
        messages_recycler.adapter = mAdapter
        messages_recycler.layoutManager = LinearLayoutManager(view.context)
        getChatUsers(uid, view)
        view.findViewById<GifImageView>(R.id.preload).visibility = View.GONE


        return view
    }

    private fun getChatUsers(uid: String, view: View?) {
        mDatabase.child("users").child(mAuth.uid.toString()).child("followers")
            .addValueEventListener(ValueEventListenerAdapter{ dataSnapshot->
                followsList = listOf()
                for (snapshot in dataSnapshot.children){
                    if (!followsList.contains(snapshot.key.toString())){
                        followsList = followsList.plus(snapshot.key.toString())
                    }
                }
                mDatabase.child("users").child(mAuth.uid.toString()).child("follows")
                    .addValueEventListener(ValueEventListenerAdapter { dataSnapshot ->
                        for (snapshot in dataSnapshot.children){
                            if (!followsList.contains(snapshot.key.toString())){
                                followsList = followsList.plus(snapshot.key.toString())
                            }
                        }
                        mDatabase.child("chats").addValueEventListener(ValueEventListenerAdapter { dataSnapshot ->
                            lastMessages = listOf()
                            for (snapshot in dataSnapshot.children){
                                val lastMessage = snapshot.getValue(LastMessage::class.java)?.copy(uid = snapshot.key.toString())
                                for (id in followsList){
                                    if (lastMessage!!.uid == mAuth.uid.toString() + id){
                                        lastMessage.user_id = id
                                        lastMessages = lastMessages + lastMessage
                                    }
                                }
                            }
                            if (lastMessages.isEmpty()){
                                view?.findViewById<TextView>(R.id.emptyTextMessages)?.visibility = View.VISIBLE
                            } else{
                                view?.findViewById<TextView>(R.id.emptyTextMessages)?.visibility = View.GONE
                            }
                            mAdapter.update(lastMessages.sortedByDescending { it.timestampDate() })
                        })
                    })
        })
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MessagesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun loadFragment(fragment: Fragment){
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.slide_left, R.animator.slide_right)
            .replace(R.id.container, fragment)
            .commit()
    }
}
