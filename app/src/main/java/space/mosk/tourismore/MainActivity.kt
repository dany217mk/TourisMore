package space.mosk.tourismore

import android.os.Bundle
import android.widget.Adapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import space.mosk.tourismore.*

class MainActivity : AppCompatActivity(){
    private lateinit var bottomNavigationBar : BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        bottomNavigationBar = findViewById(R.id.navigationView)
        val fragment = PathsFragment()
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
                    fragment = PathsFragment()
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
        /*
        https://www.meghandev.io/post/android-bottomnavigationview-example-tutorial
        https://blog.mindorks.com/android-bottom-navigationview-in-kotlin
        https://medium.com/@jaaveeth.developer/arcore-81528569eb2c
        1)https://medium.com/make-an-android-ar-augmented-reality-app-for/make-an-ar-augmented-reality-app-for-android-in-2022-48a1711562bb
        2) https://medium.com/coinmonks/arcore-sceneform-augmented-images-3-android-ar-app-2c0990f65df2
        3!!!!!!!!!)https://medium.com/@jose01.arteaga/kotlin-arcore-49b7a234f7cf
         */
    }
    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.animator.slide_left, R.animator.slide_right)
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

}