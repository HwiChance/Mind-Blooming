package com.hwichance.android.mindblooming.utils

import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.listeners.OnEditTextDialogBtnClick

class DialogUtils {
    companion object {
        fun showEditTitleDialog(
            context: Context,
            title: String,
            hint: String,
            prevText: CharSequence,
            listener: OnEditTextDialogBtnClick
        ) {
            val view = LayoutInflater.from(context).inflate(R.layout.idea_title_edit_dialog, null)
            val editTextLayout = view.findViewById<TextInputLayout>(R.id.editTitleLayout)
            editTextLayout.hint = hint

            val editText = view.findViewById<TextInputEditText>(R.id.editTitleEditText)
            editText.setText(prevText)
            editText.setSelection(prevText.length)
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, cnt: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    editTextLayout.error = null
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })

            val dialog = MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setView(view)
                .setNegativeButton(context.resources.getString(R.string.dialog_cancel), null)
                .setPositiveButton(context.resources.getString(R.string.dialog_ok), null)
                .create()

            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val newText = editText.text.toString()
                    if (StringUtils.hasCharacter(newText)) {
                        listener.onClick(newText)
                        dialog.dismiss()
                    } else {
                        editTextLayout.error = context.resources.getString(R.string.no_character)
                    }
                }
            }
            dialog.show()
        }
    }
}