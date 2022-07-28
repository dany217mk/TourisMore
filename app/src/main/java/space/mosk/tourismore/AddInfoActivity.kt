package space.mosk.tourismore

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RadioButton
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
    var mDateSetListener: DatePickerDialog.OnDateSetListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        supportActionBar?.hide()
        binding = ActivityAddInfoBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        dialog = ProgressDialog(this@AddInfoActivity)

        dialog!!.setCancelable(false)
        dialog!!.setMessage("Создание профиля")
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        binding!!.editDateNumber.setOnClickListener{
            val cal: Calendar= Calendar.getInstance()
            var year: Int = cal.get(Calendar.YEAR)
            var month: Int = cal.get(Calendar.MONTH)
            var day: Int = cal.get(Calendar.DAY_OF_MONTH)
            val dialogPickerDate: DatePickerDialog = DatePickerDialog(
                this@AddInfoActivity,
                R.style.Base_Theme_AppCompat_Light_Dialog,
                mDateSetListener,
                2000, 1, 1);
            dialogPickerDate.setOnShowListener{
                var btn_ok: Button = dialogPickerDate.getButton(DialogInterface.BUTTON_POSITIVE)
                var btn_cancel: Button = dialogPickerDate.getButton(DialogInterface.BUTTON_NEGATIVE)
                btn_ok.setTextColor(resources.getColor(R.color.main_color))
                btn_cancel.setTextColor(resources.getColor(R.color.main_color))
                btn_ok.setOnClickListener {
                    var monthStr: String = dialogPickerDate.datePicker.month.toString()
                    var dayStr: String = dialogPickerDate.datePicker.dayOfMonth.toString()
                    if (dialogPickerDate.datePicker.month < 10){
                        monthStr = "0" + (dialogPickerDate.datePicker.month+1).toString()
                    }
                    if (dialogPickerDate.datePicker.dayOfMonth < 10){
                        dayStr = "0" + dialogPickerDate.datePicker.dayOfMonth.toString()
                    }
                    val date: String = dayStr + "." + monthStr + "." + dialogPickerDate.datePicker.year.toString()
                    binding!!.editDateNumber.text = date
                    dialogPickerDate.dismiss()
                }
            }
            dialogPickerDate.show()
        }







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
            if (date_of_birth != resources.getString(R.string.select_date)){
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
                    MotionToast.LONG_DURATION, ResourcesCompat.getFont(applicationContext,
                        R.font.helvetica_regular
                    ))
                return@setOnClickListener
            }
            val gender: String = findViewById<RadioButton>(selectedId).text.toString()
            dialog!!.show()
            if (selectedImage != null){
                val reference = storage!!.reference.child("Profile")
                    .child(auth!!.uid!!)
                reference.putFile(selectedImage!!).addOnCompleteListener { task->
                    if (task.isSuccessful){
                        reference.downloadUrl.addOnCompleteListener {
                            val imageUrl = it.result.toString()
                            val uid = auth!!.uid
                            val phone = auth!!.currentUser!!.phoneNumber
                            val user = User(
                                uid = uid,name = name, surname =  surname, date_of_birth =  date_of_birth, gender = gender, phoneNumber = phone,
                                 profileImage= imageUrl, fullname = name+" "+surname)
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
                val user = User(uid, name, surname, (name+" "+surname) , date_of_birth, gender, phone, "No Image")
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




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null){
            if (data.data != null){
                binding!!.imgProfile.setImageURI(data.data)
                selectedImage = data.data
            }
        }
    }
}