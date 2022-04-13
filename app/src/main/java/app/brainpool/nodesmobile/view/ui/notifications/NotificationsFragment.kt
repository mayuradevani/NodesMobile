package app.brainpool.nodesmobile.view.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.databinding.NotificationsFragmentBinding
import app.brainpool.nodesmobile.view.ui.home.HomeViewModel
import com.alcophony.app.ui.core.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class NotificationsFragment : BaseFragment(R.layout.notifications_fragment) {

    lateinit var binding: NotificationsFragmentBinding
    @ExperimentalCoroutinesApi
    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = NotificationsFragmentBinding.inflate(inflater)
        return binding.root
    }

}