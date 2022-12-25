package com.setu.passwordmanager.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.setu.passwordmanager.databinding.CardPasswordmanagerBinding
import com.setu.passwordmanager.models.PasswordmanagerModel
import com.squareup.picasso.Picasso

interface PasswordmanagerListener {
    fun onPasswordmanagerClick(passwordmanager: PasswordmanagerModel, position: Int)
}

class PasswordmanagerAdapter constructor(
    private var passwordmanagers: List<PasswordmanagerModel>,
    private val listener: PasswordmanagerListener
) :
    RecyclerView.Adapter<PasswordmanagerAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardPasswordmanagerBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    fun delete(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardPasswordmanagerBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val passwordmanager = passwordmanagers[holder.adapterPosition]
        holder.bind(passwordmanager, listener)
    }

    override fun getItemCount(): Int = passwordmanagers.size

    class MainHolder(private val binding: CardPasswordmanagerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(passwordmanager: PasswordmanagerModel, listener: PasswordmanagerListener) {
            binding.passwordTitle.text = passwordmanager.title
            binding.description.text = passwordmanager.description
            Picasso.get().load(passwordmanager.image).resize(200, 200).into(binding.imageIcon)
            binding.root.setOnClickListener {
                listener.onPasswordmanagerClick(
                    passwordmanager,
                    adapterPosition
                )
            }
        }
    }
}
