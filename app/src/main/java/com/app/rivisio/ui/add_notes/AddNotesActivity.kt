package com.app.rivisio.ui.add_notes

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.BaseAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import com.app.rivisio.R
import com.app.rivisio.databinding.ActivityAddNotesBinding
import com.app.rivisio.ui.add_topic.CreateTagBottomSheetDialog
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.ui.image_group.ImageGroupActivity
import com.app.rivisio.ui.text_note.CONTENT
import com.app.rivisio.ui.text_note.HEADING
import com.app.rivisio.ui.text_note.TextNoteActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AddNotesActivity : BaseActivity(), CreateImageGroupBottomSheetDialog.Callback {

    private val addNotesViewModel: AddNotesViewModel by viewModels()

    private lateinit var binding: ActivityAddNotesBinding

    private var textNote: TextNote? = null

    private lateinit var listPopupWindow: ListPopupWindow

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                data?.let {
                    Timber.e(it.getStringExtra(HEADING))
                    Timber.e(it.getStringExtra(CONTENT))

                    textNote = TextNote(
                        it.getStringExtra(HEADING)!!,
                        it.getStringExtra(CONTENT)!!
                    )

                    binding.textNoteHeading.text = textNote?.heading
                    binding.textNoteContent.text = textNote?.content

                    binding.notesIllustrationText.visibility = View.GONE
                    binding.textNoteContainer.visibility = View.VISIBLE

                }
            }
        }


    companion object {
        fun getStartIntent(context: Context) = Intent(context, AddNotesActivity::class.java)
    }

    override fun getViewModel(): BaseViewModel = addNotesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.editNote.setOnClickListener {
            val adapter = TextNoteOptionsAdapter(arrayListOf("Edit", "Delete"))
            val listener = AdapterView.OnItemClickListener { parent, view, position, id ->
                if (position == 0) {
                    resultLauncher.launch(
                        TextNoteActivity.getStartIntent(
                            this@AddNotesActivity,
                            textNote?.heading,
                            textNote?.content
                        )
                    )
                } else {
                    textNote = null
                    binding.notesIllustrationText.visibility = View.VISIBLE
                    binding.textNoteContainer.visibility = View.GONE

                }
                listPopupWindow.dismiss()
            }
            showMenu(it, adapter, listener)

        }

        binding.floatingActionButton.setOnClickListener {
            val adapter = NoteTypeAdapter(arrayListOf("Text Note", "Image Group"))
            val listener = AdapterView.OnItemClickListener { parent, view, position, id ->
                if (position == 0) {
                    resultLauncher.launch(TextNoteActivity.getStartIntent(this@AddNotesActivity))
                } else {
                    val createImageGroupBottomSheetDialog = CreateImageGroupBottomSheetDialog()
                    createImageGroupBottomSheetDialog.show(
                        supportFragmentManager,
                        CreateTagBottomSheetDialog.TAG
                    )
                    createImageGroupBottomSheetDialog.setCallback(this@AddNotesActivity)
                }
                listPopupWindow.dismiss()
            }
            showMenu(it, adapter, listener)
        }
    }

    private fun showMenu(
        anchor: View,
        adapter: BaseAdapter,
        clickListener: AdapterView.OnItemClickListener
    ) {
        listPopupWindow = ListPopupWindow(this)
        listPopupWindow.anchorView = anchor

        listPopupWindow.setDropDownGravity(Gravity.END)

        listPopupWindow.width = resources.getDimension(R.dimen.popup_width).toInt()
        listPopupWindow.height = ListPopupWindow.WRAP_CONTENT

        listPopupWindow.verticalOffset =
            resources.getDimension(R.dimen.popup_vertical_offset).toInt()
        listPopupWindow.horizontalOffset =
            resources.getDimension(R.dimen.popup_horizontal_offset).toInt()

        listPopupWindow.isModal = true

        listPopupWindow.setAdapter(adapter)
        listPopupWindow.setBackgroundDrawable(
            ContextCompat.getDrawable(this@AddNotesActivity, R.drawable.bg_popup_menu_2)
        )

        listPopupWindow.setOnItemClickListener(clickListener)

        listPopupWindow.show()
    }

    override fun onImageGroupCreated(imageGroupName: String) {
        Timber.e("Image group: $imageGroupName")
        startActivity(ImageGroupActivity.getStartIntent(this@AddNotesActivity, imageGroupName))
    }

}