package space.mosk.tourismore

import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TopPlacesFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var placesList : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_top_places, container, false)
        placesList = view.findViewById(R.id.topPlacesList)
        placesList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
        val centerViews = LinearSnapHelper()
        centerViews.attachToRecyclerView(placesList)
        placesList.adapter = TopPlacesAdapter(makePlaces())
        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TopPlacesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}