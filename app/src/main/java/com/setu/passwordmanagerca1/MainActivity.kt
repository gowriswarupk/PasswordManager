package com.setu.passwordmanagerca1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.setu.passwordmanagerca1.databinding.ActivityMainBinding

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.greetingButton.setOnClickListener {
                val greetingText = getString(R.string.greeting_text)
                Toast.makeText(applicationContext, greetingText, Toast.LENGTH_LONG).show()
            }
    }

}