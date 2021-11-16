package app.brainpool.nodesmobile.view.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.databinding.MapFragmentBinding
import app.brainpool.nodesmobile.view.ui.home.HomeViewModel

class MapFragment : Fragment(R.layout.map_fragment) {


    lateinit var binding: MapFragmentBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MapFragmentBinding.inflate(inflater)
        binding.tvThermal.setOnClickListener {
            if (binding.tvThermal.text == "Base") {
                binding.tvThermal.text = "Thermal"
                binding.tvThermal.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.orange
                    )
                )
            } else {
                binding.tvThermal.text = "Base"
                binding.tvThermal.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.green_text
                    )
                )
            }
        }
        binding.ivUpDown.setOnClickListener { binding.tvThermal.performClick() }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
    }

}