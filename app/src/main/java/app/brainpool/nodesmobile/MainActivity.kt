package app.brainpool.nodesmobile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import app.brainpool.nodesmobile.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

lateinit var binding: ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            val view = binding.root
            setContentView(view)

//            val navHostFragment =
//                supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
//            navController = navHostFragment.navController
//            setupActionBarWithNavController(navController)
        } catch (e: Exception) {
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }
}