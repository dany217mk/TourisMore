package space.mosk.tourismore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class GridRecyclerView(val imgArray : List<String>, val onItemClicked : profilePicsClick) : RecyclerView.Adapter<GridRecyclerView.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val img : ImageView = itemView.findViewById(R.id.profilePh)
    }
    private lateinit var images: List<String>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.one_img, parent, false)
        return GridRecyclerView.ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView).load(imgArray[position]).centerCrop().into(holder.img)
        holder.itemView.setOnClickListener {
            onItemClicked.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return imgArray.size
    }

}
interface profilePicsClick{
    fun onClick(pos : Int)
}
