package space.mosk.tourismore

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import space.mosk.tourismore.models.User

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
        phtView.adapter = GridRecyclerView(createImages())
        //storage = FirebaseStorage.getInstance()
        var ref = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().currentUser!!.uid)

        ref.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)
                nameSurname.text = user?.name + " " + user?.surname
                Glide.with(context!!).load("https://firebasestorage.googleapis.com/v0/b/tourismore-13be3.appspot.com/o/Profile%2F1658819534682?alt=media&token=9f964c3b-121b-48ed-8e75-221a4c4eb8c3")
                    .into(profilePic)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return view
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