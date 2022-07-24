package space.mosk.tourismore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PathsFragment : Fragment(), OnViewClickListener {

    private var param1: String? = null
    private var param2: String? = null
    private val ways = makeSampleWays()

    private lateinit var listView: RecyclerView
    private lateinit var model : ShareBetweenFragments

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
        return inflater.inflate(R.layout.fragment_paths, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view.findViewById(R.id.recyclerContainer)
        listView.layoutManager = LinearLayoutManager(context)

        val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.devider)
            ?.let { itemDecorator.setDrawable(it) }

        listView.addItemDecoration(itemDecorator)
        listView.adapter = ListAdapter(ways, this)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PathsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onItemClick(pos: Int) {
        model = ViewModelProvider(requireActivity()).get(ShareBetweenFragments::class.java)
        model.sendIndex(pos)
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.slide_left, R.animator.slide_right)
            .replace(R.id.container, ChooseMapFragment())
            .commit()
    }
}