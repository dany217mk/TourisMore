package space.mosk.tourismore.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import java.sql.Date

data class LastMessage(
    @Exclude var uid:String? = "",
    var lastMsg : String = "",
    val lastMsgTime: Any = ServerValue.TIMESTAMP,
    var user_id:String? = ""){
    fun timestampDate(): Date = Date(lastMsgTime as Long)
}
