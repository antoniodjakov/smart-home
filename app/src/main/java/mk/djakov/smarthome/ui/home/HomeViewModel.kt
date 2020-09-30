package mk.djakov.smarthome.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mk.djakov.smarthome.data.model.RelayResponse
import mk.djakov.smarthome.data.repository.MainRepository
import mk.djakov.smarthome.util.Response

class HomeViewModel @ViewModelInject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _deviceOneStatus = MutableLiveData<Response<RelayResponse>>()
    val deviceOneStatus: LiveData<Response<RelayResponse>>
        get() = _deviceOneStatus

    init {
        viewModelScope.launch {
            _deviceOneStatus.postValue(repository.checkState(""))
        }
    }

    private val _deviceOneValue = MutableLiveData<Response<RelayResponse>>()
    val deviceOneValue: LiveData<Response<RelayResponse>>
        get() = _deviceOneValue

    fun updateValue(device: String, value: Boolean) = viewModelScope.launch {
        _deviceOneValue.postValue(repository.updateValue(device, value))
    }
}