package space.mosk.tourismore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.google.android.material.button.MaterialButton


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SirviceOneFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var srvsName : TextView
    private lateinit var backButton : MaterialButton

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

        return inflater.inflate(R.layout.fragment_sirvice_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        srvsName = view.findViewById(R.id.srvsName1)
        backButton = view.findViewById(R.id.backBtn1)
        backButton.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.slide_left, R.animator.slide_right)
                .replace(R.id.container, ServicesFragment())
                .commit()
        }
        val model = ViewModelProvider(requireActivity()).get(ShareBetweenFragments::class.java)
        model.name.observe(viewLifecycleOwner, Observer{
            srvsName.text = it
        })
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SirviceOneFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}