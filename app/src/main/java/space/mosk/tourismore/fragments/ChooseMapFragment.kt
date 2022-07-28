package space.mosk.tourismore.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import space.mosk.tourismore.R
import space.mosk.tourismore.adapters.ShareBetweenFragments
import space.mosk.tourismore.makeSampleWays

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ChooseMapFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private var index = 0
    private val ways = makeSampleWays()

    private lateinit var imgView : ImageView
    private lateinit var pathName : TextView
    private lateinit var description : TextView
    private lateinit var goBtn :Button
    private lateinit var backBtn : Button
    //private lateinit var swithcer : SwitchMaterial
    private lateinit var previewButton : MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imgView = view.findViewById(R.id.imageView)
        pathName = view.findViewById(R.id.name2)
        description = view.findViewById(R.id.textView2)
        goBtn = view.findViewById(R.id.materialButton)
        backBtn = view.findViewById(R.id.materialButton2)
        previewButton = view.findViewById(R.id.prevBtn)

        previewButton.setOnClickListener {
            val intent = Intent(context, PanoramaFragment::class.java)
            startActivity(intent)
        }

        backBtn.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.slide_left, R.animator.slide_right)
                .replace(R.id.container, PathsFragment())
                .commit()
        }

        val model = ViewModelProvider(requireActivity()).get(ShareBetweenFragments::class.java)
        model.index.observe(viewLifecycleOwner, Observer{
            index = it
            Glide.with(requireContext()).load(ways[it].srcImg).into(imgView)
            //imgView.setImageResource(ways[it].srcImg)
            pathName.text = ways[it].name
            description.text = ways[it].description
        })

        goBtn.setOnClickListener{

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_map, container, false)
    }

    override fun onStop() {
        super.onStop()
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChooseMapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}