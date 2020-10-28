package mk.djakov.smarthome.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import mk.djakov.smarthome.R
import mk.djakov.smarthome.databinding.FragmentHomeBinding
import mk.djakov.smarthome.util.Response

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var viewModel: HomeViewModel
    private var binding: FragmentHomeBinding? = null
    private var materialAlertDialogBuilder: MaterialAlertDialogBuilder? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        binding = DataBindingUtil.bind(view)

        setHasOptionsMenu(true)

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())

        val adapter = DeviceAdapter({
            //onStatusClick
            viewModel.changeDeviceStatus(it)
        }, {
            //onOptionsClick
        })

        binding?.recyclerView?.adapter = adapter

        activity?.findViewById<FloatingActionButton>(R.id.fab)?.setOnClickListener {
            viewModel.checkDevicesStatus()
        }

        subscribeObservers(adapter)
    }

    private fun subscribeObservers(adapter: DeviceAdapter) {

        viewModel.devices.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        viewModel.deviceValue.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Response.Success ->
                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()

                is Response.Error -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.status_change_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                }
            }
        })

        viewModel.deviceStatus.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Response.Error -> {
                    Toast.makeText(
                        requireContext(),
                        R.string.status_check_error,
                        Toast.LENGTH_SHORT
                    ).show().also {
                        viewModel.acknowledgeDeviceStatus()
                    }
                }

                else -> {
                }
            }
        })
    }

    private fun createAddDeviceDialog() {
        materialAlertDialogBuilder?.let {
            val view =
                LayoutInflater.from(requireContext()).inflate(R.layout.add_new_device_layout, null)
            val name = view.findViewById(R.id.device_name) as TextView
            val tag = view.findViewById(R.id.device_tag) as TextView
            val address = view.findViewById(R.id.device_address) as TextView

            it.setView(view)
                .setCancelable(false)
                .setTitle("Add new device")
                .setPositiveButton("Add") { dialog, _ ->
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
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
}