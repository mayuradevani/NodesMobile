package app.brainpool.nodesmobile.view.ui.settings

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.databinding.TasksFragmentBinding
import app.brainpool.nodesmobile.view.ui.home.HomeViewModel

class SettingsFragment : Fragment(R.layout.settings_fragment) {


    lateinit var binding: TasksFragmentBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TasksFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
    }

}