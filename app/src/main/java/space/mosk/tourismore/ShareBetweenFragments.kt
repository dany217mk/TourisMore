package space.mosk.tourismore

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShareBetweenFragments : ViewModel() {
    val index = MutableLiveData<Int>()
    fun sendIndex(ind: Int){
        index.value = ind
    }
}
