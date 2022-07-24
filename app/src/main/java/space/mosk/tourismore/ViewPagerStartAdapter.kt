package space.mosk.tourismore

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter

class ViewPagerStartAdapter constructor(val mContext: Context, val mListScreen: List<SliderStartItem>): PagerAdapter() {
    override fun getCount(): Int {
        return mListScreen.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layoutScreen: View = inflater.inflate(R.layout.layout_screen, null)

        val imgSlider = layoutScreen.findViewById<ImageView>(R.id.intro_img)
        val textSlider = layoutScreen.findViewById<TextView>(R.id.intro_title)
        val descSlider = layoutScreen.findViewById<TextView>(R.id.intro_desc)

        imgSlider.setImageResource(mListScreen.get(position).screenImg)
        textSlider.text = mListScreen.get(position).title
        descSlider.text = mListScreen.get(position).desc

        container.addView(layoutScreen)

        return layoutScreen
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }
}