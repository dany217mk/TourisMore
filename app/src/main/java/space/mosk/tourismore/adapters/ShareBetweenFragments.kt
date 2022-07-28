package space.mosk.tourismore.adapters

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShareBetweenFragments : ViewModel() {
    val index = MutableLiveData<Int>()
    val name = MutableLiveData<String>()
    fun sendIndex(ind: Int){
        index.value = ind
    }
    fun sendServiceName(_name : String){
        name.value = _name
    }
}
