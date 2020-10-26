package mk.djakov.smarthome.ui.home

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import mk.djakov.smarthome.R
import mk.djakov.smarthome.databinding.FragmentHomeBinding
import mk.djakov.smarthome.util.Response

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var viewModel: HomeViewModel
    private var binding: FragmentHomeBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        binding = DataBindingUtil.bind(view)

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
}