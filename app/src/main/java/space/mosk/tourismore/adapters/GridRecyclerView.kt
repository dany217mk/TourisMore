package space.mosk.tourismore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class GridRecyclerView(val imgArray : List<String>) : RecyclerView.Adapter<GridRecyclerView.ViewHolder>() {

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
            val dialog = AlertDialog.Builder(holder.itemView.context)
            dialog.setTitle("Запись")
            val inflater = LayoutInflater.from(holder.itemView.context)
            val get_img_window: View = inflater.inflate(R.layout.get_img_window, null)
            dialog.setView(get_img_window)
            val image = get_img_window.findViewById<ImageView>(R.id.image_get)
            Glide.with(get_img_window).load(imgArray[position]).centerCrop().into(image)

            dialog.setNegativeButton(
                "Отмена"
            ) { dialogInterface, i -> dialogInterface.dismiss() }

            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return imgArray.size
    }

}
interface profilePicsClick{
    fun onClick(pos : Int)
}
