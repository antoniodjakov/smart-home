package mk.djakov.smarthome.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import mk.djakov.smarthome.R
import mk.djakov.smarthome.data.model.Device

@BindingAdapter("setCustomImage")
fun ImageView.setCustomImage(device: Device) {
    setImageResource(
        when (device.command) {
            Data.commands[0] -> if (device.state) R.drawable.switch_on else R.drawable.switch_off

            Data.commands[1],
            Data.commands[2],
            Data.commands[3] -> R.drawable.ic_play
            else -> {
                if (device.state) R.drawable.switch_on else R.drawable.switch_off
            }
        }
    )
}

@BindingAdapter("setOptionsText")
fun TextView.setOptionsText(device: Device) {
    if (device.command == Data.commands[0]) {
        visibility = View.GONE
    } else {
        val suffix = if (device.command == Data.commands[2]) "s" else "ms"
        val state = if (device.commandStatus == 1) "ON" else "OFF"
        visibility = View.VISIBLE
        text = "$state for ${device.duration} $suffix"
    }
}