package app.brainpool.nodesmobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import app.brainpool.nodesmobile.databinding.MainBinding
import app.brainpool.nodesmobile.util.GlobalVar
import app.brainpool.nodesmobile.util.GlobalVar.TAG
import app.brainpool.nodesmobile.view.ui.map.MapFragment
import app.brainpool.nodesmobile.view.ui.notifications.NotificationsFragment
import app.brainpool.nodesmobile.view.ui.settings.SettingsFragment
import app.brainpool.nodesmobile.view.ui.siteNotes.SiteNotesFragment
import app.brainpool.nodesmobile.view.ui.tasks.TasksFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: MainBinding
    val fragment1: MapFragment = MapFragment()
    val fragment2: Fragment = SiteNotesFragment()
    val fragment3: Fragment = TasksFragment()
    val fragment4: Fragment = NotificationsFragment()
    val fragment5: Fragment = SettingsFragment()
    val fm: FragmentManager = supportFragmentManager
    var active: Fragment = fragment1

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults!!)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GlobalVar.REQUEST_CHECK_SETTINGS ->
                when (resultCode) {
                    RESULT_OK -> {
                        fragment1.locationUpdateState = true
                        fragment1.startLocationUpdates()
                    }
                    RESULT_CANCELED -> {
                        finish()
                    }
                    else -> {}
                }
        }
    }

    fun removeFragment(tag: String?) {
        val fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment != null) supportFragmentManager.beginTransaction().remove(fragment).commit()
    }

    fun setFragment(fragment: Fragment, tag: String?, position: Int) {
        if (fragment.isAdded) {
            fm.beginTransaction().hide(active).show(fragment).commit()
        } else {
            fm.beginTransaction().add(R.id.nav_host_fragment, fragment, tag).commit()
        }
        binding.bottomNavigation.getMenu().getItem(position).setChecked(true)
        if (fragment is SettingsFragment)
            binding.ivSetting.setColorFilter(
                ContextCompat.getColor(this, R.color.green_text),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
        else
            binding.ivSetting.setColorFilter(
                ContextCompat.getColor(this, R.color.grey_bg),
                android.graphics.PorterDuff.Mode.MULTIPLY
            );
        active = fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = MainBinding.inflate(layoutInflater)
            val view = binding.root
            setContentView(view)
            Log.v(TAG, "Extra: " + intent.getStringExtra("NOTI_ID"))//testing notification
            if (savedInstanceState == null) {
//                binding.ivSetting.setOnClickListener {
//                    setFragment(fragment5, "Settings", 0)
//                }
                binding.bottomNavigation.setOnItemSelectedListener {
                    when (it.itemId) {
                        R.id.menu_home -> {
                            setFragment(fragment1, "Home", 0)
                            return@setOnItemSelectedListener true
                        }
                        R.id.menu_site_notes -> {
                            setFragment(fragment2, "SiteNotes", 1)
                            return@setOnItemSelectedListener true
                        }
                        R.id.menu_tasks -> {
                            setFragment(fragment3, "Tasks", 2)
                            return@setOnItemSelectedListener true
                        }
                        R.id.menu_notifications -> {
                            setFragment(fragment4, "Notifications", 3)
                            return@setOnItemSelectedListener true
                        }
                    }
                    false
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}