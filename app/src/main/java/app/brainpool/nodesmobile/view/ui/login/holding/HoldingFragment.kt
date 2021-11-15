package app.brainpool.nodesmobile.view.ui.login.holding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.brainpool.nodesmobile.MainActivity
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.databinding.HoldingFragmentBinding
import app.brainpool.nodesmobile.util.gone
import app.brainpool.nodesmobile.util.materialDialog
import app.brainpool.nodesmobile.util.visible
import app.brainpool.nodesmobile.view.ui.home.HomeActivity

class HoldingFragment : Fragment(R.layout.holding_fragment) {
    lateinit var binding: HoldingFragmentBinding
    lateinit var viewModel: HoldingViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HoldingFragmentBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(HoldingViewModel::class.java)

        binding.rvDidNotReceive.setOnClickListener {
            binding.rvDidNotReceiveClicked.visible()
            binding.rvDidNotReceive.gone()

        }
        binding.rvDidNotReceiveClicked.setOnClickListener {
//            binding.rvDidNotReceiveClicked.gone()
//            binding.rvDidNotReceive.visible()
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
        return binding.root
    }
}