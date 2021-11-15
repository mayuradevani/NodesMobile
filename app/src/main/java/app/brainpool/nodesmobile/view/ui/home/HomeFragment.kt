package app.brainpool.nodesmobile.view.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.brainpool.nodesmobile.MainActivity
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.databinding.HomeFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import android.widget.ArrayAdapter
import android.widget.Toast
import dubai.business.womencouncil.data.dataSource.DataServer


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.home_fragment) {

    lateinit var binding: HomeFragmentBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            binding = HomeFragmentBinding.inflate(inflater)
            viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
            binding.ivMap.setOnClickListener {
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            val mainAdapter = HomeGridAdapter(context!!, DataServer.getHomeData())
            binding.gridView.numColumns = 2
            binding.gridView.adapter = mainAdapter
            binding.gridView.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                }
//            Dexter.withContext(context)
//                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//                .withListener(object : PermissionListener {
//                    override fun onPermissionGranted(response: PermissionGrantedResponse) { /* ... */
//                    }
//
//                    override fun onPermissionDenied(response: PermissionDeniedResponse) { /* ... */
//                    }
//
//                    override fun onPermissionRationaleShouldBeShown(
//                        permission: PermissionRequest?,
//                        token: PermissionToken?
//                    ) { /* ... */
//                    }
//                }).check()


            val location = resources.getStringArray(R.array.location)
            val adapter = ArrayAdapter(
                context!!,
                R.layout.item_spinner, location
            )
            binding.spinner.adapter = adapter
            binding.spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
            return binding.root
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return container
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
    }

}