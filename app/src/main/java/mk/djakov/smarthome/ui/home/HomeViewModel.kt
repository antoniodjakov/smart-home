package mk.djakov.smarthome.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mk.djakov.smarthome.data.model.Device
import mk.djakov.smarthome.data.model.RelayResponse
import mk.djakov.smarthome.data.repository.MainRepository
import mk.djakov.smarthome.util.Data
import mk.djakov.smarthome.util.Response

class HomeViewModel @ViewModelInject constructor(
    private val repository: MainRepository
) : ViewModel() {

    val devices = repository.devices

    private val _isLoading = MutableLiveData<Boolean>()
    val loadingStatus: LiveData<Boolean>
        get() = _isLoading

    private val _deviceStatus = MutableLiveData<Response<RelayResponse>>()
    val deviceStatus: LiveData<Response<RelayResponse>>
        get() = _deviceStatus

    private val _deviceValue = MutableLiveData<Response<RelayResponse>>()
    val deviceValue: LiveData<Response<RelayResponse>>
        get() = _deviceValue

    init {
        viewModelScope.launch {
            repository.insertAll(Data.devices)
            checkDevicesStatus()
        }
    }

    fun changeDeviceStatus(device: Device) {
        viewModelScope.launch {
            repository.updateStatus(device, !device.state)
        }
    }

    fun checkDevicesStatus() = viewModelScope.launch {
        repository.checkAllDevicesStateAsync()
    }

    fun acknowledgeDeviceStatus() {
//        _deviceOneStatus.value = Response.None()
    }
}