package space.mosk.tourismore.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import space.mosk.tourismore.databinding.ActivityAuthBinding

//qiwi

class AuthActivity : AppCompatActivity() {
    private var continueBtn: Button? = null

    private var binding: ActivityAuthBinding? = null


    var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        supportActionBar?.hide()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        auth = FirebaseAuth.getInstance()

        binding!!.continueBtn.isEnabled = false
        binding!!.editNumber.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                binding!!.continueBtn.isEnabled = binding!!.editNumber.text.toString().isNotEmpty()
                        && binding!!.editNumber.text.toString() != "+7(123)456-78-90" && binding!!.editNumber.text.toString().length == 16
            }

        })


        binding!!.continueBtn.setOnClickListener{
            val intent: Intent = Intent(applicationContext, CheckCodeActivity::class.java)
            intent.putExtra("phoneNumber", binding!!.editNumber.text.toString())
            startActivity(intent)
        }




    }
}