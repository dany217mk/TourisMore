package space.mosk.tourismore

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import space.mosk.tourismore.AddInfoActivity
import space.mosk.tourismore.MainActivity
import space.mosk.tourismore.databinding.ActivitySplashBinding

private var binding: ActivitySplashBinding? = null
var database: FirebaseDatabase?= null
var auth: FirebaseAuth? = null

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        supportActionBar?.hide()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        auth = FirebaseAuth.getInstance()

            if (auth!!.currentUser != null){
                database = FirebaseDatabase.getInstance()
                database!!.reference
                    .child("users")
                    .orderByChild("uid")
                    .equalTo(auth!!.uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var boolAuth: Boolean = false
                            snapshot.children.forEach {
                                boolAuth = true
                                val intent: Intent = Intent(applicationContext, MainActivity::class.java)
                                startActivity(intent)
                                finishAffinity()
                            }
                            if (!boolAuth){
                                val intent: Intent = Intent(applicationContext, AddInfoActivity::class.java)
                                startActivity(intent)
                                finishAffinity()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("danmos", error.message.toString())
                            val intent: Intent = Intent(applicationContext, AddInfoActivity::class.java)
                            startActivity(intent)
                            finishAffinity()
                        }

                    })
            } else{
                if (restorePrefData()){
                    val authActivity: Intent = Intent(applicationContext, AuthActivity::class.java)
                    startActivity(authActivity)
                    finish()
                } else{
                    val intent: Intent = Intent(applicationContext, StartActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

    }
    private fun restorePrefData(): Boolean {
        var pref: SharedPreferences = applicationContext.getSharedPreferences("myPrefs", MODE_PRIVATE)
        return pref.getBoolean("isIntroOpened", false)
    }
}