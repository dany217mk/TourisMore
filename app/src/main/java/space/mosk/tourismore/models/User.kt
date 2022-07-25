package space.mosk.tourismore.models
class User {
    var uid:String? = null
    var name:String? = null
    var surname:String? = null
    var date_of_birth:String? = null
    var gender:String? = null
    var phoneNumber:String?= null
    var profileImage:String?= null
    constructor(uid: String?, name: String?, surname: String?, date_of_birth: String?, gender: String?, phoneNumber: String?, profileImage: String?){
        this.uid = uid
        this.name = name
        this.surname = surname
        this.date_of_birth = date_of_birth
        this.gender = gender
        this.phoneNumber = phoneNumber
        this.profileImage = profileImage
    }
}