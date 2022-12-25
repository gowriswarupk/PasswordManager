package com.setu.passwordmanager.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.setu.passwordmanager.R
import com.setu.passwordmanager.adapters.PasswordmanagerAdapter
import com.setu.passwordmanager.adapters.PasswordmanagerListener
import com.setu.passwordmanager.databinding.ActivityPasswordmanagerListBinding
import com.setu.passwordmanager.main.MainApp
import com.setu.passwordmanager.models.PasswordmanagerModel

class PasswordmanagerListActivity : AppCompatActivity(), PasswordmanagerListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityPasswordmanagerListBinding
    private var position: Int = 0

    //swipe to delete/undo
    // TODO
//    lateinit var passwordmanagerRV: RecyclerView
//    lateinit var passwordmanagerAdapter: PasswordmanagerAdapter
//    lateinit var passwordmanagerList: ArrayList<PasswordmanagerModel>

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


    val adapter = PasswordmanagerAdapter(passwordmanagers = List<PasswordmanagerModel>)

    val swipeGesture = object : SwipeGesture() {
        override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int)
        {
            when(direction){
                    adapter.deleteItem(viewHolder.adapterPosition)
        }
    }
    val itemTouchHelper = ItemTouchHelper(swipeGesture)
    itemTouchHelper.attachToRecyclerView(newRecyclerView)

    newRecyclerView.adapter = adapter

}

}
