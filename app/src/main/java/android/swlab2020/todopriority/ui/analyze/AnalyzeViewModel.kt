package android.swlab2020.todopriority.ui.analyze

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AnalyzeViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is analyze Fragment"
    }
    val text: LiveData<String> = _text
}
