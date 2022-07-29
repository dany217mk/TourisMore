package space.mosk.tourismore

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.places.PlacesFactory
import com.yandex.mapkit.places.panorama.NotFoundError
import com.yandex.mapkit.places.panorama.PanoramaService
import com.yandex.mapkit.places.panorama.PanoramaView
import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError


class PanoramaFragment : Activity(), PanoramaService.SearchListener {
    private val MAPKIT_API_KEY = "f727989a-ecd4-4f05-a90d-f923d9179f62"
    private val SEARCH_LOCATION: Point = Point(55.733330, 37.587649)
    private var panoramaView: PanoramaView? = null
    private var panoramaService: PanoramaService? = null
    private var searchSession: PanoramaService.SearchSession? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.initialize(this)
        PlacesFactory.initialize(this)
        setContentView(R.layout.panorama)
        super.onCreate(savedInstanceState)
        panoramaView = findViewById<View>(R.id.panoview) as PanoramaView
        panoramaService = PlacesFactory.getInstance().createPanoramaService()
        searchSession = panoramaService!!.findNearest(SEARCH_LOCATION, this)
        findViewById<Button>(R.id.close_pan).setOnClickListener { finishActivity(0) }
    }

    override fun onStop() {
        panoramaView?.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        panoramaView?.onStart()
    }


    override fun onPanoramaSearchResult(p0: String) {
        panoramaView?.getPlayer()?.openPanorama(p0)
        panoramaView?.getPlayer()?.enableRotation()
        panoramaView?.getPlayer()?.enableZoom()
        panoramaView?.getPlayer()?.enableMarkers()
    }

    override fun onPanoramaSearchError(p0: Error) {
        Toast.makeText(this, p0.toString(), Toast.LENGTH_SHORT).show()
    }

}