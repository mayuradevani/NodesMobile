package app.brainpool.nodesmobile.view.ui.login.holding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.databinding.HoldingFragmentBinding
import app.brainpool.nodesmobile.util.gone
import app.brainpool.nodesmobile.util.visible
import com.alcophony.app.ui.core.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HoldingFragment : BaseFragment(R.layout.holding_fragment) {
    lateinit var binding: HoldingFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HoldingFragmentBinding.inflate(inflater)

        binding.rvDidNotReceive.setOnClickListener {
            binding.rvDidNotReceiveClicked.visible()
            binding.rvDidNotReceive.gone()

        }
        binding.rvDidNotReceiveClicked.setOnClickListener {
            binding.rvDidNotReceiveClicked.gone()
            binding.rvDidNotReceive.visible()
        }
        return binding.root
    }
}