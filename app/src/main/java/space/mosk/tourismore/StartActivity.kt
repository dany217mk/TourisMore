package space.mosk.tourismore

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import java.util.*

class StartActivity : AppCompatActivity() {

    private var screenPager: ViewPager? = null
    private var viewPagerAdapter: ViewPagerAdapter? = null
    private var tabIntro: TabLayout? = null
    private var btn_next: Button? = null
    private var position: Int = 0
    private var btn_get_started: Button? = null
    private var btn_anim: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        supportActionBar?.hide()
        setContentView(R.layout.activity_start)

        btn_next = findViewById(R.id.btn_next)
        tabIntro = findViewById(R.id.tab_intro)
        btn_get_started = findViewById(R.id.btn_get_started)
        btn_anim = AnimationUtils.loadAnimation(applicationContext, R.anim.btn_get_started_anim)


        val mList: MutableList<SliderStartItem> = ArrayList()
        mList.add(SliderStartItem("Travel", "Исследуй мир с новой стороны\n Поделись эмоциями с другими пользователями", R.drawable.service1))
        mList.add(SliderStartItem("Communicate", "Найди новых друзей\n И стань самым популярным автором", R.drawable.service2))
        mList.add(SliderStartItem("TouriseMore", "Сделай свое путешествие незабываемым вместе с TourisMore!", R.drawable.service3))



        screenPager = findViewById(R.id.sliderViewPager)
        viewPagerAdapter = ViewPagerAdapter(this, mList)
        screenPager?.adapter = viewPagerAdapter

        tabIntro?.setupWithViewPager(screenPager)

        btn_next?.setOnClickListener {
            position = screenPager?.currentItem!!
            if (position < mList.size){
                position++
                screenPager?.setCurrentItem(position)
            }
            if  (position == mList.size-1){
                loadLastScreen()
            }
        }

        tabIntro?.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == mList.size-1){
                    loadLastScreen()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        btn_get_started?.setOnClickListener {
            val authActivity: Intent = Intent(applicationContext, SplashActivity::class.java)
            startActivity(authActivity)

            savePrefsData()
        }
    }

    private fun savePrefsData() {
        var pref: SharedPreferences = applicationContext.getSharedPreferences("myPrefs", MODE_PRIVATE)
        var editor: SharedPreferences.Editor = pref.edit()
        editor.putBoolean("isIntroOpened", true)
        editor.commit()
    }

    private fun loadLastScreen() {
        btn_next?.visibility = View.INVISIBLE
        tabIntro?.visibility = View.INVISIBLE
        btn_get_started?.visibility = View.VISIBLE

        btn_get_started?.animation = btn_anim
    }
}