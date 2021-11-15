package app.brainpool.nodesmobile.view.ui.home

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import app.brainpool.nodesmobile.data.PrefsKey
import app.brainpool.nodesmobile.databinding.HomeBinding
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    lateinit var binding: HomeBinding
    lateinit var navController: NavController

    @ExperimentalCoroutinesApi
    lateinit var viewModel: HomeViewModel

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        Log.v(TAG, "Key: " + Prefs.getString(PrefsKey.AUTH_KEY, "not rec"))
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }
}