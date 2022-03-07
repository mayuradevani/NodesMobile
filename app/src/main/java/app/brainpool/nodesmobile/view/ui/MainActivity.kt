package app.brainpool.nodesmobile.view.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import app.brainpool.nodesmobile.R
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.databinding.MainBinding
import app.brainpool.nodesmobile.util.GlobalVar
import app.brainpool.nodesmobile.util.setNightModeOnOff
import app.brainpool.nodesmobile.util.setupTheme
import app.brainpool.nodesmobile.view.ui.map.MapFragment
import app.brainpool.nodesmobile.view.ui.notifications.NotificationsFragment
import app.brainpool.nodesmobile.view.ui.settings.SettingsFragment
import app.brainpool.nodesmobile.view.ui.siteNotes.SiteNotesFragment
import app.brainpool.nodesmobile.view.ui.tasks.TasksFragment
import com.google.android.material.navigation.NavigationBarView
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: MainBinding

    private val fragment1: Fragment = MapFragment()
    private val fragment2: Fragment = SiteNotesFragment()
    private val fragment3: Fragment = TasksFragment()
    private val fragment4: Fragment = NotificationsFragment()
    private val fragment5: Fragment = SettingsFragment()
    val fm: FragmentManager = supportFragmentManager
    var lastActive = Fragment()
    var active = fragment1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = MainBinding.inflate(layoutInflater)
            val view = binding.root
            setContentView(view)

            /*This is used for map update notification received from server*/
            if (!intent.getStringExtra(GlobalVar.EXTRA_FILE_NAME)
                    .isNullOrEmpty()
            ) {
                Prefs.putBoolean(PrefsKey.UPDATE_MAP, true)
                Prefs.putString(
                    PrefsKey.MAP_TILE_FILE_NAME,
                    intent.getStringExtra(GlobalVar.EXTRA_FILE_NAME)
                )
            }

            binding.ivSetting.setOnClickListener {
                if (lastActive is SettingsFragment) {
                    onBackPressed()
                    onBackPressed()
                } else if (active is SettingsFragment) {
                    fm.beginTransaction().hide(active).show(lastActive).commit()
                    active = lastActive
                    lastActive = Fragment()
                } else {
                    fm.beginTransaction().hide(active).show(fragment5).commit()
                    lastActive = active
                    active = fragment5
                }
            }

            binding.bottomNavigation.setOnItemSelectedListener(
                mOnNavigationItemSelectedListener
            )
            fm.beginTransaction().add(R.id.nav_host_fragment, fragment5, "5").hide(fragment5)
                .commit()
            fm.beginTransaction().add(R.id.nav_host_fragment, fragment4, "4").hide(fragment4)
                .commit()
            fm.beginTransaction().add(R.id.nav_host_fragment, fragment3, "3").hide(fragment3)
                .commit()
            fm.beginTransaction().add(R.id.nav_host_fragment, fragment2, "2").hide(fragment2)
                .commit()
            fm.beginTransaction().add(R.id.nav_host_fragment, fragment1, "1").commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun attachBaseContext(base: Context) {
        if (Prefs.getString(PrefsKey.NIGHT_MODE, "") == "")
            Prefs.putString(PrefsKey.NIGHT_MODE, base.getString(R.string.auto))
        setNightModeOnOff(base,Prefs.getString(PrefsKey.NIGHT_MODE))

        val context: Context = setupTheme(base, Prefs.getString(PrefsKey.NIGHT_MODE))
        super.attachBaseContext(context)
    }

    private val mOnNavigationItemSelectedListener =
        NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mapFragment -> {
                    fm.beginTransaction().hide(active).show(fragment1).commit()
                    active = fragment1
                    return@OnItemSelectedListener true
                }
                R.id.siteNotesFragment -> {
                    fm.beginTransaction().hide(active).show(fragment2).commit()
                    active = fragment2
                    return@OnItemSelectedListener true
                }
                R.id.tasksFragment -> {
                    fm.beginTransaction().hide(active).show(fragment3).commit()
                    active = fragment3
                    return@OnItemSelectedListener true
                }
                R.id.notificationsFragment -> {
                    fm.beginTransaction().hide(active).show(fragment4).commit()
                    active = fragment4
                    return@OnItemSelectedListener true
                }
            }
            false
        }


    override fun onBackPressed() {
        if (active is MapFragment) {
            finish()
        } else if (lastActive is SettingsFragment) {
            fm.beginTransaction().remove(active).show(lastActive).commit()
            active = lastActive
            lastActive = Fragment()
        } else
            binding.bottomNavigation.selectedItemId = R.id.mapFragment
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }
}