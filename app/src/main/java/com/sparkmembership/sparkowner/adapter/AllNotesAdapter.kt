package com.sparkmembership.sparkowner.adapter

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sparkmembership.sparkfitness.util.DateUtil.formatIsoToLocalTimeLegacy
import com.sparkmembership.sparkfitness.util.toGone
import com.sparkmembership.sparkfitness.util.toVisible
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.data.response.Note
import com.sparkmembership.sparkowner.databinding.ItemAllNotesBinding

class AllNotesAdapter(
    private val context: Context,
    private val listener: OnNotesClickListener,
    private val contactID: Long?
) : RecyclerView.Adapter<AllNotesAdapter.NoteViewHolder>() {

    private var notesList: List<Note> = emptyList()

    fun setAllNoteList(notesList: List<Note>) {
        this.notesList = notesList
        notifyDataSetChanged()
    }

    class NoteViewHolder(val binding: ItemAllNotesBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemAllNotesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notesList[position]

        holder.binding.apply {
            staffName.text = context.getString(R.string.contactName, note.staffName)
            noteText.text = note.note
            notesFor.text = setNotesForEditText(context,note.contactFirstName,note.contactLastName)
            dateTime.text = formatIsoToLocalTimeLegacy (note.timestamp)
            if (note.assignedStaffName != "" && note.assignedStaffName != null) {
                followUp.text = context.getString(R.string.followUpAssignedTo, note.assignedStaffName)
                followUp.toVisible()
            } else {
                followUp.toGone()
            }

            if (contactID?.toInt() != note.contactID){
                itemCard.setBackgroundResource(R.drawable.rounded_background_orange)
                noteText.setTextColor(context.getColor(R.color.black))
                staffName.setTextColor(context.getColor(R.color.black))
                divider.setBackgroundColor(context.getColor(R.color.light_peach))
            }else{
                itemCard.setBackgroundResource(R.drawable.card_background)
                divider.setBackgroundColor(context.getColor(R.color.light_gray))
                noteText.setTextColor(context.getColor(R.color.colorAppTextPrimary))
                staffName.setTextColor(context.getColor(R.color.colorAppTextPrimary))
            }

            deleteButton.toVisibleIf(contactID?.toInt() == note.contactID)

            deleteButton.setOnClickListener {
                listener.onDelete(note)
            }
        }
    }

    override fun getItemCount(): Int = notesList.size

    interface OnNotesClickListener {
        fun onDelete(note: Note)
    }
}

fun setNotesForEditText(context: Context,firstName:String,lastName:String):SpannableString{
    val formattedString = context.getString(R.string.notesFor, firstName, lastName)
    val spannable = SpannableString(formattedString)
    spannable.setSpan(UnderlineSpan(), 0, formattedString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return spannable
}

fun View.toVisibleIf(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.GONE
}
