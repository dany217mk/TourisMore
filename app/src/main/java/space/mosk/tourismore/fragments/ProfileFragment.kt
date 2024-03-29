package space.mosk.tourismore.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import space.mosk.tourismore.AddPubFragment
import space.mosk.tourismore.GridRecyclerView
import space.mosk.tourismore.activities.AuthActivity
import space.mosk.tourismore.models.User
import space.mosk.tourismore.R
import space.mosk.tourismore.activities.auth
import space.mosk.tourismore.adapters.ValueEventListenerAdapter

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private var user: User? = null
    private lateinit var nameSurname : TextView
    private lateinit var profilePic : CircleImageView
    private lateinit var phtView : RecyclerView
    private var storage: FirebaseStorage? = null

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()


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
        var view: View = inflater.inflate(R.layout.fragment_profile, container, false)
        nameSurname = view.findViewById(R.id.name)
        profilePic = view.findViewById(R.id.profilePicture)
        phtView = view.findViewById(R.id.photoListView)
        phtView.layoutManager = GridLayoutManager(context, 3)
        val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.devider)
            ?.let { itemDecorator.setDrawable(it) }
        phtView.addItemDecoration(itemDecorator)
        val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference

        mDatabase.child("images").child(auth?.currentUser!!.uid).addValueEventListener(
            ValueEventListenerAdapter{
            val photoes = it.children.map{ it.getValue(String::class.java).toString() }.reversed()
            phtView.adapter = GridRecyclerView(photoes)
            view.findViewById<TextView>(R.id.countPub).text = phtView.adapter?.itemCount.toString()
            if (phtView.adapter?.itemCount == 0){
                view.findViewById<TextView>(R.id.emptyText).visibility = View.VISIBLE
            } else{
                view.findViewById<TextView>(R.id.emptyText).visibility = View.GONE
            }
        })
        view.findViewById<MaterialButton>(R.id.addPubBtn).setOnClickListener{
            loadFragment(AddPubFragment())
        }


        view.findViewById<Button>(R.id.signout).setOnClickListener {
            mAuth.signOut()
            val intent: Intent = Intent(view.context, AuthActivity::class.java)
            startActivity(intent)
        }

        var ref = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().currentUser!!.uid)

        ref.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)
                nameSurname.text = user?.name + " " + user?.surname

                if (user?.profileImage != "No Image"){
                    Glide.with(context!!).load(user?.profileImage).into(profilePic)
                } else{
                    Glide.with(view.context).load(R.drawable.profile).fallback(R.drawable.profile).centerCrop().into(profilePic)
                }
                view.findViewById<TextView>(R.id.follows_num_text).text = user?.follows?.size.toString()

                view.findViewById<LinearLayout>(R.id.follows_btn).setOnClickListener{
                    loadFragment(MyFollowsFragment("profile"))
                }

                view.findViewById<LinearLayout>(R.id.followers_btn).setOnClickListener{
                    loadFragment(MyFollowersFragment())
                }

                view.findViewById<TextView>(R.id.followers_num_text).text = user?.followers?.size.toString()
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
        return view
    }

    private fun loadFragment(fragment: Fragment){
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.slide_left, R.animator.slide_right)
            .replace(R.id.container, fragment)
            .commit()
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}