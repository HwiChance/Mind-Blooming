package com.hwichance.android.mindblooming.utils

import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.TextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.hwichance.android.mindblooming.R

class DialogUtils {
    companion object {
        fun showEditTitleDialog(
            context: Context,
            preTitle: CharSequence,
            titleView: TextView
        ) {
            val dialogView =
                LayoutInflater.from(context).inflate(R.layout.idea_title_edit_dialog, null)
            val editTitleLayout = dialogView.findViewById<TextInputLayout>(R.id.editTitleLayout)
            val editTitleEditText =
                dialogView.findViewById<TextInputEditText>(R.id.editTitleEditText)
            editTitleEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    editTitleLayout.error = null
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })
            editTitleEditText.setText(preTitle)
            editTitleEditText.setSelection(preTitle.length)

            val dialog = MaterialAlertDialogBuilder(context)
                .setTitle(context.resources.getString(R.string.edit_title_dialog_title))
                .setView(dialogView)
                .setNegativeButton(context.resources.getString(R.string.dialog_cancel), null)
                .setPositiveButton(context.resources.getString(R.string.dialog_ok), null)
                .create()

            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val newTitle = editTitleEditText.text.toString()
                    if (StringUtils.hasCharacter(newTitle)) {
                        titleView.text = newTitle
                        dialog.dismiss()
                    } else {
                        editTitleLayout.error = context.resources.getString(R.string.no_character)
                    }
                }
            }
            dialog.show()
        }
    }
}