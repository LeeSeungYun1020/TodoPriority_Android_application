package android.swlab2020.todopriority.data

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class FragmentViewModel(application: Application) : AndroidViewModel(application) {
    var fabVisibility: MutableLiveData<Boolean> = MutableLiveData()
    var addTask: MutableLiveData<Intent> = MutableLiveData()
    var addProject: MutableLiveData<Intent> = MutableLiveData()
    var updateTask: MutableLiveData<Intent> = MutableLiveData()
    var updateProject: MutableLiveData<Intent> = MutableLiveData()
    var navigateAnalyze: MutableLiveData<Int> = MutableLiveData(-1)
}