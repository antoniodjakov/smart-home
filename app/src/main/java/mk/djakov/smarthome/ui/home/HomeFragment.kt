package mk.djakov.smarthome.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import mk.djakov.smarthome.R
import mk.djakov.smarthome.databinding.FragmentHomeBinding
import mk.djakov.smarthome.util.Response

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var homeViewModel: HomeViewModel
    private var binding: FragmentHomeBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        binding = DataBindingUtil.bind(view)

        binding?.deviceOneSwitch?.setOnCheckedChangeListener { compoundButton, checked ->
            if(binding?.deviceOneSwitch?.isChecked != checked) {
                homeViewModel.updateValue("device_1", checked)
            }
        }

        subscribeObservers()
    }

    private fun subscribeObservers() {
        homeViewModel.deviceOneValue.observe(viewLifecycleOwner, {
            when (it) {
                is Response.Success -> Toast.makeText(
                    requireContext(),
                    "Успешно променет статус",
                    Toast.LENGTH_SHORT
                ).show()

                is Response.Error -> Toast.makeText(
                    requireContext(),
                    "Грешка при промена на статус",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        homeViewModel.deviceOneStatus.observe(viewLifecycleOwner, {
            when (it) {
                is Response.Success -> {
                    binding?.deviceOneSwitch?.isChecked = it.data?.state == 1
                }

                is Response.Error -> Toast.makeText(
                    requireContext(),
                    "Грешка при проверка на статус",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}