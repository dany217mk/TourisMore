package space.mosk.tourismore

import android.app.backup.BackupManager.dataChanged
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.TextKeyListener.clear
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import space.mosk.tourismore.models.User


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FriendsFragment : Fragment(), FriendsAdapter.Listener {




    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference

    private lateinit var mUser: User
    private lateinit var mUsers: List<User>
    private lateinit var mAdapter: FriendsAdapter

    private lateinit var srvsName : TextView
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
        val view = inflater.inflate(R.layout.fragment_friends, container, false)
        val uid = mAuth.currentUser!!.uid
        mAdapter = FriendsAdapter(this)
        val friends_recycler = view.findViewById<RecyclerView>(R.id.friends_recycler)
        friends_recycler.adapter = mAdapter
        friends_recycler.layoutManager = LinearLayoutManager(view.context)
        getAllUsers(uid)


        view.findViewById<EditText>(R.id.search_users).addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isNotEmpty()){
                    val query = mDatabase
                        .child("users")
                        .orderByChild("surname")
                        .startAt(p0.toString())
                        .endAt(p0.toString() + "\uf8ff")
                    query.addValueEventListener(ValueEventListenerAdapter{ dataSnapshot ->
                        mUsers = emptyList()
                        for (snapshot in dataSnapshot.children){
                            val user = snapshot.getValue(User::class.java)
                            if (user != null){
                                mUsers = mUsers + user
                            }
                        }
                        if (mUsers.isEmpty()){
                            view.findViewById<TextView>(R.id.emptyTextFriends).visibility = View.VISIBLE
                        } else{
                            view.findViewById<TextView>(R.id.emptyTextFriends).visibility = View.GONE
                        }
                        mAdapter.update(mUsers, mUser.follows)
                    })
                } else{
                    view.findViewById<TextView>(R.id.emptyTextFriends).visibility = View.GONE
                    getAllUsers(mAuth.currentUser!!.uid)
                }
            }

        })

        return view
    }

    private fun getAllUsers(uid: String) {
        mDatabase.child("users").addValueEventListener(ValueEventListenerAdapter{
            val allUsers = it.children.map { it.getValue(User::class.java)!!.copy(uid = it.key) }
            allUsers.partition { it.uid == uid }
            val (userList, otherUsersList) = allUsers.partition { it.uid == uid }
            mUser = userList.first()
            mUsers = otherUsersList

            mAdapter.update(mUsers, mUser.follows)

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        srvsName = view.findViewById(R.id.srvsName1)
        backButton = view.findViewById(R.id.backBtn1)
        backButton.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.slide_left, R.animator.slide_right)
                .replace(R.id.container, ServicesFragment())
                .commit()
        }
        val model = ViewModelProvider(requireActivity()).get(ShareBetweenFragments::class.java)
        model.name.observe(viewLifecycleOwner, Observer{
            srvsName.text = it
        })
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FriendsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun follow(uid: String) {
        val setFollow = mDatabase.child("users").child(mUser.uid.toString()).child("follows").child(uid).setValue(true)
        val setFollowers = mDatabase.child("users").child(uid).child("followers").child(mUser.uid.toString()).setValue(true)
       /* mDatabase.child("feed-posts").child(uid).addListenerForSingleValueEvent(ValueEventListenerAdapter{
            val postsMap = it.children.map { it.key to it.value}.toMap()
            mDatabase.child("feed-posts").child(mUser.uid!!).updateChildren(postsMap)
        })*/
        setFollow.continueWith({setFollowers}).addOnCompleteListener {
            if (it.isSuccessful){
                mAdapter.followed(uid)
            }
        }
    }

    override fun unfollow(uid: String) {
        val setFollow = mDatabase.child("users").child(mUser.uid.toString()).child("follows").child(uid).removeValue()
        val setFollowers = mDatabase.child("users").child(uid).child("followers").child(mUser.uid.toString()).removeValue()
        /*mDatabase.child("feed-posts").child(uid).addListenerForSingleValueEvent(ValueEventListenerAdapter{
            val postsMap = it.children.map { it.key  to null }.toMap()
            mDatabase.child("feed-posts").child(mUser.uid!!).updateChildren(postsMap)
        })*/
        setFollow.continueWith({setFollowers}).addOnCompleteListener {
            if (it.isSuccessful){
                mAdapter.unfollowed(uid)
            }
        }
    }
}


