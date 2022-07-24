package space.mosk.tourismore

import `in`.aabhasjindal.otptextview.OTPListener
import `in`.aabhasjindal.otptextview.OtpTextView
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import space.mosk.tourismore.databinding.ActivityCheckCodeBinding
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.util.concurrent.TimeUnit


class CheckCodeActivity : AppCompatActivity() {

    private var binding: ActivityCheckCodeBinding? = null
    var verificationId: String? = null
    var auth: FirebaseAuth? = null
    var dialog: ProgressDialog?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        supportActionBar?.hide()
        binding = ActivityCheckCodeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        dialog = ProgressDialog(this@CheckCodeActivity)
        dialog!!.setMessage("Отправляем код. Подождите..")
        dialog!!.setCancelable(false)
        dialog!!.show()
        auth = FirebaseAuth.getInstance()
        val phoneNumber = intent.getStringExtra("phoneNumber")
        binding!!.phoneLabel.text = "Код отправлен на номер: $phoneNumber"


        val options = PhoneAuthOptions.newBuilder(auth!!)
            .setPhoneNumber(phoneNumber.toString())       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    Log.d("success", p0.toString())
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    Log.d("error", p0.toString())
                    MotionToast.createColorToast(this@CheckCodeActivity,
                        "Попробуйте позже ☹️",
                        "Сервер временно недоступен",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(applicationContext,R.font.helvetica_regular))
                        Log.d("danmos", p0.toString())
                        dialog!!.setMessage("Упсс, возникла небольшая ошибка. Попробуйте позжу..")
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    dialog!!.dismiss()
                    MotionToast.createColorToast(this@CheckCodeActivity,
                        "Код отправлен успешно️",
                        "",
                        MotionToastStyle.INFO,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(applicationContext,R.font.helvetica_regular))
                    verificationId = p0
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                    binding!!.otpView.requestFocus()
                }

            })          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)


        binding!!.otpView.requestFocus()
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        binding!!.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {}
            override fun onOTPComplete(otp: String) {
                dialog!!.setMessage("Проверяем данные")
                dialog!!.show()
                val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
                auth!!.signInWithCredential(credential)
                    .addOnCompleteListener{ task->
                        dialog!!.dismiss()
                        if (task.isSuccessful){
                            binding!!.otpView.showSuccess()
                            MotionToast.createColorToast(this@CheckCodeActivity,
                                "Успешно 😍",
                                "Код корректен!",
                                MotionToastStyle.SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION, ResourcesCompat.getFont(applicationContext,R.font.helvetica_regular))
                            val intent: Intent = Intent(applicationContext, AddInfoActivity::class.java)
                            startActivity(intent)
                            finishAffinity()
                        } else{
                            MotionToast.createColorToast(this@CheckCodeActivity,
                                "Неудачно ☹️",
                                "Код неверен!",
                                MotionToastStyle.ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(applicationContext,R.font.helvetica_regular))
                            binding!!.otpView.showError()
                        }

                    }
            }
        }

        binding!!.noCode.setOnClickListener{
            val authActivity: Intent = Intent(applicationContext, AuthActivity::class.java)
            startActivity(authActivity)
            finish()
        }
    }
}