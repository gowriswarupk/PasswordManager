package com.setu.passwordmanager.models

import timber.log.Timber.i

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class PasswordmanagerMemStore : PasswordmanagerStore {

    val passwordmanagers = ArrayList<PasswordmanagerModel>()

    override fun findAll(): List<PasswordmanagerModel> {
        return passwordmanagers
    }

    override fun create(passwordmanager: PasswordmanagerModel) {
        passwordmanager.id = getId()
        passwordmanagers.add(passwordmanager)
        logAll()
    }

    override fun update(passwordmanager: PasswordmanagerModel) {
        val foundPasswordmanager: PasswordmanagerModel? =
            passwordmanagers.find { p -> p.id == passwordmanager.id }
        if (foundPasswordmanager != null) {
            foundPasswordmanager.title = passwordmanager.title
            foundPasswordmanager.password = passwordmanager.password
            foundPasswordmanager.description = passwordmanager.description
            foundPasswordmanager.image = passwordmanager.image
            foundPasswordmanager.lat = passwordmanager.lat
            foundPasswordmanager.lng = passwordmanager.lng
            foundPasswordmanager.zoom = passwordmanager.zoom
            logAll()
        }
    }

    private fun logAll() {
        passwordmanagers.forEach { i("$it") }
    }

    override fun delete(passwordmanager: PasswordmanagerModel) {
        passwordmanagers.remove(passwordmanager)
    }
}