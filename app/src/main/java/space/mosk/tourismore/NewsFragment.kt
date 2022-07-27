package space.mosk.tourismore

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import space.mosk.tourismore.R
import space.mosk.tourismore.ValueEventListenerAdapter
import space.mosk.tourismore.models.FeedPost


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class NewsFragment : Fragment() {
    private lateinit var posts: List<FeedPost?>

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        val view = inflater.inflate(R.layout.fragment_news, container, false)
        val feed_recycler = view.findViewById<RecyclerView>(R.id.feed_recycler)



        mDatabase.child("feed-posts").child(mAuth.currentUser!!.uid).addValueEventListener(
            ValueEventListenerAdapter{
                posts = it.children.map { it.getValue(FeedPost::class.java) }
                    .sortedByDescending { it!!.timestampDate() }
                mDatabase.child("users").child(mAuth.uid.toString()).child("follows")
                    .addValueEventListener(ValueEventListenerAdapter{dataSnapshot ->
                    for (snapshot in dataSnapshot.children){
                        mDatabase.child("feed-posts").child(snapshot.key.toString())
                            .addValueEventListener(ValueEventListenerAdapter{
                                posts = posts + it.children.map { it.getValue(FeedPost::class.java) }
                                    .sortedByDescending { it!!.timestampDate() }
                        })
                    }
                        posts.sortedByDescending { it!!.timestampDate()}
                        feed_recycler.adapter = FeedAdapter(posts as List<FeedPost>)
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
}