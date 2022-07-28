package space.mosk.tourismore.fragments
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.lifecycle.ViewModelProvider
import space.mosk.tourismore.R
import space.mosk.tourismore.adapters.ShareBetweenFragments

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class ServicesFragment : Fragment(){
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var gridLayout : GridLayout
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
        return inflater.inflate(R.layout.fragment_services, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gridLayout = view.findViewById(R.id.gridLayout)
        val count = gridLayout.childCount
        model = ViewModelProvider(requireActivity()).get(ShareBetweenFragments::class.java)
        for(i in 0 until count){
            val container = gridLayout.getChildAt(i)
            container.setOnClickListener {
                when(i){
                        0 -> {
                        loadFragment(FriendsFragment())
                        model.sendServiceName("Пользователи")
                    }
                    1 -> {
                        loadFragment(AddRecordFragment())
                        model.sendServiceName("Поделиться записью")
                    }
                    2 ->{
                        loadFragment(TopPlacesFragment())
                    }
                    3->{
                        loadFragment(RatingFragment())
                    }
                    4->{
                        loadFragment(PedometerFragment())
                    }
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment){
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.slide_left, R.animator.slide_right)
            .replace(R.id.container, fragment)
            .commit()
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ServicesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}