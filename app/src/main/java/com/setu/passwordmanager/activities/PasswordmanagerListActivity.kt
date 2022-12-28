package com.setu.passwordmanager.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.setu.passwordmanager.R
import com.setu.passwordmanager.adapters.PasswordmanagerAdapter
import com.setu.passwordmanager.adapters.PasswordmanagerListener
import com.setu.passwordmanager.databinding.ActivityPasswordmanagerListBinding
import com.setu.passwordmanager.main.MainApp
import com.setu.passwordmanager.models.PasswordmanagerJSONStore
import com.setu.passwordmanager.models.PasswordmanagerModel

class PasswordmanagerListActivity : AppCompatActivity(), PasswordmanagerListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityPasswordmanagerListBinding
    private var position: Int = 0
    var passwordmanager = PasswordmanagerModel()
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordmanagerListBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = PasswordmanagerAdapter(app.passwordmanagers.findAll(), this)

        //swipeGesture working!
        val swipeGesture = object : SwipeGesture() {
            override fun onSwiped(
                viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                direction: Int
            ) {
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        val position = viewHolder.adapterPosition
//                        Todo  exception handling
                        passwordmanager =
                            (app.passwordmanagers as PasswordmanagerJSONStore).passwordmanagers[position]
                        app.passwordmanagers.delete(passwordmanager)
                        (binding.recyclerView.adapter)?.notifyItemRemoved(position)

                        database = FirebaseDatabase.getInstance().getReference("passwords")
                        database.child(passwordmanager.id.toString()).removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    applicationContext,
                                    "Password Deleted from DB",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }.addOnFailureListener {
                                Toast.makeText(
                                    applicationContext,
                                    "Sorry, Password deletion from DB failed -Key20087165",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeGesture)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, PasswordmanagerActivity::class.java)
                getResult.launch(launcherIntent)
            }
            R.id.item_map -> {
                val launcherIntent = Intent(this, PasswordmanagerMapsActivity::class.java)
                mapIntentLauncher.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                (binding.recyclerView.adapter)?.notifyItemRangeChanged(
                    0,
                    app.passwordmanagers.findAll().size
                )
            }
        }

    //...
    private val mapIntentLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { }

    //Register tap and open editable page
    override fun onPasswordmanagerClick(passwordmanager: PasswordmanagerModel, pos: Int) {
        val launcherIntent = Intent(this, PasswordmanagerActivity::class.java)
        launcherIntent.putExtra("password_edit", passwordmanager)
        position = pos
        getClickResult.launch(launcherIntent)
    }

    private val getClickResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                (binding.recyclerView.adapter)?.notifyItemRangeChanged(
                    0,
                    app.passwordmanagers.findAll().size
                )
            } else // Deleting
                if (it.resultCode == 99)
                    (binding.recyclerView.adapter)?.notifyItemRemoved(position)
        }

}
