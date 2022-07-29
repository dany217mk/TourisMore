package space.mosk.tourismore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import space.mosk.tourismore.R
import space.mosk.tourismore.adapters.MyFollowersAdapter
import space.mosk.tourismore.adapters.ValueEventListenerAdapter
import space.mosk.tourismore.models.User

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RatingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyFollowersFragment() : Fragment(), MyFollowersAdapter.Listener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference

    private lateinit var mUsers: List<User>
    private lateinit var mAdapter: MyFollowersAdapter

    private lateinit var followsList: List<String>

    private lateinit var backButton : MaterialButton

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
        val view = inflater.inflate(R.layout.fragment_my_followers, container, false)
        val uid = mAuth.currentUser!!.uid
        mAdapter = MyFollowersAdapter(this, requireActivity())
        val my_follows_recycler = view.findViewById<RecyclerView>(R.id.my_follows_recycler)
        my_follows_recycler.adapter = mAdapter
        my_follows_recycler.layoutManager = LinearLayoutManager(view.context)
        getMyFollowsUsers(uid, view)
        return view
    }

    private fun getMyFollowsUsers(uid: String, view: View) {
        mDatabase.child("users").child(mAuth.uid.toString()).child("followers").addValueEventListener(
            ValueEventListenerAdapter{dataSnapshot->
                followsList = listOf()
                for (snapshot in dataSnapshot.children){
                    followsList = followsList.plus(snapshot.key.toString())!!
                }
                mDatabase.child("users").addValueEventListener(ValueEventListenerAdapter { dataSnapshot ->
                    mUsers = listOf()
                    for (snapshot in dataSnapshot.children){
                        val user = snapshot.getValue(User::class.java)?.copy(uid = snapshot.key.toString())
                        for (id in followsList){
                            if (user!!.uid == id){
                                mUsers = mUsers + user
                            }
                        }
                    }
                    if (mUsers.isEmpty()){
                        view.findViewById<TextView>(R.id.emptyTextFollows)?.visibility = View.VISIBLE
                    } else{
                        view.findViewById<TextView>(R.id.emptyTextFollows)?.visibility = View.GONE
                    }
                    mDatabase.child("users").child(mAuth.uid.toString()).addValueEventListener(ValueEventListenerAdapter{
                        val user = it.getValue(User::class.java)
                        mAdapter.update(mUsers, user!!.follows)
                    })
                })
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backButton = view.findViewById(R.id.backBtn1)
        backButton.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.slide_left, R.animator.slide_right)
                .replace(R.id.container, ProfileFragment())
                .commit()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RatingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RatingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun follow(uid: String) {
        val setFollow = mDatabase.child("users").child(mAuth.uid.toString()).child("follows").child(uid).setValue(true)
        val setFollowers = mDatabase.child("users").child(uid).child("followers").child(mAuth.uid.toString()).setValue(true)
        setFollow.continueWith({setFollowers}).addOnCompleteListener {
            if (it.isSuccessful){
                mAdapter.followed(uid)
            }
        }
    }

    override fun unfollow(uid: String) {
        val setFollow = mDatabase.child("users").child(mAuth.uid.toString()).child("follows").child(uid).removeValue()
        val setFollowers = mDatabase.child("users").child(uid).child("followers").child(mAuth.uid.toString()).removeValue()
        setFollow.continueWith({setFollowers}).addOnCompleteListener {
            if (it.isSuccessful){
                mAdapter.unfollowed(uid)
            }
        }
    }
}