package app.brainpool.nodesmobile

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import app.brainpool.nodesmobile.databinding.MainBinding
import app.brainpool.nodesmobile.util.switch
import dagger.hilt.android.AndroidEntryPoint

import com.google.android.material.bottomnavigation.BottomNavigationView
import app.brainpool.nodesmobile.view.ui.notifications.NotificationsFragment

import app.brainpool.nodesmobile.view.ui.home.HomeFragment
import app.brainpool.nodesmobile.view.ui.map.MapFragment
import app.brainpool.nodesmobile.view.ui.siteNotes.SiteNotesFragment
import app.brainpool.nodesmobile.view.ui.tasks.TasksFragment


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: MainBinding
    lateinit var navController: NavController
    val fragment1: Fragment = MapFragment()
    val fragment2: Fragment = SiteNotesFragment()
    val fragment3: Fragment = TasksFragment()
    val fragment4: Fragment = NotificationsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = MainBinding.inflate(layoutInflater)
            val view = binding.root
            setContentView(view)
            val crashButton = Button(this)
            crashButton.text = "Test Crash"
            crashButton.setOnClickListener {
                throw RuntimeException("Test Crash") // Force a crash
            }

            addContentView(
                crashButton, ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
            if (savedInstanceState == null) {
                binding.bottomNavigation.setOnItemSelectedListener {
                    when (it.itemId) {
                        R.id.menu_home -> {
                            supportFragmentManager.switch(R.id.nav_host_fragment, fragment1, "Home")
                            return@setOnItemSelectedListener true
                        }
                        R.id.menu_site_notes -> {
                            supportFragmentManager.switch(
                                R.id.nav_host_fragment,
                                fragment2,
                                "SiteNotes"
                            )
                            return@setOnItemSelectedListener true
                        }
                        R.id.menu_tasks -> {
                            supportFragmentManager.switch(
                                R.id.nav_host_fragment,
                                fragment3,
                                "Tasks"
                            )
                            return@setOnItemSelectedListener true
                        }
                        R.id.menu_notifications -> {
                            supportFragmentManager.switch(
                                R.id.nav_host_fragment,
                                fragment4,
                                "Notifications"
                            )
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

//    override fun onSupportNavigateUp(): Boolean {
//        navController.navigateUp()
//        return super.onSupportNavigateUp()
//    }
}