package space.mosk.tourismore

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.geometry.SubpolylineHelper
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.transport.TransportFactory
import com.yandex.mapkit.transport.masstransit.PedestrianRouter
import com.yandex.mapkit.transport.masstransit.SectionMetadata
import com.yandex.mapkit.transport.masstransit.Session
import com.yandex.mapkit.transport.masstransit.TimeOptions
import com.yandex.runtime.Error
import java.util.ArrayList

class MakeRouteActivity : Fragment(), Session.RouteListener  {
    private lateinit var mapView: com.yandex.mapkit.mapview.MapView
    private lateinit var button: Button
    private lateinit var button_save: Button
    private lateinit var placeMark1: PlacemarkMapObject
    private lateinit var placeMark2: PlacemarkMapObject
    private lateinit var bufMark: PlacemarkMapObject

    private var mapObjects: MapObjectCollection? = null
    private var mtRouter: PedestrianRouter? = null
    private lateinit var points: MutableList<RequestPoint>
    private lateinit var route: Route
    private lateinit var data_base: DatabaseReference   //variable for firebase
    private val data_key : String = "USER'S_PATH"   //key for firebase
    private var mapObjectTapListener: MapObjectTapListener =
        MapObjectTapListener { mapObject, point ->
            val mark = mapObject as PlacemarkMapObject
            val ppoint: Point = Point(mark.geometry.latitude, mark.geometry.longitude)
            mapView.map.move(
                CameraPosition(ppoint, 16.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 2f), null
            )
            return@MapObjectTapListener true
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("ЧsЗХ", "ЧsЗХ")
        MapKitFactory.initialize(requireActivity())
        val v : View = inflater.inflate(R.layout.fragment_make_path, container, false)
        button = v.findViewById(R.id.button_make)
        button_save = v.findViewById(R.id.save_path)

        mapView = v.findViewById(R.id.mapview_route)
        mapView.map.move(
            CameraPosition(Point(53.327300, 50.316413), 16.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0f), null
        )

        mapObjects = mapView!!.map.mapObjects.addCollection()
        val options = TimeOptions()

        points = ArrayList()
        route = Route(ArrayList<MarkerInfo>(0), 0)
        button.setOnClickListener { make_new_marker() }
        return v
    }
    private fun make_new_marker() {
        mapView.map.mapObjects.addPlacemark(mapView.map.cameraPosition.target)
        route.points.add(MarkerInfo("", mapView.map.cameraPosition.target.latitude, mapView.map.cameraPosition.target.longitude))

        points.add(RequestPoint(mapView.map.cameraPosition.target, RequestPointType.WAYPOINT, null))
        val options = TimeOptions()
        if (points.size > 1){
            mtRouter = TransportFactory.getInstance()?.createPedestrianRouter()
            mtRouter?.requestRoutes(points, options, this)
        }
    }

    override fun onMasstransitRoutes(routes: MutableList<com.yandex.mapkit.transport.masstransit.Route>) {
        if (routes.size > 0) {
            for (section in routes[0].sections) {
                drawSection(
                    section.metadata.data,
                    SubpolylineHelper.subpolyline(
                        routes[0].geometry, section.geometry
                    )
                )
            }
        }
    }
    private fun drawSection(
        data: SectionMetadata.SectionData,
        geometry: Polyline
    ) {
        val polylineMapObject = mapObjects!!.addPolyline(geometry)

        if (data.transports != null) {
            for (transport in data.transports!!) {
                if (transport.line.style != null) {
                    polylineMapObject.setStrokeColor(
                        transport.line.style!!.color!! or -0x1000000
                    )
                    return
                }
            }

            polylineMapObject.setStrokeColor(-0xffff01)
        } else {
            polylineMapObject.setStrokeColor(-0xffff01)
        }
    }
    override fun onMasstransitRoutesError(p0: Error) {
        TODO("Not yet implemented")
    }
}