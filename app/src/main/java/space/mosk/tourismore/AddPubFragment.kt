package space.mosk.tourismore

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import space.mosk.tourismore.models.FeedPost
import space.mosk.tourismore.models.User
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

var imageUri: Uri?= null

private  var imageDownloadUrl: String? = null

/**
 * A simple [Fragment] subclass.
 * Use the [RatingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddPubFragment : Fragment() {

    private lateinit var backButton : MaterialButton
    private lateinit var bottomNavView : BottomNavigationView
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var dialog: ProgressDialog?= null

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val mStorage: StorageReference = FirebaseStorage.getInstance().reference
    private lateinit var model : ShareBetweenFragments

    private lateinit var mUser: User

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
        val view = inflater.inflate(R.layout.fragment_add_pub, container, false)
        dialog = ProgressDialog(view.context)
        dialog!!.setMessage("Запись публикуется...")
        dialog!!.setCancelable(false)
        mDatabase.child("users").child(mAuth.currentUser!!.uid).addValueEventListener(
            ValueEventListenerAdapter{
                mUser = it.getValue(User::class.java)!!
            })
        view.findViewById<ImageView>(R.id.post_image).setOnClickListener{
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 45)
        }
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent, 45)
        view.findViewById<MaterialButton>(R.id.shareBtn).setOnClickListener{
            if (imageUri != null){
                dialog!!.show()
                val reference = mStorage.child("users").child(mAuth.currentUser!!.uid).child("images").child(
                    imageUri!!.lastPathSegment.toString())
                mStorage.child("users").child(mAuth.currentUser!!.uid).child("images").child(
                    imageUri!!.lastPathSegment.toString()).putFile(imageUri!!).addOnCompleteListener {
                    if (it.isSuccessful){
                        imageDownloadUrl = reference.downloadUrl.toString()
                        reference.downloadUrl.addOnCompleteListener{ uri->
                            imageDownloadUrl = uri.result.toString()
                            mDatabase.child("images")
                                .child(mAuth.currentUser!!.uid)
                                .push()
                                .setValue(imageDownloadUrl)
                                .addOnCompleteListener{
                                    if (it.isSuccessful){
                                        mDatabase.child("feed-posts")
                                            .push().setValue(
                                                FeedPost(
                                                uid = mAuth.currentUser!!.uid,
                                                name = mUser.name.toString(),
                                                surname = mUser.surname.toString(),
                                                image = imageDownloadUrl.toString(),
                                                caption = view.findViewById<EditText>(R.id.title_input).text.toString(),
                                                profileImage = mUser.profileImage.toString()
                                            )
                                            ).addOnCompleteListener{
                                                if (it.isSuccessful){
                                                    model = ViewModelProvider(requireActivity()).get(
                                                        ShareBetweenFragments::class.java)
                                                    loadFragment(ProfileFragment())
                                                    bottomNavView.selectedItemId = R.id.profile
                                                    dialog!!.dismiss()
                                                }
                                            }
                                    }
                                }
                        }
                    }
                }
            } else{
                MotionToast.createColorToast(requireActivity(),
                    "Упс",
                    "Вы не выбрали изображение :(",
                    MotionToastStyle.WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION, ResourcesCompat.getFont(view.context,
                        R.font.helvetica_regular
                    ))
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backButton = view.findViewById(R.id.backBtn2)
        bottomNavView = requireActivity().findViewById(R.id.navigationView)
        backButton.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.slide_left, R.animator.slide_right)
                .replace(R.id.container, ProfileFragment())
                .commit()
        }

    }

    private fun loadFragment(fragment: Fragment){
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.slide_left, R.animator.slide_right)
            .replace(R.id.container, fragment)
            .commit()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null){
            if (data.data != null){
                view?.findViewById<ImageView>(R.id.post_image)?.setImageURI(data.data)
                imageUri = data.data
            }
        }
    }


}