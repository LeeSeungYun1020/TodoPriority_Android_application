package android.swlab2020.todopriority.ui.matrix

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MatrixViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is matrix Fragment"
    }
    val text: LiveData<String> = _text
}