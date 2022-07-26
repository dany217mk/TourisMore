package space.mosk.tourismore

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import space.mosk.tourismore.R
import space.mosk.tourismore.ValueEventListenerAdapter
import space.mosk.tourismore.models.FeedPost
import space.mosk.tourismore.models.User
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AddRecordFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var srvsName : TextView
    private lateinit var backButton : MaterialButton
    private lateinit var imageView: ImageView
    private lateinit var mUser: User
    private lateinit var model : ShareBetweenFragments

    var dialog: ProgressDialog?= null

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val mStorage: StorageReference = FirebaseStorage.getInstance().reference

    private var imageUri: Uri? = null
    private val REQUEST_CODE = 1
    private val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)


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
        var view = inflater.inflate(R.layout.fragment_add_record, container, false)
        imageView = view.findViewById(R.id.post_image)
        dialog = ProgressDialog(view.context)
        dialog!!.setMessage("Выкладываем запись")
        dialog!!.setCancelable(false)
        mDatabase.child("users").child(mAuth.currentUser!!.uid).addValueEventListener(
            ValueEventListenerAdapter{
            mUser = it.getValue(User::class.java)!!
        })
        takeCameraPicture()
        view.findViewById<Button>(R.id.shareBtn).setOnClickListener {
            if (imageUri != null){
                dialog!!.show()
                mStorage.child("users").child(mAuth.currentUser!!.uid).child("images").child(
                    imageUri!!.lastPathSegment.toString()).putFile(imageUri!!).addOnCompleteListener {task ->
                        if (task.isSuccessful){
                            val imageDownloadUrl = mStorage.child("users")
                                .child(mAuth.currentUser!!.uid).child("images")
                                .child(imageUri!!.lastPathSegment.toString()).downloadUrl.toString()
                            mDatabase.child("images")
                                .child(mAuth.currentUser!!.uid)
                                .push()
                                .setValue(imageDownloadUrl)
                                .addOnCompleteListener{
                                        if (it.isSuccessful){
                                            mDatabase.child("feed-posts")
                                                .child(mAuth.currentUser!!.uid)
                                                .push().setValue(FeedPost(
                                                    uid = mAuth.currentUser!!.uid,
                                                    name = mUser.name.toString(),
                                                    surname = mUser.surname.toString(),
                                                    image = imageDownloadUrl,
                                                    caption = view.findViewById<EditText>(R.id.title_input).text.toString(),
                                                    profileImage = mUser.profileImage.toString()
                                                )).addOnCompleteListener{
                                                    if (it.isSuccessful){
                                                        model = ViewModelProvider(requireActivity()).get(
                                                            ShareBetweenFragments::class.java)
                                                        loadFragment(ProfileFragment())
                                                        dialog!!.dismiss()
                                                    }
                                                }
                                        }
                                }
                        }
                }
            }
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Glide.with(this).load(imageUri).centerCrop().into(imageView)
        }else{
            loadFragment(ServicesFragment())
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        srvsName = view.findViewById(R.id.srvsName2)
        backButton = view.findViewById(R.id.backBtn2)
        backButton.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.slide_left, R.animator.slide_right)
                .replace(R.id.container, ServicesFragment())
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

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddRecordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun takeCameraPicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            val imageFile = createImageFile()
            imageUri = FileProvider.getUriForFile(requireActivity(),
                "space.mosk.tourismore.fileprovider",
                imageFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    private fun createImageFile(): File {
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${simpleDateFormat.format(Date())}_",
            ".jpg",
            storageDir
        )
    }


}