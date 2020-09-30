package mk.djakov.smarthome.ui.gallery

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import mk.djakov.smarthome.data.repository.MainRepository

class GalleryViewModel @ViewModelInject constructor(
    private val repository: MainRepository
) : ViewModel() {


}