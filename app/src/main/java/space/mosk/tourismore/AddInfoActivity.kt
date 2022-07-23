package space.mosk.tourismore

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import space.mosk.tourismore.databinding.ActivityAddInfoBinding
import space.mosk.tourismore.models.User
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.util.*
import kotlin.collections.HashMap

class AddInfoActivity : AppCompatActivity() {
    var binding: ActivityAddInfoBinding?= null
    var auth: FirebaseAuth?= null
    var database: FirebaseDatabase?= null
    var storage: FirebaseStorage? = null
    var dialog: ProgressDialog?= null
    var selectedImage: Uri?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        supportActionBar?.hide()
        binding = ActivityAddInfoBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        dialog = ProgressDialog(this@AddInfoActivity)

        dialog!!.setCancelable(false)
        dialog!!.setMessage("Create profile")
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        binding!!.imgProfile.setOnClickListener{
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 45)
        }

        binding!!.createProfile.setOnClickListener{
            val name: String = binding!!.editName.text.toString()
            val surname: String = binding!!.editSurname.text.toString()
            val date_of_birth: String = binding!!.editDateNumber.text.toString()
            var counter: Int = 0
            if (name.isNotEmpty() && name.length > 1){
                counter++
            } else{
                binding!!.editName.setError("Имя не соответствует формату")
            }
            if (surname.isNotEmpty() && surname.length > 1){
                counter++
            } else{
                binding!!.editSurname.setError("Фамилия не соответствует формату")
            }
            if (date_of_birth.isNotEmpty() && checkdate(date_of_birth)){
                counter++
            } else{
                binding!!.editDateNumber.setError("Дата не соответствует формату")
            }
            val selectedId: Int = binding!!.radioSex.getCheckedRadioButtonId()
            if (selectedId != -1){
                counter++
            }
            if (counter != 4) {
                MotionToast.createColorToast(this@AddInfoActivity,
                    "Ошибка 😍",
                    "Неверно заполнены поля!",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION, ResourcesCompat.getFont(applicationContext,R.font.helvetica_regular))
                return@setOnClickListener
            }
            val gender: String = findViewById<RadioButton>(selectedId).text.toString()
            dialog!!.show()
            if (selectedImage != null){
                val reference = storage!!.reference.child("Profile")
                    .child(auth!!.uid!!)
                reference.putFile(selectedImage!!).addOnCompleteListener { task->
                    if (task.isSuccessful){
                        reference.downloadUrl.addOnCompleteListener {uri->
                            val imageUrl = uri.toString()
                            val uid = auth!!.uid
                            val phone = auth!!.currentUser!!.phoneNumber
                            val user = User(uid, name, surname, date_of_birth, gender, phone, imageUrl)
                            database!!.reference
                                .child("users")
                                .child(uid!!)
                                .setValue(user)
                                .addOnCompleteListener{
                                    dialog!!.dismiss()
                                    val intent = Intent(applicationContext, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                        }
                    }

                }
            } else{
                val uid = auth!!.uid
                val phone = auth!!.currentUser!!.phoneNumber
                val user = User(uid, name, surname, date_of_birth, gender, phone, "No Image")
                database!!.reference
                    .child("users")
                    .child(uid!!)
                    .setValue(user)
                    .addOnCompleteListener{
                        dialog!!.dismiss()
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
            }
        }

    }

    private fun checkdate(toString: String): Boolean {
        // TODO: 24.07.2022 проверка валидности даты (на вход дата в формате: 12.06.2005)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null){
            if (data.data != null){
                val uri = data.data
                val storage = FirebaseStorage.getInstance()
                val time = Date().time
                val reference = storage.reference
                    .child("Profile")
                    .child(time.toString() + "")
                    reference.putFile(uri!!).addOnCompleteListener { task->
                        if (task.isSuccessful){
                            reference.downloadUrl.addOnCompleteListener{ uri->
                                val filePath =  uri.toString()
                                val obj = HashMap<String,Any>()
                                obj["image"] = filePath
                                database!!.reference
                                    .child("users")
                                    .child(FirebaseAuth.getInstance().uid!!)
                                    .updateChildren(obj).addOnSuccessListener {  }
                            }
                        }
                    }
                binding!!.imgProfile.setImageURI(data.data)
                selectedImage = data.data
            }
        }
    }
}