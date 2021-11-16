package app.brainpool.nodesmobile.view.ui.home

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import app.brainpool.nodesmobile.MainActivity
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.Splash
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.databinding.HomeFragmentBinding
import app.brainpool.nodesmobile.model.HomeListItem
import app.brainpool.nodesmobile.view.ui.home.adapter.HomeListAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.AndroidEntryPoint
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
            binding.tvLogout.setOnClickListener {
                Prefs.putString(PrefsKey.AUTH_KEY, "")
                val intent = Intent(activity, Splash::class.java)
                startActivity(intent)
                activity?.finish()
            }
            binding.ivMap.setOnClickListener {
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            binding.recyclerView.apply {
                hasFixedSize()
                layoutManager = GridLayoutManager(context, 2)
                adapter = HomeListAdapter(DataServer.getHomeData()).also { adapter ->
                    adapter.setOnItemClickListener(listener = OnItemClickListener { adapter, view, position ->
                        val homeListItem = adapter?.getItem(position) as HomeListItem
                        itemClickListener(homeListItem)
                    })
                }
            }

            Dexter.withContext(context)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) { /* ... */
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) { /* ... */
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) { /* ... */
                    }
                }).check()


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

    private fun itemClickListener(homeListItem: HomeListItem) {
        when (homeListItem.title) {
//            "MAP" -> navController.navigate(R.id.map)
        }
    }
}