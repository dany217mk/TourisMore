package space.mosk.tourismore

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import space.mosk.tourismore.*

class ViewPagerAdapter(fa: FragmentActivity, private val listOfTitle: List<String>) :
    FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = listOfTitle.size

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return PathsFragment()
            1 -> return NewsFragment()
            2 -> return MessagesFragment()
            3 -> return ServicesFragment()
            4 -> return ProfileFragment()
        }
        return PathsFragment()
    }

}