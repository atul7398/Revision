package com.app.rivisio.ui.add_topic

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.app.rivisio.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.dmoral.toasty.Toasty

class ModalBottomSheet : BottomSheetDialogFragment() {

    private var callback: Callback? = null

    interface Callback {
        fun onAddTagClick(tagName: String, tagColor: String)
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.modal_bottom_sheet_content, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var tagColor = "#F74940"

        val listener = View.OnClickListener {

            view.findViewById<View>(R.id.red_circle).isSelected = false
            view.findViewById<View>(R.id.yellow_circle).isSelected = false
            view.findViewById<View>(R.id.orange_circle).isSelected = false
            view.findViewById<View>(R.id.blue_circle).isSelected = false
            view.findViewById<View>(R.id.green_circle).isSelected = false
            view.findViewById<View>(R.id.violet_circle).isSelected = false
            view.findViewById<View>(R.id.brown_circle).isSelected = false


            it.isSelected = true
            tagColor = it.tag.toString()

        }

        view.findViewById<View>(R.id.red_circle).setOnClickListener(listener)
        view.findViewById<View>(R.id.yellow_circle).setOnClickListener(listener)
        view.findViewById<View>(R.id.orange_circle).setOnClickListener(listener)
        view.findViewById<View>(R.id.blue_circle).setOnClickListener(listener)
        view.findViewById<View>(R.id.green_circle).setOnClickListener(listener)
        view.findViewById<View>(R.id.violet_circle).setOnClickListener(listener)
        view.findViewById<View>(R.id.brown_circle).setOnClickListener(listener)

        view.findViewById<View>(R.id.red_circle).isSelected = true

        view.findViewById<AppCompatButton>(R.id.add_tag_button).setOnClickListener {
            if (TextUtils.isEmpty(view.findViewById<AppCompatEditText>(R.id.tag_field).text)) {
                Toasty.custom(
                    requireContext(),
                    "Tag cannot be empty",
                    R.drawable.ic_error,
                    es.dmoral.toasty.R.color.errorColor,
                    Toast.LENGTH_SHORT,
                    true,
                    true
                ).show()
                return@setOnClickListener
            }

            callback?.onAddTagClick(
                view.findViewById<AppCompatEditText>(R.id.tag_field).text.toString(),
                tagColor
            )

            dismiss()

        }
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}