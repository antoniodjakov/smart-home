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
import mk.djakov.smarthome.util.Response

class HomeViewModel @ViewModelInject constructor(
    private val repository: MainRepository
) : ViewModel() {

    val devices = repository.devices

    private val _deviceStatus = MutableLiveData<Response<out RelayResponse>>()
    val deviceStatus: LiveData<Response<out RelayResponse>>
        get() = _deviceStatus

    private val _device = MutableLiveData<Device?>()
    val device: LiveData<Device?>
        get() = _device

    init {
        checkDevicesStatus()
    }

    fun changeDeviceStatus(device: Device) {
        viewModelScope.launch {
            _deviceStatus.postValue(repository.updateStatus(device, !device.state))
        }
    }

    fun checkDevicesStatus() = viewModelScope.launch {
        repository.checkAllDevicesStateAsync()
    }

    fun acknowledgeDeviceStatus() {
//        _deviceOneStatus.value = Response.None()
    }

    fun showDeviceDetails(device: Device) {
        _device.value = device
    }

    fun addDevice(id: Int? = null, name: String, address: String) =
        viewModelScope.launch {
            id?.let {
                repository.updateDevice(id, name, address)
            } ?: repository.addDevice(name, address)
        }

    fun deleteDevice(device: Device) = viewModelScope.launch {
        repository.deleteDevice(device)
    }

    fun acknowledgeDevice() {
        _device.value = null
    }
}