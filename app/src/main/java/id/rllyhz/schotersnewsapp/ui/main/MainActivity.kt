package id.rllyhz.schotersnewsapp.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import id.rllyhz.schotersnewsapp.BuildConfig
import id.rllyhz.schotersnewsapp.databinding.ActivityMainBinding
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launchWhenResumed {
            delay(3000)
            binding.mainProgressbar.visibility = View.GONE
        }

        Log.d("myapp", BuildConfig.API_KEY)
    }
}