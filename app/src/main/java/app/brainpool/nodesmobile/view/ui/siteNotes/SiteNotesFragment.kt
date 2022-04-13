package app.brainpool.nodesmobile.view.ui.siteNotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.databinding.SiteNotesFragmentBinding
import app.brainpool.nodesmobile.view.ui.home.HomeViewModel
import com.alcophony.app.ui.core.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class SiteNotesFragment : BaseFragment(R.layout.site_notes_fragment) {

    lateinit var binding: SiteNotesFragmentBinding

    @ExperimentalCoroutinesApi
    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SiteNotesFragmentBinding.inflate(inflater)
        return binding.root
    }

}