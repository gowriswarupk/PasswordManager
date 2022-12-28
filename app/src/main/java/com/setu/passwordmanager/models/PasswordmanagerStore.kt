package com.setu.passwordmanager.models

interface PasswordmanagerStore {
    fun findAll(): List<PasswordmanagerModel>
    fun create(passwordmanager: PasswordmanagerModel)
    fun update(passwordmanager: PasswordmanagerModel)
    fun delete(passwordmanager: PasswordmanagerModel)
    fun findOne(passwordmanager: PasswordmanagerModel)
}