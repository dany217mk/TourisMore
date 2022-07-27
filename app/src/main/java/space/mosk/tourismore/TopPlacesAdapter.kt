package space.mosk.tourismore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.flaviofaria.kenburnsview.KenBurnsView

class TopPlacesAdapter(val array : List<Places>) : RecyclerView.Adapter<TopPlacesAdapter.ViewHolder>() {
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val title : TextView = itemView.findViewById(R.id.tourTitle)
        val descr : TextView = itemView.findViewById(R.id.tourDescription)
        val img : KenBurnsView = itemView.findViewById(R.id.tourImg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.top_place, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = array[position].title
        holder.descr.text = array[position].description
        Glide.with(holder.itemView.context)
            .load(array[position].urlToImg)
            .apply(RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override((Target.SIZE_ORIGINAL)))
            .into(holder.img)
    }

    override fun getItemCount(): Int {
        return array.size
    }

}

data class Places(val title : String, val description : String, val urlToImg : String)

fun makePlaces() : List<Places>{
    return listOf(
        Places("Красная площадь", "Красная площадь по праву " +
                "возглавляет рейтинг самых популярных мест в Москве. " +
                "Ее выверенный до сантиметра, веками формировавшийся " +
                "архитектурный ансамбль поражает воображение своей монументальностью и благородным величием.", "https://infosmi.net/wp-content/uploads/2020/11/kreml-moskva-rossiya-z7zh.jpg"),
        Places("ГУМ", "Самый известный магазин страны – уникальный историко-культурный " +
                "памятник мирового уровня и полноценная визитная карточка города.", "https://незабываемая.москва/blog/samyye_poseshchayemyye_mesta_v_moskve_2_IS.jpg"),
        Places("Музеи Московского Кремля", "Культурно-исторический музей-заповедник «Московский Кремль» (ММК) фактически был основан Александром I " +
                "в 1806 году на базе Оружейной палаты.", "https://незабываемая.москва/blog/samyye_poseshchayemyye_mesta_v_moskve_3_IS.jpg"),
        Places("ВДНХ", "Когда-то грандиозная Выставка достижений народного хозяйства " +
                "превратилась в один из самых масштабных в стране памятников " +
                "советской эпохи и популярнейшее место для отдыха.", "https://images.unsplash.com/photo-1658843941585-dd447ae263c9?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=715&q=80")
    )
}