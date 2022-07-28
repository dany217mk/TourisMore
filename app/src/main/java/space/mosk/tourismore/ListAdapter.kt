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
        if(array[position].name.length > 19){
            var s = ""
            for(i in 0 until 16){
                s += array[position].name[i]
            }
            s += "..."
            holder.name.text = s
        }
        else{
            holder.name.text = array[position].name
        }
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

data class Way(val name :String,val littleDescr: String, val description : String, val img : Int, val srcImg : String)

fun makeSampleWays() : List<Way>{
    return listOf(
        Way("Шумные улицы и бульвары Москвы", "8 KM","Этот 8-километровый маршрут — отличный " +
                "способ познакомиться с главными улицами Москвы и прогуляться по широким " +
                "бульварам города. Начиная путь от станции метро Арбатская, стоит, прежде" +
                " всего, пройти по Старому и Новому Арбату — с многочисленными барами, " +
                "сувенирными магазинами, кафе и магазинами (обязательно загляните в ТЦ «Весна»!).", R.drawable.arbat, "https://n1s1.elle.ru/0e/7f/7c/0e7f7caff07dcdbcf82459a5c12a59c7/728x524_1_ab0212eac500bbc337e818e5ea254632@940x676_0xc35dbb80_14689052981497610633.jpeg"),
        Way("От Китай-города до Пятницкой улицы", "4 KM","Маршрут не очень длинный (4 километра)," +
                " но вполне подойдет для ознакомления с центром столицы. Выйдя " +
                "из метро на Китай-городе, сворачиваем на Варварку и, любуясь " +
                "древними церквями, расположенными по сторонам, идем к мосту через " +
                "Москву-реку. ", R.drawable.kitai, "https://n1s1.elle.ru/2d/77/d4/2d77d4055512ffcf2be99bc3349c8b6d/728x546_1_a2acaed1b8782f5a57226099ae31655b@940x705_0xc35dbb80_7455606241497610704.jpeg"),
        Way("От Цветного бульвара до Китай-города", "11 KM","Если вам нужно совершить покупки в " +
                "корнерах молодых классных брендов и посмотреть Москву — " +
                "вам стоит начинать свой маршрут от Цветного бульвара " +
                "(с ТЦ «Цветной» и «Неглинная») и по Рождественке идти к Кузнецкому " +
                "мосту, где непременно стоит посетить смотровую площадку знаменитого " +
                "Центрального детского магазина.", R.drawable.cvet_blvr, "https://n1s1.elle.ru/a0/e5/e2/a0e5e24850f9bc0f7a082a1936f86864/728x486_1_8e3b0240849409f92dba043ca9db1348@940x627_0xc35dbb80_14293302101497610845.jpeg"),
        //мой комп тут мне очень надоел, потому что медленно работает
        Way("По центру Москвы", "5 КМ", "Прекрасный маршрут длиной 5 километров для тех, " +
                "кто хочет увидеть наиболее известные достопримечательности Москвы и " +
                "параллельно заняться покупками. Начиная свой путь с Театральной площади, " +
                "можно пройти до Лубянской площади и Китай-города.", R.drawable.teatr_plsh, "https://n1s1.elle.ru/26/6b/be/266bbe20fa43c95a87180ee942d9f8e7/728x486_1_3dceaa63eb816c86f5d0c4809c0092f2@940x627_0xc35dbb80_1785947151497609946.jpeg"),
        Way("Парковая полоса", "3 КМ", "Еще один хороший способ сочетать " +
                "культурную программу с наслаждением природой — пройтись" +
                " от Кропоткинской до Нескучного сада. Во-первых, в начале" +
                " пути вы сможете зайти в ГМИИ им. Пушкина, где всегда проходят" +
                " замечательные выставки. Во-вторых, увидите Храм Христа Спасителя " +
                "и самую модную площадку Москвы — территорию бывшей кондитерской фабрики " +
                "«Красный Октябрь» на Берсеневской набережной. ", R.drawable.neskuch_sad, "https://n1s1.elle.ru/8c/dd/a6/8cdda655bff6f003535243c073826e2b/728x486_1_ac27777a5ba932da2c73145b3fe88301@940x627_0xc35dbb80_20860919481497610901.jpeg"),

    )
}