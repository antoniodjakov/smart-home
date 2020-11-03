package mk.djakov.smarthome.ui.home

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import mk.djakov.smarthome.R
import mk.djakov.smarthome.data.model.Device
import mk.djakov.smarthome.databinding.FragmentHomeBinding
import mk.djakov.smarthome.util.Const
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
    }

    private fun createAddDeviceDialog(device: Device? = null) {
        materialAlertDialogBuilder?.let {
            val view =
                LayoutInflater.from(requireContext()).inflate(R.layout.add_device_layout, null)
            val label = view.findViewById(R.id.label) as TextView
            val name = view.findViewById(R.id.device_name) as TextInputEditText
            val address = view.findViewById(R.id.device_address) as TextInputEditText
            val gpio = view.findViewById(R.id.device_gpio) as TextInputEditText
            val addButton = view.findViewById(R.id.add_button) as Button
            val cancelButton = view.findViewById(R.id.cancel_button) as MaterialButton

            device?.let {
                label.text = getString(R.string.update_device)
                name.setText(device.name)
                address.setText(device.address)
                gpio.setText(device.gpio.toString())
                addButton.text = getString(R.string.update_device)
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
                            gpio = if (TextUtils.isEmpty(gpio.text)) Const.GPIO
                            else gpio.text.toString().toInt()
                        )
                    } ?: viewModel.addDevice(
                        id = null,
                        name = name.text.toString(),
                        address = address.text.toString(),
                        gpio = if (TextUtils.isEmpty(gpio.text)) Const.GPIO
                        else gpio.text.toString().toInt()
                    )
                    alertDialog.dismiss()
                    viewModel.acknowledgeDevice()
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
            R.id.action_add -> true.also {
                createAddDeviceDialog()
            }
            else -> false
        }

    override fun onDestroyView() {
        viewModel.updateDeviceList()
        _binding = null
        super.onDestroyView()
    }
}