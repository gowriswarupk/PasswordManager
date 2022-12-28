package com.setu.passwordmanager.models

import android.content.Context
import android.net.Uri
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.setu.passwordmanager.helpers.exists
import com.setu.passwordmanager.helpers.read
import com.setu.passwordmanager.helpers.write
import timber.log.Timber
import java.lang.reflect.Type
import java.util.*

const val JSON_FILE = "passwordmanagers.json"
val gsonBuilder: Gson = GsonBuilder().setPrettyPrinting()
    .registerTypeAdapter(Uri::class.java, UriParser())
    .create()
val listType: Type = object : TypeToken<ArrayList<PasswordmanagerModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class PasswordmanagerJSONStore(private val context: Context) : PasswordmanagerStore {

    var passwordmanagers = mutableListOf<PasswordmanagerModel>()

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<PasswordmanagerModel> {
        logAll()
        return passwordmanagers
    }

    override fun create(passwordmanager: PasswordmanagerModel) {
//        passwordmanager.id = generateRandomId()
        passwordmanagers.add(passwordmanager)
        serialize()
    }

    override fun update(passwordmanager: PasswordmanagerModel) {
        val passwordmanagersList = findAll() as ArrayList<PasswordmanagerModel>
        var foundPasswordmanager: PasswordmanagerModel? =
            passwordmanagersList.find { p -> p.id == passwordmanager.id }
        if (foundPasswordmanager != null) {
            foundPasswordmanager.title = passwordmanager.title
            foundPasswordmanager.password = passwordmanager.password
            foundPasswordmanager.description = passwordmanager.description
            foundPasswordmanager.image = passwordmanager.image
            foundPasswordmanager.lat = passwordmanager.lat
            foundPasswordmanager.lng = passwordmanager.lng
            foundPasswordmanager.zoom = passwordmanager.zoom
        }

        serialize()
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(passwordmanagers, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        passwordmanagers = gsonBuilder.fromJson(jsonString, listType)
    }

    override fun delete(passwordmanager: PasswordmanagerModel) {
        passwordmanagers.remove(passwordmanager)
        serialize()
    }

    override fun findOne(passwordmanager: PasswordmanagerModel) {
        passwordmanager.id = getId()
        passwordmanagers.get(passwordmanager.id.toInt())
    }

    private fun logAll() {
        passwordmanagers.forEach { Timber.i("$it") }
    }
}

class UriParser : JsonDeserializer<Uri>, JsonSerializer<Uri> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(json?.asString)
    }

    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}