package space.mosk.tourismore.models

import com.google.firebase.database.ServerValue
import java.sql.Date
import java.sql.Timestamp

data class FeedPost (val  uid: String = "",
                     val name: String = "",
                     val surname: String = "",
                     val image: String = "",
                     val likesCount: Int = 0,
                     val caption: String = "",
                     val longitude: String = "",
                     val width: String = "",
                     val timestamp: Any = ServerValue.TIMESTAMP,
                     val profileImage: String = ""){
    fun timestampDate(): Date = Date(timestamp as Long)
}