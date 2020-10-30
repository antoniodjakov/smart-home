package mk.djakov.smarthome.ui.home

import android.os.Bundle
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import mk.djakov.smarthome.R
import mk.djakov.smarthome.data.model.Device
import mk.djakov.smarthome.databinding.FragmentHomeBinding
import mk.djakov.smarthome.util.Helper
import mk.djakov.smarthome.util.Helper.toast
import mk.djakov.smarthome.util.Response

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var viewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var materialAlertDialogBuilder: MaterialAlertDialogBuilder? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = DataBindingUtil.bind(view)

        setHasOptionsMenu(true)

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())

        val adapter = DeviceAdapter({
            //onStatusClick
            viewModel.changeDeviceStatus(it)
        }, { (device, view) ->
            //onOptionsClick
            showMenu(view, device)
        })

        binding.recyclerView.adapter = adapter

        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP or ItemTouchHelper.DOWN) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPosition = viewHolder.adapterPosition
                    val toPosition = target.adapterPosition

                    adapter.notifyItemMoved(fromPosition, toPosition)
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
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        activity?.findViewById<FloatingActionButton>(R.id.fab)?.setOnClickListener {
            viewModel.checkDevicesStatus()
        }

        subscribeObservers(adapter)
    }

    private fun subscribeObservers(adapter: DeviceAdapter) {

        viewModel.devices.observe(viewLifecycleOwner, { adapter.submitList(it) })

        viewModel.deviceStatus.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Response.Success -> requireContext().toast(getString(R.string.status_change_success))

                is Response.Error -> requireContext().toast(getString(R.string.status_change_error))

                else -> {
                }
            }
        })

        viewModel.device.observe(viewLifecycleOwner, { device ->
            device?.let {
                createAddDeviceDialog(it).also {
                    viewModel.acknowledgeDevice()
                }
            }
        })
    }

    private fun createAddDeviceDialog(device: Device? = null) {
        materialAlertDialogBuilder?.let {
            val view =
                LayoutInflater.from(requireContext()).inflate(R.layout.add_device_layout, null)
            val name = view.findViewById(R.id.device_name) as TextView
            val address = view.findViewById(R.id.device_address) as TextView
            val addButton = view.findViewById(R.id.add_button) as Button
            val cancelButton = view.findViewById(R.id.cancel_button) as MaterialButton

            device?.let {
                name.text = device.name
                address.text = device.address
                addButton.text = getString(R.string.update_device)
                if (device.address.startsWith("http://")) {
                    (view.findViewById(R.id.address_text_input) as TextInputLayout).prefixText = ""
                }
            }

            val alertDialog = it.setView(view)
                .setCancelable(false)
                .show()

            addButton.setOnClickListener {
                if (isInputValid(name.text.toString(), address.text.toString())) {
                    device?.let {
                        viewModel.addDevice(
                            device.id,
                            name.text.toString(),
                            Helper.formatAddress(address.text.toString())
                        )
                    } ?: viewModel.addDevice(
                        null,
                        name.text.toString(),
                        Helper.formatAddress(address.text.toString())
                    )
                    alertDialog.dismiss()
                }
            }

            cancelButton.setOnClickListener {
                alertDialog.dismiss()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                createAddDeviceDialog()
                return true
            }
        }
        return false
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}