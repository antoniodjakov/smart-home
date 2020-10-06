package mk.djakov.smarthome.ui.home

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import mk.djakov.smarthome.R
import mk.djakov.smarthome.databinding.FragmentHomeBinding
import mk.djakov.smarthome.util.Const
import mk.djakov.smarthome.util.Response

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var viewModel: HomeViewModel
    private var binding: FragmentHomeBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        binding = DataBindingUtil.bind(view)

        binding?.deviceOneStatus?.setOnClickListener { _ ->
            val checked = binding?.deviceOneStatus?.drawable?.constantState ==
                    ContextCompat.getDrawable(requireContext(), R.drawable.light_on)?.constantState
            viewModel.updateValue(Const.DEVICE_ONE, !checked)
            viewModel.setLoadingDeviceOne(true)
        }

        binding?.deviceTwoStatus?.setOnClickListener { _ ->
            val checked = binding?.deviceTwoStatus?.drawable?.constantState ==
                    ContextCompat.getDrawable(requireContext(), R.drawable.light_on)?.constantState
            viewModel.updateValue(Const.DEVICE_TWO, !checked)
            viewModel.setLoadingDeviceTwo(true)
        }

        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.deviceOneValue.observe(viewLifecycleOwner, { response ->
            viewModel.setLoadingDeviceOne(false)
            when (response) {
                is Response.Success ->
                    setDeviceState(binding?.deviceOneStatus, response.data?.state == 1)

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

        viewModel.deviceTwoValue.observe(viewLifecycleOwner, { response ->
            viewModel.setLoadingDeviceTwo(false)
            when (response) {
                is Response.Success ->
                    setDeviceState(binding?.deviceTwoStatus, response.data?.state == 1)

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

        viewModel.deviceOneStatus.observe(viewLifecycleOwner, {
            viewModel.setLoadingDeviceOne(false)
            when (it) {
                is Response.Error -> {
                    Toast.makeText(
                        requireContext(),
                        R.string.status_check_error,
                        Toast.LENGTH_SHORT
                    ).show().also {
                        setDeviceState(binding?.deviceOneStatus, false).also {
                            viewModel.acknowledgeDeviceOneStatus()
                        }
                    }
                }

                else -> {
                }
            }
        })

        viewModel.deviceTwoStatus.observe(viewLifecycleOwner, {
            viewModel.setLoadingDeviceTwo(false)
            when (it) {
                is Response.Error -> {
                    Toast.makeText(
                        requireContext(),
                        R.string.status_check_error,
                        Toast.LENGTH_SHORT
                    ).show().also {
                        setDeviceState(binding?.deviceTwoStatus, false).also {
                            viewModel.acknowledgeDeviceTwoStatus()
                        }
                    }
                }

                else -> {
                }
            }
        })

        viewModel.loadingStatusDeviceOne.observe(viewLifecycleOwner, {
            binding?.deviceOneStatus?.visibility = if (it) View.INVISIBLE else View.VISIBLE
            binding?.deviceOneProgressBar?.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })

        viewModel.loadingStatusDeviceTwo.observe(viewLifecycleOwner, {
            binding?.deviceTwoStatus?.visibility = if (it) View.INVISIBLE else View.VISIBLE
            binding?.deviceTwoProgressBar?.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
    }

    private fun setDeviceState(imageView: ImageView?, checked: Boolean) {
        imageView?.setImageResource(if (checked) R.drawable.light_on else R.drawable.light_off)
    }
}