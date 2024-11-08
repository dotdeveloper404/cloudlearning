package com.sparkmembership.sparkowner.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sparkmembership.sparkowner.data.response.Contact
import com.sparkmembership.sparkowner.databinding.ItemContactBinding
import com.sparkmembership.sparkowner.util.PicassoUtil.loadImage

class ContactAdapter(val onContactListener: OnContactListener? = null) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var contactsList: MutableList<Contact> = mutableListOf()
    private var filteredContactsList: MutableList<Contact> = mutableListOf()

    init {
        filteredContactsList.addAll(contactsList)
    }

    fun setContacts(contacts: ArrayList<Contact>) {
        this.contactsList = contacts
        this.filteredContactsList = contacts.toMutableList()
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredContactsList = if (query.isEmpty()) {
            contactsList.toMutableList()
        } else {
            contactsList.filter {
                it.firstName.contains(query, ignoreCase = true) ||
                        it.emailAddress.contains(query, ignoreCase = true)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    class ContactViewHolder(val binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        if (contactsList.isEmpty() || position >= contactsList.size) {
            return
        }

        val contact = filteredContactsList[position]
        holder.binding.apply {
            textContactName.text = "${contact.firstName} ${contact.lastName}"
            textContactEmail.text = contact.emailAddress
            loadImage(contact.picture, imageContact)
            itemContact.setOnClickListener {
                onContactListener?.onItemClick(contact)
            }

        }
    }

    override fun getItemCount(): Int {
        return filteredContactsList.size
    }

    interface OnContactListener{
        fun onItemClick(contact:Contact)
    }
}
