package space.mosk.tourismore.models

import com.google.firebase.database.Exclude

data class User (
    @Exclude var uid:String? = "",
    var name:String? = "",
    var surname:String? = "",
    var date_of_birth:String? = "",
    var gender:String? = "",
    var phoneNumber:String?= "",
    var profileImage:String?= "",
    val follows: Map<String, Boolean> = emptyMap(),
    val followers: Map<String, Boolean> = emptyMap(),
)