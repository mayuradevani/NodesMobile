package app.brainpool.nodesmobile.view.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
    }

}