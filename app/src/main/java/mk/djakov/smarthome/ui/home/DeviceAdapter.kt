package mk.djakov.smarthome.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mk.djakov.smarthome.R
import mk.djakov.smarthome.data.model.Device
import mk.djakov.smarthome.databinding.DeviceItemBinding

class DeviceAdapter(
    val deviceStatusClickListener: (Device) -> Unit,
    val deviceOptionsClickListener: (Pair<Device, View>) -> Unit
) : ListAdapter<Device, DeviceAdapter.ViewHolder>(DiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = getItem(position)
        holder.apply {
            bind(
                createDeviceStatusClickListener(device),
                createDeviceOptionsClickListener(
                    device,
                    holder.itemView.findViewById(R.id.device_options)
                ),
                device
            )
            itemView.tag = device
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DeviceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    private fun createDeviceStatusClickListener(device: Device): View.OnClickListener {
        return View.OnClickListener { deviceStatusClickListener(device) }
    }

    private fun createDeviceOptionsClickListener(device: Device, view: View): View.OnClickListener {
        return View.OnClickListener { deviceOptionsClickListener(Pair(device, view)) }
    }

    class ViewHolder(private val binding: DeviceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            statusClickListener: View.OnClickListener,
            optionsClickListener: View.OnClickListener,
            item: Device
        ) {
            binding.apply {
                device = item
                onStatusClickListener = statusClickListener
                onOptionsClickListener = optionsClickListener
            }
        }
    }
}

private class DiffCallback : DiffUtil.ItemCallback<Device>() {

    override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
        return oldItem == newItem
    }
}