package space.mosk.tourismore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class GridRecyclerView(val imgArray : List<Images>) : RecyclerView.Adapter<GridRecyclerView.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val img : ImageView = itemView.findViewById(R.id.profilePh)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.one_img, parent, false)
        return GridRecyclerView.ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.img.setImageResource(imgArray[position].IMmg)
    }

    override fun getItemCount(): Int {
        return imgArray.size
    }

}

data class Images(val IMmg : Int)

fun createImages() : List<Images>{
    return listOf(
        Images(R.drawable.profile),
        Images(R.drawable.plus),
        Images(R.drawable.logo),
        Images(R.drawable.ratings),
        Images(R.drawable.step),
        Images(R.drawable.step),
        Images(R.drawable.step),
        Images(R.drawable.step),
        Images(R.drawable.step),
        Images(R.drawable.step),
        Images(R.drawable.step),
        Images(R.drawable.step),
        Images(R.drawable.step)
    )
}