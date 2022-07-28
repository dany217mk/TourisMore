package space.mosk.tourismore

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import space.mosk.tourismore.models.Message
import space.mosk.tourismore.models.User
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RatingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment(private  var user: User) : Fragment() {
    private lateinit var chat_recycle: RecyclerView

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var backButton : MaterialButton

    var adapter: MessagesAdapter? = null
    var messages: ArrayList<Message>? = null
    var senderRoom: String? = null
    var receiverRoom: String? = null
    var dialog: ProgressDialog? = null
    var msgBox: EditText? = null
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val mStorage: StorageReference = FirebaseStorage.getInstance().reference
    var senderUid: String? = null
    var receiverUid: String? = null

    private lateinit var bottomNavView : BottomNavigationView

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
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        view.findViewById<TextView>(R.id.username_chat_text).text = user.name + " " +  user.surname
        if (user.profileImage != "No Image"){
            Glide.with(view).load(user.profileImage).centerCrop().into(view.findViewById(R.id.user_chat_image))
        } else{
            Glide.with(view).load(R.drawable.profile).fallback(R.drawable.profile).centerCrop().into(view.findViewById(R.id.user_chat_image))
        }
        dialog = ProgressDialog(view.context)
        dialog!!.setMessage("Uploading image...")
        dialog!!.setCancelable(false)
        messages = ArrayList<Message>()

        receiverUid = user.uid
        senderUid = mAuth.uid

        val handler = Handler()
        view.findViewById<EditText>(R.id.messageBox).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()){
                    view.findViewById<ImageView>(R.id.sendBtn).visibility = View.VISIBLE
                } else{
                    view.findViewById<ImageView>(R.id.sendBtn).visibility = View.GONE
                }
                mDatabase.child("presence").child(senderUid!!).setValue("typing...")
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(userStoppedTyping, 1000)
            }
            var userStoppedTyping =
                Runnable {
                    mDatabase.child("presence").child(senderUid!!).setValue("Online")
                }
        })



        mDatabase.child("presence").child(receiverUid!!)
            .addValueEventListener(ValueEventListenerAdapter { snapshot->
                    if (snapshot.exists()) {
                        val status = snapshot.getValue(String::class.java)
                        if (!status!!.isEmpty()) {
                            if (status == "Offline") {
                                view.findViewById<TextView>(R.id.status).visibility = View.GONE
                            } else {
                                view.findViewById<TextView>(R.id.status).text = status
                                view.findViewById<TextView>(R.id.status).visibility = View.VISIBLE
                            }
                        }
                    }
            })

        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid
        adapter = MessagesAdapter(view.context, messages, senderRoom!!, receiverRoom!!)
        chat_recycle = view.findViewById<RecyclerView>(R.id.recyclerView)
        chat_recycle.layoutManager = LinearLayoutManager(view.context)
        chat_recycle.adapter = adapter
        mDatabase.child("chats")
            .child(senderRoom!!)
            .child("messages")
            .addValueEventListener(ValueEventListenerAdapter {snapshot->
                    messages!!.clear()
                    for (snapshot1 in snapshot.children) {
                        val message: Message? = snapshot1.getValue(Message::class.java)
                        message!!.messageId = snapshot1.key
                        messages!!.add(message)
                    }
                    adapter!!.notifyDataSetChanged()
                chat_recycle.scrollToPosition(messages!!.size-1)
            })

        view.findViewById<ImageView>(R.id.sendBtn).setOnClickListener {
            val messageTxt: String = msgBox?.text.toString()
            val date = Date()
            val message = Message(messageTxt, senderUid, date.time)
            msgBox?.setText("")
            val randomKey = mDatabase.push().key
            val lastMsgObj = HashMap<String, Any>()
            lastMsgObj["lastMsg"] = message.message!!
            lastMsgObj["lastMsgTime"] = date.time
            mDatabase.child("chats").child(senderRoom!!).updateChildren(lastMsgObj)
            mDatabase.child("chats").child(receiverRoom!!).updateChildren(lastMsgObj)
            mDatabase.child("chats")
                .child(senderRoom!!)
                .child("messages")
                .child(randomKey!!)
                .setValue(message).addOnSuccessListener {
                    mDatabase.child("chats")
                        .child(receiverRoom!!)
                        .child("messages")
                        .child(randomKey)
                        .setValue(message).addOnSuccessListener {
                            chat_recycle.scrollToPosition(messages!!.size-1)
                        }
                }
        }
        view.findViewById<ImageView>(R.id.attachment).setOnClickListener(View.OnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 25)
        })


        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backButton = view.findViewById(R.id.backBtn1)
        bottomNavView = requireActivity().findViewById(R.id.navigationView)
        bottomNavView.visibility = View.GONE
        msgBox = view.findViewById<EditText>(R.id.messageBox)
        backButton.setOnClickListener{
            bottomNavView.visibility = View.VISIBLE
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.slide_left, R.animator.slide_right)
                .replace(R.id.container, MessagesFragment())
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 25) {
            if (data != null) {
                if (data.data != null) {
                    val selectedImage = data.data
                    val calendar = Calendar.getInstance()
                    val reference = mStorage.child("chats")
                        .child(calendar.timeInMillis.toString() + "")
                    dialog!!.show()
                    reference.putFile(selectedImage!!).addOnCompleteListener { task ->
                        dialog!!.dismiss()
                        if (task.isSuccessful) {
                            reference.downloadUrl.addOnSuccessListener { uri ->
                                val filePath = uri.toString()
                                val messageTxt: String =
                                    msgBox?.text.toString()
                                val date = Date()
                                val message = Message(messageTxt, senderUid, date.time)
                                message.message = "photo"
                                message.imageUrl = filePath
                                msgBox?.text.toString()
                                val randomKey = database!!.reference.push().key
                                val lastMsgObj = HashMap<String, Any>()
                                lastMsgObj["lastMsg"] = message.message!!
                                lastMsgObj["lastMsgTime"] = date.time
                                mDatabase.child("chats").child(senderRoom!!)
                                    .updateChildren(lastMsgObj)
                                mDatabase.child("chats").child(receiverRoom!!)
                                    .updateChildren(lastMsgObj)
                                mDatabase.child("chats")
                                    .child(senderRoom!!)
                                    .child("messages")
                                    .child(randomKey!!)
                                    .setValue(message).addOnSuccessListener {
                                        mDatabase.child("chats")
                                            .child(receiverRoom!!)
                                            .child("messages")
                                            .child(randomKey)
                                            .setValue(message).addOnSuccessListener {
                                                chat_recycle.scrollToPosition(messages!!.size-1)
                                            }
                                    }

                            }
                        }
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        val currentId = mAuth.uid
        mDatabase.child("presence").child(currentId!!).setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        mDatabase.child("presence").child(currentId!!).setValue("Offline")
    }
}