package space.mosk.tourismore

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.google.ar.sceneform.ux.InstructionsController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.coroutineScope
import space.mosk.tourismore.models.User

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment(), profilePicsClick {

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

        mDatabase.child("images").child(auth?.currentUser!!.uid).addValueEventListener(ValueEventListenerAdapter{
            val photoes = it.children.map{ it.getValue(String::class.java).toString() }.reversed()
            phtView.adapter = GridRecyclerView(photoes, this@ProfileFragment)
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
                }
                view.findViewById<TextView>(R.id.follows_num_text).text = user?.follows?.size.toString()
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

    override fun onClick(pos: Int) {
        val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
        mDatabase.child("images").child(auth?.currentUser!!.uid).addValueEventListener(ValueEventListenerAdapter{
            val photoes = it.children.map{ it.getValue(String::class.java).toString() }.reversed()
            val imgDialog = ShowImageDialog(photoes[pos])
            imgDialog.show(requireActivity().supportFragmentManager, "img")
        })
    }


}