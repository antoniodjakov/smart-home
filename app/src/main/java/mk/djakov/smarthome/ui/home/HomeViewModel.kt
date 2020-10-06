package mk.djakov.smarthome.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mk.djakov.smarthome.data.model.RelayResponse
import mk.djakov.smarthome.data.repository.MainRepository
import mk.djakov.smarthome.util.Const
import mk.djakov.smarthome.util.Response

class HomeViewModel @ViewModelInject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _isLoadingDeviceOne = MutableLiveData<Boolean>()
    val loadingStatusDeviceOne: LiveData<Boolean>
        get() = _isLoadingDeviceOne

    private val _isLoadingDeviceTwo = MutableLiveData<Boolean>()
    val loadingStatusDeviceTwo: LiveData<Boolean>
        get() = _isLoadingDeviceTwo

    private val _deviceOneStatus = MutableLiveData<Response<RelayResponse>>()
    val deviceOneStatus: LiveData<Response<RelayResponse>>
        get() = _deviceOneStatus

    private val _deviceTwoStatus = MutableLiveData<Response<RelayResponse>>()
    val deviceTwoStatus: LiveData<Response<RelayResponse>>
        get() = _deviceTwoStatus

    init {
        viewModelScope.launch {
            _deviceOneStatus.postValue(repository.checkState(Const.DEVICE_ONE).also {
                setLoadingDeviceOne(true)
            })

            _deviceTwoStatus.postValue(repository.checkState(Const.DEVICE_TWO).also {
                setLoadingDeviceTwo(true)
            })
        }
    }

    private val _deviceOneValue = MutableLiveData<Response<RelayResponse>>()
    val deviceOneValue: LiveData<Response<RelayResponse>>
        get() = _deviceOneValue

    private val _deviceTwoValue = MutableLiveData<Response<RelayResponse>>()
    val deviceTwoValue: LiveData<Response<RelayResponse>>
        get() = _deviceTwoValue

    fun updateValue(device: String, value: Boolean) = viewModelScope.launch {
        if (device == Const.DEVICE_ONE) {
            _deviceOneValue.postValue(repository.updateValue(device, value))
        } else {
            _deviceTwoValue.postValue(repository.updateValue(device, value))
        }
    }

    fun setLoadingDeviceOne(value: Boolean) {
        _isLoadingDeviceOne.value = value
    }

    fun setLoadingDeviceTwo(value: Boolean) {
        _isLoadingDeviceTwo.value = value
    }
}