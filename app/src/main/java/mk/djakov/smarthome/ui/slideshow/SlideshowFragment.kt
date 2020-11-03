package mk.djakov.smarthome.ui.slideshow

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import mk.djakov.smarthome.R
import mk.djakov.smarthome.databinding.FragmentSlideshowBinding

@AndroidEntryPoint
class SlideshowFragment : Fragment(R.layout.fragment_slideshow) {

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = DataBindingUtil.bind(view)!!

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}