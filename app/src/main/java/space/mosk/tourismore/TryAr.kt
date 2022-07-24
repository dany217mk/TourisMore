package space.mosk.tourismore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.ar.sceneform.ux.ArFragment


class TryAr : AppCompatActivity() {
    private lateinit var btn1 : Button
    private lateinit var btn2 : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_try_ar2)
        btn1 = findViewById(R.id.carBtn)
        btn2 = findViewById(R.id.machine)
        btn1.setOnClickListener{
            (supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment)
                .setOnTapPlaneGlbModel("j.glb")
        }
        btn2.setOnClickListener {
            (supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment)
                .setOnTapPlaneGlbModel("xyj.glb")
        }

    }

}
