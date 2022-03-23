package app.brainpool.nodesmobile.view.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.data.localdatastore.Property
import app.brainpool.nodesmobile.data.localdatastore.UserNodes
import app.brainpool.nodesmobile.data.models.HomeListItem
import app.brainpool.nodesmobile.databinding.HomeFragmentBinding
import app.brainpool.nodesmobile.util.*
import app.brainpool.nodesmobile.view.ui.MainActivity
import com.alcophony.app.ui.core.BaseFragment
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.home_fragment) {
    lateinit var binding: HomeFragmentBinding
    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var userNodes: UserNodes
    var pId: String = ""

    //    var proprtyList: List<Property> = ArrayList()
    lateinit var adapter: ArrayAdapter<Property>
    private var pIdLastSelected: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            binding = HomeFragmentBinding.inflate(inflater)
            binding.btnStart.setOnClickListener {
                binding.btnStart.text = getString(R.string.please_wait)
                goToMain(pId)
            }
//            binding.ivMap.setOnClickListener {
//                goToMain()
//            }
//            binding.recyclerView.apply {
//                hasFixedSize()
//                layoutManager = GridLayoutManager(context, 2)
//                adapter = HomeListAdapter(DataServer.getHomeData())
//            }
            binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    pId = adapter.getItem(position)?.id.toString()
//                    if (p != null)
//                        if (WifiService.instance.isOnline()) {
//                            viewModel.getAllMapsByPropertyId(
//                                requireContext(),
//                                p.id.toString()
//                            )
//                        } else
//                            p.let { viewModel.getAllMapsByPropertyId(it) }
                    binding.btnStart.visible()
                }
            }

            if (WifiService.instance.isOnline()) {
                viewModel.getUserProfile(requireContext())
            } else {
                viewModel.getUserProfileLocal()
            }
            binding.btnStart.gone()

            observeLiveData()
            return binding.root
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return container
    }

    private fun setPushNotificationToken(user: UserNodes) {
        FirebaseMessaging.getInstance()
            .token.addOnSuccessListener {
                viewModel.updateOrStoreNotificationToken(requireContext(), it, user)
            }
    }

    override fun onResume() {
        super.onResume()
        binding.btnStart.text = getString(R.string.log_in)
    }

    private fun goToMain(pId: String) {
//        val arguments = Bundle()
//        arguments.putString("appnotification", (messageBody ?: "{}").toString())

        val args = JSONObject()
        args.put("propertyId", pId)
        val noti = args.toString() ?: "{}"
        startActivity(Intent(context, MainActivity::class.java).putExtra("appnotification", noti))
//        activity?.navigateWithExtra<MainActivity>(pId)
    }

    private fun observeLiveData() {
        observeViewState(viewModel.main, binding.fetchProgress) { response ->
            if (response != null) {
                if (response.data != null) {
                    Prefs.putBoolean(PrefsKey.SENT_TOKEN, true)
                    Log.v(GlobalVar.TAG, "Token sent:" + response.data)
                }
                binding.fetchProgress.gone()
            }
        }
        observeViewState(viewModel.userProfile, binding.fetchProgress) { user ->
            if (user != null) {
                userNodes = user
//                if (user.licenseNumberId?.isNotEmpty() == true) {
//                    Prefs.putString(PrefsKey.LICENCE_NUMBER_ID, user.licenseNumberId)
//                    Prefs.putString(PrefsKey.LICENCE_NUMBER_NAME, user.licenseNumberName)
//                }
//                Prefs.putString(PrefsKey.IMEI, user.imei)
                Firebase.crashlytics.setUserId(user.imei.toString())
//                Prefs.putString(PrefsKey.TIME_INTERVAL, user.timeInterval.toString())
//                Prefs.putString(PrefsKey.RADIUS, user.radius.toString())
//                Prefs.putString(PrefsKey.USER_ID, user.id)
                val name = user.firstname + " " + user.lastname
//                Prefs.putString(PrefsKey.NAME, name)
                binding.tvUserName.text = name
//                var role = user.role
                binding.tvRole.text = user.role
//                Prefs.putString(PrefsKey.ROLE, role)
                pIdLastSelected = user.defPropertyId
//                Prefs.putString(PrefsKey.DEF_PROPERTY_ID, pIdLastSelected)
                if (!Prefs.getBoolean(PrefsKey.SENT_TOKEN, false)) {
                    setPushNotificationToken(userNodes)
                }
                if (WifiService.instance.isOnline()) {
                    viewModel.getAllProperties(requireContext())
                } else {
                    viewModel.getAllPropertiesLocal()
                }

            }
        }
        observeViewState(viewModel.getAllProperties, binding.fetchProgress) { proprtyList ->
            if (proprtyList != null) {
//                this.proprtyList = proprtyList
//                    val json = proprtyList?.toJson()
//                    Prefs.putString(PrefsKey.PROPERTIES, json)
//                val db = Realm.getDefaultInstance()
////                    db.deleteAll()
//                for (p in proprtyList) {
//                    viewModel.insert(Property().apply {
//                        id = p?.id.toString()
//                        name = p?.name.toString()
//                    })
////                        db.executeTransactionAsync {
////                            Property().apply {
////                                id = p?.id.toString()
////                                name = p?.name.toString()
////                            }.save()
////                        }
//                }
                setPropertiesSpinner(proprtyList)
            }
        }

//        observeViewState(viewModel.getAllMapsByPropertyId, binding.fetchProgress) { response ->
//            binding.btnStart.visible()
////            if (response != null) {
////                val proprtyList = response.data?.getAllMapsByPropertyId
////                if (proprtyList != null) {
////                    for (p in proprtyList) {
////                        if (p?.isDefault == true) {
////                            if (!p.mapTileFile?.filename.isNullOrEmpty()) {
////                                Prefs.putString(
////                                    PrefsKey.MAP_TILE_FILE_NAME,
////                                    p.mapTileFile?.filename
////                                )
////                                var folder = ""
////                                if (p.mapTileFile?.filename?.contains(".jpg") == true)
////                                    folder = p.mapTileFile.filename.split(".jpg").get(0)
////                                else
////                                    folder = p.mapTileFile?.filename?.split(".tiff")?.get(0)
////                                        .toString()
////
////                                Prefs.putString(
////                                    PrefsKey.MAP_TILE_FOLDER,
////                                    folder
////                                )
////
////                                //center
////                                Prefs.putDouble(
////                                    PrefsKey.MAP_CENTER_LATI,
////                                    p.center?.coordinates?.get(1)!!
////                                )
////                                Prefs.putDouble(
////                                    PrefsKey.MAP_CENTER_LONGI,
////                                    p.center?.coordinates?.get(0)!!
////                                )
////
//////                                    //south west
//////                                    Prefs.putDouble(
//////                                        PrefsKey.MAP_SOUTHWEST_LATI,
//////                                        p.center?.coordinates?.get(1)!!
//////                                    )
//////                                    Prefs.putDouble(
//////                                        PrefsKey.MAP_SOUTHWEST_LONGI,
//////                                        p.center?.coordinates?.get(0)!!
//////                                    )
//////
//////                                    //north east
//////                                    Prefs.putDouble(
//////                                        PrefsKey.MAP_NORTHEAST_LATI,
//////                                        p.center?.coordinates?.get(1)!!
//////                                    )
//////                                    Prefs.putDouble(
//////                                        PrefsKey.MAP_NORTHEAST_LONGI,
//////                                        p.center?.coordinates?.get(0)!!
//////                                    )
////                                binding.btnStart.visible()
////                            }
////                        }
////                    }
////                }
////            }
//        }
    }

    private fun setPropertiesSpinner(proprtyList: MutableList<Property>) {
        if (proprtyList.isNotEmpty()) {
            adapter = ArrayAdapter(
                requireContext(),
                R.layout.item_spinner, proprtyList
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spinner.adapter = adapter
            for (position in 0 until adapter.count) {
                if (adapter.getItem(position)?.id ?: 0 == userNodes.defPropertyId) {
                    binding.spinner.setSelection(position, false)
                    return
                }
            }


//        if (proprtyList != null) {
//            val db = Realm.getDefaultInstance()
//            db.executeTransactionAsync {
//                Realm.getDefaultInstance().where(Property::class.java).findAll()
//                    .map { proprtyList }
//                for ((i, p) in proprtyList!!.withIndex()) {
//                    locationArray.add(p?.name.toString())
//                    if (pIdLastSelected == p?.id)
//                        itemClick = i
//                }
//                val adapter = ArrayAdapter(
//                    requireContext(),
//                    R.layout.item_spinner, locationArray
//                )
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                binding.spinner.adapter = adapter
//                binding.spinner.setSelection(itemClick, false)
//            }
//
//
//
//            val locationArray = mutableListOf<String>()
//            var itemClick = 0
//
//            try {
//                proprtyList = viewModel.getAllProperties()
//
//                val adapter = ArrayAdapter(
//                    requireContext(),
//                    R.layout.item_spinner, locationArray
//                )
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                binding.spinner.adapter = adapter
//                binding.spinner.setSelection(itemClick, false)
//            } catch (e: java.lang.Exception) {
//                e.printStackTrace()
//            }
//
        } else {
            materialDialog(getString(R.string.no_property), "", "OK") {
                it.dismiss()
            }
            binding.ivDropdown.gone()
            binding.spinner.gone()
        }
    }

    private fun itemClickListener(homeListItem: HomeListItem) {
        try {
            when (homeListItem.title) {
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}