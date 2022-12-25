package com.setu.passwordmanager.activities

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.setu.passwordmanager.R
import com.setu.passwordmanager.databinding.ActivityPasswordmanagerBinding
import com.setu.passwordmanager.main.MainApp
import com.setu.passwordmanager.models.Location
import com.setu.passwordmanager.models.PasswordmanagerModel
import com.setu.passwordmanager.models.generateRandomId
import com.setu.passwordmanager.showImagePicker
import com.squareup.picasso.Picasso
import timber.log.Timber.i

class PasswordmanagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPasswordmanagerBinding
    private lateinit var imageIntentLauncher: ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher: ActivityResultLauncher<Intent>
    private lateinit var database: DatabaseReference
    var passwordmanager = PasswordmanagerModel()
    lateinit var app: MainApp

    private val CAMERA_REQUEST_CODE = 1

    var edit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        edit = false

        binding = ActivityPasswordmanagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

        i("Password Manager Activity started...")


        //Password Text field with editable entity
        if (intent.hasExtra("password_edit")) {
            edit = true
            passwordmanager = intent.extras?.getParcelable("password_edit")!!
            binding.passwordTitle.setText(passwordmanager.title)
            binding.passwordEdit.setText(passwordmanager.password)
            binding.description.setText(passwordmanager.description)
            binding.btnAdd.setText(R.string.save_password)
            Picasso.get()
                .load(passwordmanager.image)
                .into(binding.passwordImage)
            if (passwordmanager.image != Uri.EMPTY) {
                binding.chooseImage.setText(R.string.change_password_image)
            }

        }

        //On Button Click Actions
        binding.btnAdd.setOnClickListener {

            //CloudStorage needs value while creating, not editing
            if (!edit) {
                passwordmanager.id = generateRandomId()
            }

            passwordmanager.title = binding.passwordTitle.text.toString()
            passwordmanager.password = binding.passwordEdit.text.toString()
            passwordmanager.description = binding.description.text.toString()
            if (passwordmanager.title.isEmpty()) {
                Snackbar.make(it, R.string.enter_password_title, Snackbar.LENGTH_LONG)
                    .show()
            } else {
                if (edit) {
                    //log message to check progess and locate error
                    i("ID value for edit: " + passwordmanager.id)

                    //create Hashmap for sending to Cloud Storage w/ structure
                    val PasswordmanagerModel = HashMap<String, Any>()
                    PasswordmanagerModel["id"] = passwordmanager.id
                    PasswordmanagerModel["title"] = passwordmanager.title
                    PasswordmanagerModel["password"] = passwordmanager.password
                    PasswordmanagerModel["desc"] = passwordmanager.description

                    //database Reference in firebase realtime database
                    database = FirebaseDatabase.getInstance().getReference("passwords")

                    database.child(passwordmanager.id.toString()).setValue(PasswordmanagerModel)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Password Updated to DB", Toast.LENGTH_SHORT)
                                .show()
                        }.addOnFailureListener {
                            Toast.makeText(
                                this,
                                "Sorry, Password update failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    //Local Save
                    app.passwordmanagers.update(passwordmanager.copy())
                    i("Atleast local worked?: " + passwordmanager.id)

                } else {
                    i("cloud save started " + passwordmanager.id)
                    database = FirebaseDatabase.getInstance().getReference("passwords")
                    val PasswordmanagerModel = HashMap<String, Any>()
                    PasswordmanagerModel["id"] = passwordmanager.id
                    PasswordmanagerModel["title"] = passwordmanager.title
                    PasswordmanagerModel["password"] = passwordmanager.password
                    PasswordmanagerModel["desc"] = passwordmanager.description


                    database.child(passwordmanager.id.toString()).setValue(PasswordmanagerModel)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Password Saved to DB", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(this, "Sorry, Password save failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    app.passwordmanagers.create(passwordmanager.copy())
                    i("new id value " + passwordmanager.id)
                }
            }
            i("add Button Pressed: $passwordmanager")

            setResult(RESULT_OK)
            finish()
        }

        //Image Selector
        binding.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher, this)
        }

        //Camera Take Picture
        binding.btnCamera.setOnClickListener {
            cameraCheckPermission()
        }

        //Location Set
        binding.passwordLocation.setOnClickListener {
            val location = Location(52.245696, -7.139102, 15f)
            if (passwordmanager.zoom != 0f) {
                location.lat = passwordmanager.lat
                location.lng = passwordmanager.lng
                location.zoom = passwordmanager.zoom
            }
            val launcherIntent = Intent(this, MapActivity::class.java)
                .putExtra("location", location)
            mapIntentLauncher.launch(launcherIntent)
        }
        registerImagePickerCallback()
        registerMapCallback()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_passwordmanager, menu)
        if (edit) menu.getItem(0).isVisible = true
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_delete -> {
                setResult(99)
                //Delete Cloud saved counterparts
                database = FirebaseDatabase.getInstance().getReference("passwords")
                database.child(passwordmanager.id.toString()).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Password Deleted from DB", Toast.LENGTH_SHORT)
                            .show()
                    }.addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Sorry, Password deletion from DB failed -Key20087165",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                //Local save deleted
                app.passwordmanagers.delete(passwordmanager)
                finish()
            }
            R.id.item_cancel -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun cameraCheckPermission() {
        Dexter.withContext(this).withPermission(android.Manifest.permission.CAMERA).withListener(
            object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    camera()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    TODO("Not yet implemented")
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    showRorationalDialogForPermissions()
                }

            }
        ).onSameThread().check()

    }

    private fun camera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {

                    val bitmap = data?.extras?.get("data") as Bitmap
                    binding.passwordImage.load(bitmap) {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())
                    }
                }
            }
        }
    }

    private fun showRorationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("Please allow for application to use camera feature")
            .setPositiveButton("Go to settings") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data}")
                            val image = result.data!!.data!!
                            contentResolver.takePersistableUriPermission(
                                image,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )
                            passwordmanager.image = image
                            Picasso.get()
                                .load(passwordmanager.image)
                                .into(binding.passwordImage)
                            binding.chooseImage.setText(R.string.change_password_image)
                        }
                    }
                    RESULT_CANCELED -> {}
                    else -> {}
                }
            }
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Location ${result.data.toString()}")
                            val location =
                                result.data!!.extras?.getParcelable<Location>("location")!!
                            i("Location == $location")
                            passwordmanager.lat = location.lat
                            passwordmanager.lng = location.lng
                            passwordmanager.zoom = location.zoom
                        }
                    }
                    RESULT_CANCELED -> {}
                    else -> {}
                }
            }
    }


}
