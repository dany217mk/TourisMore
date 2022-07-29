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
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.RotationType
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import space.mosk.tourismore.fragments.PathsFragment


class MapFragment : Fragment(), UserLocationObjectListener {

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

        val view : View = inflater.inflate(R.layout.map_fragment, container,  false)
        view.findViewById<Button>(R.id.button_find).setOnClickListener {
            val nextFrag = PathsFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, nextFrag, "findThisFragment")
                .addToBackStack(null)
                .commit() }
        mapView = view.findViewById(R.id.mapview)
        mapView?.getMap()?.isRotateGesturesEnabled = false
        mapView?.getMap()?.move(CameraPosition(Point(0.0, 0.0), 14f, 0f, 0f))
        requestLocationPermission()
        val mapKit = MapKitFactory.getInstance()
        mapKit.resetLocationManagerToDefault()

        userLocationLayer = mapView?.getMapWindow()?.let { mapKit.createUserLocationLayer(it) }
        userLocationLayer!!.isVisible = true
        userLocationLayer!!.isHeadingEnabled = true
        userLocationLayer!!.setObjectListener(this)

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

    override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocationLayer!!.setAnchor(
            PointF((mapView!!.width * 0.5).toFloat(), (mapView!!.height * 0.5).toFloat()),
            PointF((mapView!!.width * 0.5).toFloat(), (mapView!!.height * 0.83).toFloat())
        )
        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(
                requireActivity(), R.drawable.user_arrow
            )
        )
        val pinIcon = userLocationView.pin.useCompositeIcon()
        pinIcon.setIcon(
            "icon",
            ImageProvider.fromResource(requireActivity(), R.drawable.icon),
            IconStyle().setAnchor(PointF(0f, 0f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(0f)
                .setScale(1f)
        )
        pinIcon.setIcon(
            "pin",
            ImageProvider.fromResource(requireActivity(), R.drawable.search_result),
            IconStyle().setAnchor(PointF(0.5f, 0.5f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(1f)
                .setScale(0.5f)
        )
        userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x66000001
    }

    override fun onObjectRemoved(view: UserLocationView) {}
    override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {}

    companion object {
        const val PERMISSIONS_REQUEST_FINE_LOCATION = 1
    }
}