package mk.djakov.smarthome.ui.home

import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.Group
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import mk.djakov.smarthome.R
import mk.djakov.smarthome.data.model.Device
import mk.djakov.smarthome.databinding.FragmentHomeBinding
import mk.djakov.smarthome.util.Const
import mk.djakov.smarthome.util.Data
import mk.djakov.smarthome.util.Helper.swapDevices
import mk.djakov.smarthome.util.Helper.toast
import mk.djakov.smarthome.util.Response

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var viewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: DeviceAdapter
    private var materialAlertDialogBuilder: MaterialAlertDialogBuilder? = null
    private lateinit var commandAdapter: ArrayAdapter<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = DataBindingUtil.bind(view)

        setHasOptionsMenu(true)

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())

        adapter = DeviceAdapter({
            //onStatusClick
            viewModel.changeDeviceStatus(it)
        }, { (device, view) ->
            //onOptionsClick
            showMenu(view, device)
        })

        commandAdapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_menu_popup_item,
            Data.commands
        )

        binding.recyclerView.adapter = adapter

        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP or ItemTouchHelper.DOWN) {
                var list: ArrayList<Device> = arrayListOf()
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    list = ArrayList(adapter.currentList)
                    val fromPosition = viewHolder.adapterPosition
                    val toPosition = target.adapterPosition

//                    TODO use this in future, it has a better animation than DB update
//                    adapter.notifyItemMoved(fromPosition, toPosition)

                    list.swapDevices(fromPosition, toPosition)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) = makeFlag(
                    ItemTouchHelper.ACTION_STATE_DRAG,
                    ItemTouchHelper.DOWN or ItemTouchHelper.UP or ItemTouchHelper.START or ItemTouchHelper.END
                )

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)

                    // Item dropped
                    if (actionState == SCROLL_STATE_IDLE) {
                        viewModel.saveDeviceOrder(list).also {
                            viewModel.updateDeviceList()
                        }
                    }
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        activity?.findViewById<FloatingActionButton>(R.id.fab)?.setOnClickListener {
            createAddDeviceDialog()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.checkDevicesStatus()
        }

        subscribeObservers(adapter)
    }

    private fun subscribeObservers(adapter: DeviceAdapter) {

        viewModel.devices.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        viewModel.deviceStatus.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Response.Success -> requireContext().toast(getString(R.string.status_change_success))
                    .also { viewModel.acknowledgeDeviceStatus() }

                is Response.Error -> {
                    requireContext().toast(
                        when (response.message) {
                            Const.NO_INTERNET_CONNECTION -> getString(R.string.no_internet_connection)
                            Const.BAD_REQUEST -> getString(R.string.bad_request)
                            else -> getString(R.string.status_change_error)
                        }.also { viewModel.acknowledgeDeviceStatus() }
                    )
                }

                else -> {
                }
            }
        })

        viewModel.device.observe(viewLifecycleOwner, { device ->
            device?.let { createAddDeviceDialog(it) }
        })

        viewModel.status.observe(viewLifecycleOwner, {
            binding.swipeRefreshLayout.isRefreshing = false
        })
    }

    private fun createAddDeviceDialog(device: Device? = null) {
        materialAlertDialogBuilder?.let {
            val view =
                LayoutInflater.from(requireContext()).inflate(R.layout.add_device_layout, null)
            val label = view.findViewById(R.id.label) as TextView
            val name = view.findViewById(R.id.device_name) as TextInputEditText
            val address = view.findViewById(R.id.device_address) as TextInputEditText
            val command = view.findViewById(R.id.command_spinner) as AppCompatSpinner
            val gpio = view.findViewById(R.id.device_gpio) as TextInputEditText
            val duration = view.findViewById(R.id.device_duration) as TextInputEditText
            val durationLayout = view.findViewById(R.id.duration_text_input) as TextInputLayout
            val onChip = view.findViewById(R.id.on_chip) as Chip
            val offChip = view.findViewById(R.id.off_chip) as Chip
            val pulseGroup = view.findViewById(R.id.pulse_group) as Group
            val addButton = view.findViewById(R.id.add_button) as Button
            val cancelButton = view.findViewById(R.id.cancel_button) as MaterialButton

            command.apply {
                adapter = commandAdapter
                setSelection(device?.let { Data.commands.indexOf(device.command) } ?: 0)
            }

            command.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    when (position) {
                        0 -> pulseGroup.visibility = View.GONE

                        1, 3 -> pulseGroup.visibility = View.VISIBLE.also {
                            durationLayout.apply {
                                suffixText = "ms"
                                suffixTextView.updateLayoutParams {
                                    height = ViewGroup.LayoutParams.MATCH_PARENT
                                    prefixTextView.gravity = Gravity.CENTER
                                }
                            }
                        }

                        2 -> pulseGroup.visibility = View.VISIBLE.also {
                            durationLayout.apply {
                                suffixText = "s"
                                suffixTextView.updateLayoutParams {
                                    height = ViewGroup.LayoutParams.MATCH_PARENT
                                    prefixTextView.gravity = Gravity.CENTER
                                }
                            }
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }

            device?.let {
                label.text = getString(R.string.update_device)
                name.setText(device.name)
                address.setText(device.address)
                gpio.setText(device.gpio.toString())
                addButton.text = getString(R.string.update_device)
                command.dropDownHorizontalOffset = Data.commands.indexOf(device.command)
                when (device.command) {
                    Data.commands[0] -> pulseGroup.visibility = View.GONE

                    Data.commands[1], Data.commands[3] -> {
                        pulseGroup.visibility = View.VISIBLE
                        duration.setText(device.duration.toString())
                        durationLayout.suffixText = "ms"
                        if (device.commandStatus == 1) onChip.isChecked =
                            true else offChip.isChecked = true
                    }

                    Data.commands[2] -> {
                        pulseGroup.visibility = View.VISIBLE
                        duration.setText(device.duration.toString())
                        durationLayout.suffixText = "s"
                        if (device.commandStatus == 1) onChip.isChecked =
                            true else offChip.isChecked = true
                    }
                }
            } ?: let { label.text = getString(R.string.add_new_device) }

            val alertDialog = it.setView(view)
                .setCancelable(false)
                .show()

            addButton.setOnClickListener {
                if (isInputValid(name.text.toString(), address.text.toString())) {
                    device?.let {
                        viewModel.addDevice(
                            id = device.id,
                            name = name.text.toString(),
                            address = address.text.toString(),
                            gpio = if (TextUtils.isEmpty(gpio.text)) Const.GPIO else gpio.text.toString()
                                .toInt(),
                            command = command.selectedItem.toString(),
                            commandStatus = if (offChip.isChecked) 0 else 1,
                            duration = if (!TextUtils.isEmpty(duration.text)) duration.text.toString()
                                .toInt() else 0
                        )
                    } ?: viewModel.addDevice(
                        id = null,
                        name = name.text.toString(),
                        address = address.text.toString(),
                        gpio = if (TextUtils.isEmpty(gpio.text)) Const.GPIO else gpio.text.toString()
                            .toInt(),
                        command = command.selectedItem.toString(),
                        commandStatus = if (offChip.isChecked) 0 else 1,
                        duration = if (!TextUtils.isEmpty(duration.text)) duration.text.toString()
                            .toInt() else 0
                    )
                    alertDialog.dismiss()
                    viewModel.acknowledgeDevice()
                } else {
                    requireContext().toast(getString(R.string.invalid_input))
                }
            }

            cancelButton.setOnClickListener {
                alertDialog.dismiss()
                viewModel.acknowledgeDevice()
            }
        }
    }

    private fun isInputValid(name: String, address: String) =
        name.isNotEmpty() && address.isNotEmpty()

    private fun showMenu(anchor: View, device: Device) =
        PopupMenu(requireContext(), anchor).apply {
            menuInflater.inflate(R.menu.popup_menu, this.menu)
            gravity = Gravity.END
            show()
        }.also {
            it.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> {
                        viewModel.showDeviceDetails(device)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.action_delete -> {
                        viewModel.deleteDevice(device)
                        return@setOnMenuItemClickListener true
                    }
                    else -> return@setOnMenuItemClickListener false
                }
            }
        }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_refresh -> true.also {
                viewModel.checkDevicesStatus()
                binding.swipeRefreshLayout.isRefreshing = true
            }
            else -> false
        }

    override fun onDestroyView() {
        viewModel.updateDeviceList()
        _binding = null
        super.onDestroyView()
    }
}