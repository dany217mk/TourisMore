package space.mosk.tourismore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ListAdapter(private val array: List<Way>, val onViewClick: OnViewClickListener) : RecyclerView.Adapter<ListAdapter.ViewHolder>()
{

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name : TextView = itemView.findViewById(R.id.wayName)
        val img : ImageView = itemView.findViewById(R.id.imgCardHolder)
        val littleDesc : TextView = itemView.findViewById(R.id.wayDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.one_path_block, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = array[position].name
        holder.littleDesc.text = array[position].littleDescr
        holder.img.setImageResource(array[position].img)
        holder.itemView.setOnClickListener{
            onViewClick.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return array.size
    }
}

interface OnViewClickListener {
    fun onItemClick(pos : Int)
}

data class Way(val name :String,val littleDescr: String, val description : String, val img : Int, val srcImg : Int){
    /*
    constructor( name :String, littleDescr: String,  description : String,  img : String, srcImg : String) : this(name,littleDescr,  description, img, srcImg){

    }

     */
}

fun makeSampleWays() : List<Way>{
    return listOf(
        Way("Кремль", "15 KM","Годнота", R.drawable.squared_img, R.drawable.kremlin),
        Way("Александровский сад", "25 KM","Годнота", R.drawable.squared_img, R.drawable.kremlin),
        Way("ВДНХ", "16 KM","Годнота", R.drawable.squared_img, R.drawable.kremlin),
        Way("Музей космонавтики", "12 KM","Супер пупер очень круто", R.drawable.squared_img, R.drawable.kremlin),
        Way("Центр океанографии и морской биологии Москвариум", "1225 KM","ааааааааааааааа", R.drawable.squared_img, R.drawable.kremlin),
        Way("ббббббббббб", "24 KM","ббббббббббббб", R.drawable.squared_img, R.drawable.kremlin),
    )
}