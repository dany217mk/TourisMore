package space.mosk.tourismore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import space.mosk.tourismore.FeedAdapter
import space.mosk.tourismore.R
import space.mosk.tourismore.adapters.ValueEventListenerAdapter
import space.mosk.tourismore.models.FeedPost


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class NewsFragment : Fragment(), FeedAdapter.Listener {
    private lateinit var mAdapter: FeedAdapter
    private lateinit var posts: List<FeedPost?>

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference

    private lateinit var followsList: List<String>

    private var mLikesListener: Map<String, ValueEventListener> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    data class FeedPostLikes(
        val likesCount: Int,
        val likes: Boolean)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)
        val feed_recycler = view.findViewById<RecyclerView>(R.id.feed_recycler)


        mDatabase.child("users").child(mAuth.uid.toString()).child("follows")
            .addValueEventListener(ValueEventListenerAdapter{ dataSnapshot ->
                followsList = listOf()
                followsList = followsList.plus(mAuth.uid.toString())
                for (snapshot in dataSnapshot.children){
                    followsList = followsList.plus(snapshot.key.toString())!!
                }
                val postsRef = mDatabase.child("feed-posts")
                postsRef.addValueEventListener(ValueEventListenerAdapter{dataSnapshot->
                    posts = listOf()

                    for (snapshot in dataSnapshot.children){
                        val post = snapshot.getValue(FeedPost::class.java)?.copy(id = snapshot.key.toString())
                        for (id in followsList){
                            if (post!!.uid == id){
                                posts = posts + post
                            }
                        }
                    }
                    mAdapter = FeedAdapter(this, posts.asReversed() as List<FeedPost>)
                    feed_recycler.adapter = mAdapter
                    feed_recycler.layoutManager = LinearLayoutManager(view.context)
                    if (feed_recycler.adapter?.itemCount == 0){
                        view.findViewById<TextView>(R.id.emptyTextNews).visibility = View.VISIBLE
                    } else{
                        view.findViewById<TextView>(R.id.emptyTextNews).visibility = View.GONE
                    }
                })
            })
        return view
    }



    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun toggleLike(postId: String) {
        val ref = mDatabase.child("likes").child(postId).child(mAuth.uid!!)

            ref.addListenerForSingleValueEvent(ValueEventListenerAdapter{
                if (it.exists()){
                    ref.removeValue()
                } else{
                    ref.setValue(true)
                }
            })
    }

    override fun loadLikes(postId: String, position: Int) {
        fun createListener() =
            mDatabase.child("likes").child(postId).addValueEventListener(ValueEventListenerAdapter{
                val usersLikes = it.children.map {it.key}.toSet()
                val postLikes = FeedPostLikes(usersLikes.size, usersLikes.contains(mAuth.uid))
                mAdapter.updatePostLikes(position, postLikes)
            })
        if (mLikesListener[postId] == null){
            mLikesListener += (postId to createListener())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mLikesListener.values.forEach { mDatabase.removeEventListener(it) }
    }
}