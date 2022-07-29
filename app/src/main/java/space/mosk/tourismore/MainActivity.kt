package space.mosk.tourismore

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Adapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yandex.mapkit.MapKitFactory
import space.mosk.tourismore.*
import space.mosk.tourismore.fragments.*

class MainActivity : AppCompatActivity(){

    private var initialized = false
    private val MAPKIT_API_KEY = "f727989a-ecd4-4f05-a90d-f923d9179f62"
    private lateinit var bottomNavigationBar : BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        MapKitFactory.initialize(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        bottomNavigationBar = findViewById(R.id.navigationView)
        val fragment = MapFragment()
        loadFragment(fragment)
        bottomNavigationBar.setOnItemSelectedListener {
                item -> var fragment: Fragment
            when (item.itemId) {
                R.id.home -> {
                    fragment = ServicesFragment()
                    loadFragment(fragment)
                    true
                }
                R.id.paths -> {
                    fragment = MapFragment()
                    loadFragment(fragment)
                    true
                }
                R.id.news -> {
                    fragment = NewsFragment()
                    loadFragment(fragment)
                    true
                }
                R.id.messages -> {
                    fragment = MessagesFragment()
                    loadFragment(fragment)
                    true
                }
                R.id.profile ->{
                    fragment = ProfileFragment()
                    loadFragment(fragment)
                    true
                }
                else -> false
            }
        }
        bottomNavigationBar.setOnItemReselectedListener{}
    }

    fun initialize(apiKey: String, context: Context) {
        val r : Boolean = getSharedPreferences("mysettings",  Context.MODE_PRIVATE).getBoolean("init", true)
        if (initialized || r) {
            return
        }

        val e  = getSharedPreferences("mysettings", Context.MODE_PRIVATE).edit()
        e.putBoolean("init", true)
        e.apply()
        MapKitFactory.setApiKey(apiKey)
        MapKitFactory.initialize(context)
        initialized = true
    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.animator.slide_left, R.animator.slide_right)
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }
    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }
}