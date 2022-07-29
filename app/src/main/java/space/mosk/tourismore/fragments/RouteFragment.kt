package space.mosk.tourismore.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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
import space.mosk.tourismore.MapFragment
import space.mosk.tourismore.MarkerInfo
import space.mosk.tourismore.R
import space.mosk.tourismore.Route
import space.mosk.tourismore.adapters.ValueEventListenerAdapter
import space.mosk.tourismore.models.User

class RouteFragment  : Fragment(), Session.RouteListener  {
    private lateinit var mapView: com.yandex.mapkit.mapview.MapView
    private lateinit var button: Button
    private lateinit var button_save: Button

    private var mapObjects: MapObjectCollection? = null
    private var mtRouter: PedestrianRouter? = null
    private lateinit var points: MutableList<RequestPoint>
    private lateinit var route: Route
    private lateinit var data_base: DatabaseReference
    private val data_key : String = "USER'S_PATH"
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

    private lateinit var mUser: User

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val mStorage: StorageReference = FirebaseStorage.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        points = ArrayList()
        MapKitFactory.initialize(requireActivity())
        val v : View = inflater.inflate(R.layout.route_fragment, container, false)
        val button_back : Button = v.findViewById(R.id.button_back)
        button_back.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        }
        mapView = v.findViewById(R.id.mapview_show_route)
        mapView.map.move(
            CameraPosition(Point(53.327300, 50.316413), 16.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0f), null
        )
        mDatabase.child("paths").child("-N87WvjUJgCtP2achX4O").addValueEventListener(
            ValueEventListenerAdapter { dataSnapshot->
                for (snapshot in dataSnapshot.children) {
                    Log.d("LATIT", snapshot.child("point").value.toString())
                    mapView.map.mapObjects.addPlacemark(
                        Point(
                            (snapshot.child("point").value as HashMap<String, Double>).get("latitude")!!.toDouble(),
                            (snapshot.child("point").value as HashMap<String, Double>).get("longitude")!!.toDouble()
                        )
                    )
                    points.add(RequestPoint(Point(
                        (snapshot.child("point").value as HashMap<String, Double>).get("latitude")!!.toDouble(),
                        (snapshot.child("point").value as HashMap<String, Double>).get("longitude")!!.toDouble()
                    ), RequestPointType.WAYPOINT, null))
                }

                })
        mapObjects = mapView!!.map.mapObjects.addCollection()
        val options = TimeOptions()
        route = Route(ArrayList<MarkerInfo>(0), 0)

        mtRouter = TransportFactory.getInstance()?.createPedestrianRouter()
        mtRouter?.requestRoutes(points, options, this)
        return v
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
        mapObjects = mapView!!.map.mapObjects.addCollection()
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

    }
}