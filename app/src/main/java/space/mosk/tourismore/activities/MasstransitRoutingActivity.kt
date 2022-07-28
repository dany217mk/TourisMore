package space.mosk.tourismore.activities

import android.app.Activity
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.map.MapObjectCollection
import android.os.Bundle
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.transport.TransportFactory
import android.view.View
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.RequestPoint
import java.util.ArrayList
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.geometry.SubpolylineHelper
import com.yandex.runtime.network.RemoteError
import com.yandex.runtime.network.NetworkError
import android.widget.Toast
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.transport.masstransit.SectionMetadata.SectionData
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.mapkit.transport.masstransit.*
import com.yandex.runtime.Error
import space.mosk.tourismore.R
import java.util.HashSet

class MasstransitRoutingActivity : Activity(), Session.RouteListener {
    private val MAPKIT_API_KEY = "f727989a-ecd4-4f05-a90d-f923d9179f62"
    private val TARGET_LOCATION = Point(55.752078, 37.592664)
    private val ROUTE_START_LOCATION = Point(55.699671, 37.567286)
    private val ROUTE_END_LOCATION = Point(55.790621, 37.558571)
    private var mapView: MapView? = null
    private var mapObjects: MapObjectCollection? = null
    private var mtRouter: PedestrianRouter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        MapKitFactory.initialize(this)
        TransportFactory.initialize(this)
        setContentView(R.layout.tmp_map)
        super.onCreate(savedInstanceState)
        mapView = findViewById<View>(R.id.mapview) as MapView

        // And to show what can be done with it, we move the camera to the center of Saint Petersburg.
        mapView!!.map.move(
            CameraPosition(TARGET_LOCATION, 12.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 5f),
            null
        )
        mapObjects = mapView!!.map.mapObjects.addCollection()
        val options = TimeOptions()
        val points: MutableList<RequestPoint> = ArrayList()
        points.add(RequestPoint(ROUTE_START_LOCATION, RequestPointType.WAYPOINT, null))
        points.add(RequestPoint(ROUTE_END_LOCATION, RequestPointType.WAYPOINT, null))
        mtRouter = TransportFactory.getInstance()?.createPedestrianRouter()
        mtRouter?.requestRoutes(points, options, this)
    }

    override fun onStop() {
        mapView!!.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView!!.onStart()
    }

    override fun onMasstransitRoutes(routes: List<Route>) {
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

    override fun onMasstransitRoutesError(p0: Error) {
        //pass
    }

    /*
    override fun onMasstransitRoutesError(error: Error) {
        var errorMessage = getString(R.string.unknown_error_message)
        if (error is RemoteError) {
            errorMessage = getString(R.string.remote_error_message)
        } else if (error is NetworkError) {
            errorMessage = getString(R.string.network_error_message)
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }
    */
    private fun drawSection(
        data: SectionData,
        geometry: Polyline
    ) {
        // Draw a section polyline on a map
        // Set its color depending on the information which the section contains
        val polylineMapObject = mapObjects!!.addPolyline(geometry)
        // Masstransit route section defines exactly one on the following
        // 1. Wait until public transport unit arrives
        // 2. Walk
        // 3. Transfer to a nearby stop (typically transfer to a connected
        //    underground station)
        // 4. Ride on a public transport
        // Check the corresponding object for null to get to know which
        // kind of section it is
        if (data.transports != null) {
            // A ride on a public transport section contains information about
            // all known public transport lines which can be used to travel from
            // the start of the section to the end of the section without transfers
            // along a similar geometry
            for (transport in data.transports!!) {
                // Some public transport lines may have a color associated with them
                // Typically this is the case of underground lines
                if (transport.line.style != null) {
                    polylineMapObject.setStrokeColor( // The color is in RRGGBB 24-bit format
                        // Convert it to AARRGGBB 32-bit format, set alpha to 255 (opaque)
                        transport.line.style!!.color!! or -0x1000000
                    )
                    return
                }
            }
            // Let us draw bus lines in green and tramway lines in red
            // Draw any other public transport lines in blue
            /*
            val knownVehicleTypes = HashSet<String>()
            knownVehicleTypes.add("bus")
            knownVehicleTypes.add("tramway")
            for (transport in data.transports!!) {
                val sectionVehicleType = getVehicleType(transport, knownVehicleTypes)
                if (sectionVehicleType == "bus") {
                    polylineMapObject.setStrokeColor(-0xff0100) // Green
                    return
                } else if (sectionVehicleType == "tramway") {
                    polylineMapObject.setStrokeColor(-0x10000) // Red
                    return
                }
            }
             */
            polylineMapObject.setStrokeColor(-0xffff01) // Blue
        } else {
            // This is not a public transport ride section
            // In this example let us draw it in black
            polylineMapObject.setStrokeColor(-0xffff01) // Black  -0x1000000
        }
    }
    /*
    private fun getVehicleType(transport: Transport, knownVehicleTypes: HashSet<String>): String? {
        // A public transport line may have a few 'vehicle types' associated with it
        // These vehicle types are sorted from more specific (say, 'histroic_tram')
        // to more common (say, 'tramway').
        // Your application does not know the list of all vehicle types that occur in the data
        // (because this list is expanding over time), therefore to get the vehicle type of
        // a public line you should iterate from the more specific ones to more common ones
        // until you get a vehicle type which you can process
        // Some examples of vehicle types:
        // "bus", "minibus", "trolleybus", "tramway", "underground", "railway"
        for (type in transport.line.vehicleTypes) {
            if (knownVehicleTypes.contains(type)) {
                return type
            }
        }
        return null
    }
     */
}