package space.mosk.tourismore

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.RotationType
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider

class FeedMarkerFragment : Fragment(){

    private val MAPKIT_API_KEY = "f727989a-ecd4-4f05-a90d-f923d9179f62"
    private var mapView: MapView? = null
    private var userLocationLayer: UserLocationLayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {requestLocationPermission()

        val view : View = inflater.inflate(R.layout.fragment_feed_marker, container,  false)
        val bundle : Bundle = Bundle()
        val lng = bundle.getDouble("lng")
        val lat = bundle.getDouble("lat")
        mapView = view.findViewById(R.id.mapview)
        mapView?.getMap()?.isRotateGesturesEnabled = false
        mapView?.getMap()?.move(CameraPosition(Point(0.0, 0.0), 14f, 0f, 0f))
        requestLocationPermission()
        val mapKit = MapKitFactory.getInstance()
        mapKit.resetLocationManagerToDefault()
        mapView!!.map.move(
            CameraPosition(Point(lng, lat), 14.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 5f),
            null
        )
        return view
    }
    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                "android.permission.ACCESS_FINE_LOCATION"
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf("android.permission.ACCESS_FINE_LOCATION"),
                PERMISSIONS_REQUEST_FINE_LOCATION
            )
        }
    }


    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView!!.onStart()
    }


    companion object {
        const val PERMISSIONS_REQUEST_FINE_LOCATION = 1
    }
}