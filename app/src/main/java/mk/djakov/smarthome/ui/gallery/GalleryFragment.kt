package mk.djakov.smarthome.ui.gallery

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import mk.djakov.smarthome.R
import mk.djakov.smarthome.databinding.FragmentGalleryBinding

@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_gallery) {

    private lateinit var galleryViewModel: GalleryViewModel
    private var binding: FragmentGalleryBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        galleryViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)
        binding = DataBindingUtil.bind(view)

        binding?.forwardButton?.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    println("Forward button pressed")
                    view.background.setColorFilter(-0x1f0b8adf, PorterDuff.Mode.SRC_ATOP)
                    view.invalidate()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    println("Forward button released")
                    view.background.clearColorFilter()
                    view.invalidate()
                    true
                }

            }
            false
        }

    }
}