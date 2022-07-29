package space.mosk.tourismore.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import java.sql.Date

data class FeedPost(
    val uid: String = "",
    val name: String = "",
    val surname: String = "",
    val image: String = "",
    val caption: String = "",
    val longitude: String = "",
    val latitude: String = "",
    val width: String = "",
    val timestamp: Any = ServerValue.TIMESTAMP,
    val profileImage: String = "",
    @Exclude val id: String =""){
    fun timestampDate(): Date = Date(timestamp as Long)
}