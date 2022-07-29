package space.mosk.tourismore.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import space.mosk.tourismore.GridRecyclerView
import space.mosk.tourismore.R
import space.mosk.tourismore.activities.auth
import space.mosk.tourismore.adapters.ValueEventListenerAdapter
import space.mosk.tourismore.models.User

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RatingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OtherUserFragment(private val user: User, private val actionVal: String) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mUser: User? = null

    private lateinit var bottomNavView : BottomNavigationView

    private lateinit var phtView : RecyclerView

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference

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
        val view = inflater.inflate(R.layout.fragment_other_user, container, false)
        view.findViewById<TextView>(R.id.username_text).text = user.name + " " + user.surname
        if (user?.profileImage != "No Image"){
            Glide.with(view.context).load(user?.profileImage).into(view.findViewById(R.id.profilePicture))
        } else{
            Glide.with(view.context).load(R.drawable.profile).fallback(R.drawable.profile).centerCrop().into(view.findViewById(R.id.profilePicture))
        }

        phtView = view.findViewById(R.id.photoListView)

        val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.devider)
            ?.let { itemDecorator.setDrawable(it) }
        phtView.addItemDecoration(itemDecorator)

        mDatabase.child("users").child(mAuth.uid.toString()).addValueEventListener(ValueEventListenerAdapter{
            mUser = it.getValue(User::class.java)
            if (mUser?.follows?.keys!!.contains(user.uid.toString())){
                view.findViewById<Button>(R.id.follow_btn).visibility = View.GONE
                view.findViewById<Button>(R.id.unfollow_btn).visibility = View.VISIBLE
                view.findViewById<Button>(R.id.msg_user).visibility = View.VISIBLE
            } else{
                view.findViewById<Button>(R.id.follow_btn).visibility = View.VISIBLE
                view.findViewById<Button>(R.id.unfollow_btn).visibility = View.GONE
                view.findViewById<Button>(R.id.msg_user).visibility = View.GONE
            }
            view.findViewById<TextView>(R.id.follows_num_text).text = user?.follows?.size.toString()
            view.findViewById<TextView>(R.id.followers_num_text).text = user?.followers?.size.toString()
            view.findViewById<Button>(R.id.msg_user).setOnClickListener{
                bottomNavView.selectedItemId = R.id.messages
                loadFragment(ChatFragment(user))
            }
            view.findViewById<Button>(R.id.follow_btn).setOnClickListener{
                follow(user.uid.toString(), view)
            }
            view.findViewById<Button>(R.id.unfollow_btn).setOnClickListener{
                unfollow(user.uid.toString(), view)
            }

            mDatabase.child("images").child(user.uid.toString()).addValueEventListener(
                ValueEventListenerAdapter{
                    val photoes = it.children.map{ it.getValue(String::class.java).toString() }.reversed()
                    phtView.adapter = GridRecyclerView(photoes)
                    phtView.layoutManager = GridLayoutManager(context, 3)
                    view.findViewById<TextView>(R.id.countPub).text = phtView.adapter?.itemCount.toString()
                    if (phtView.adapter?.itemCount == 0){
                        view.findViewById<TextView>(R.id.emptyText).visibility = View.VISIBLE
                    } else{
                        view.findViewById<TextView>(R.id.emptyText).visibility = View.GONE
                    }
                })
        })

        return view
    }

    private fun unfollow(uid: String, view: View) {
        val setFollow = mDatabase.child("users").child(mAuth.uid.toString()).child("follows").child(uid).removeValue()
        val setFollowers = mDatabase.child("users").child(uid).child("followers").child(mAuth.uid.toString()).removeValue()
        setFollow.continueWith({setFollowers}).addOnCompleteListener {
            if (it.isSuccessful){
                view.findViewById<Button>(R.id.follow_btn).visibility = View.VISIBLE
                view.findViewById<Button>(R.id.unfollow_btn).visibility = View.GONE
                view.findViewById<Button>(R.id.msg_user).visibility = View.GONE
            }
        }
    }

    private fun follow(uid: String, view: View) {
        val setFollow = mDatabase.child("users").child(mAuth.uid.toString()).child("follows").child(uid).setValue(true)
        val setFollowers = mDatabase.child("users").child(uid).child("followers").child(mAuth.uid.toString()).setValue(true)
        setFollow.continueWith({setFollowers}).addOnCompleteListener {
            if (it.isSuccessful){
                view.findViewById<Button>(R.id.follow_btn).visibility = View.GONE
                view.findViewById<Button>(R.id.unfollow_btn).visibility = View.VISIBLE
                view.findViewById<Button>(R.id.msg_user).visibility = View.VISIBLE
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backButton = view.findViewById(R.id.backBtn1)
        bottomNavView = requireActivity().findViewById(R.id.navigationView)
        if (actionVal == "friends") {
            backButton.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.animator.slide_left, R.animator.slide_right)
                    .replace(R.id.container, FriendsFragment())
                    .commit()
            }
        } else if (actionVal == "chat") {
            backButton.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.animator.slide_left, R.animator.slide_right)
                    .replace(R.id.container, ChatFragment(user))
                    .commit()
            }
        } else if (actionVal == "followers") {
            backButton.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.animator.slide_left, R.animator.slide_right)
                    .replace(R.id.container, MyFollowersFragment())
                    .commit()
            }
        } else if (actionVal == "follows") {
            backButton.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.animator.slide_left, R.animator.slide_right)
                    .replace(R.id.container, MyFollowsFragment("profile"))
                    .commit()
            }
        } else {
            backButton.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.animator.slide_left, R.animator.slide_right)
                    .replace(R.id.container, FriendsFragment())
                    .commit()
            }


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

    private fun loadFragment(fragment: Fragment){
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.slide_left, R.animator.slide_right)
            .replace(R.id.container, fragment)
            .commit()
    }
}