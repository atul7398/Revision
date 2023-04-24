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
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import com.app.rivisio.R
import com.app.rivisio.databinding.ActivityAddNotesBinding
import com.app.rivisio.ui.add_topic.CreateTagBottomSheetDialog
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.ui.image_group.IMAGE_GROUP_NAME
import com.app.rivisio.ui.image_group.IMAGE_LIST
import com.app.rivisio.ui.image_group.ImageGroupActivity
import com.app.rivisio.ui.text_note.CONTENT
import com.app.rivisio.ui.text_note.HEADING
import com.app.rivisio.ui.text_note.TextNoteActivity
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.model.Image
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.ArrayList

@AndroidEntryPoint
class AddNotesActivity : BaseActivity(), CreateImageGroupBottomSheetDialog.Callback {

    private val addNotesViewModel: AddNotesViewModel by viewModels()

    private lateinit var binding: ActivityAddNotesBinding

    private var textNote: TextNote? = null
    private var imageNote: ImageNote? = null

    private lateinit var listPopupWindow: ListPopupWindow

    private var addTextNoteLauncher =
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

    private var addImageGroupLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                imageNote = ImageNote(
                    data?.getStringExtra(IMAGE_GROUP_NAME),
                    data?.getParcelableArrayListExtra(IMAGE_LIST)
                )
                if (imageNote != null) {

                    binding.imageGroupName.text = imageNote!!.imageGroupName

                    if (imageNote!!.selectedImages!!.size > 0) {
                        Glide
                            .with(this@AddNotesActivity)
                            .load(imageNote!!.selectedImages!![0].path)
                            .centerCrop()
                            .into(binding.image1)
                    } else {
                        loadDummyImage(binding.image1)
                    }

                    if (imageNote!!.selectedImages!!.size > 1) {
                        Glide
                            .with(this@AddNotesActivity)
                            .load(imageNote!!.selectedImages!![1].path)
                            .centerCrop()
                            .into(binding.image2)
                    } else {
                        loadDummyImage(binding.image2)
                    }

                    if (imageNote!!.selectedImages!!.size > 2) {
                        Glide
                            .with(this@AddNotesActivity)
                            .load(imageNote!!.selectedImages!![2].path)
                            .centerCrop()
                            .into(binding.image3)
                    } else {
                        loadDummyImage(binding.image3)
                    }

                    if (imageNote!!.selectedImages!!.size > 3) {
                        Glide
                            .with(this@AddNotesActivity)
                            .load(imageNote!!.selectedImages!![3].path)
                            .centerCrop()
                            .into(binding.image4)
                    } else {
                        loadDummyImage(binding.image4)
                    }

                    binding.notesIllustrationText.visibility = View.GONE
                    binding.imageNoteContainer.visibility = View.VISIBLE


                }
            }
        }

    private fun loadDummyImage(imageView: AppCompatImageView) {
        Glide
            .with(this@AddNotesActivity)
            .load(R.drawable.dummy_image)
            .centerCrop()
            .into(imageView)
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
                    addTextNoteLauncher.launch(
                        TextNoteActivity.getStartIntent(
                            this@AddNotesActivity,
                            textNote?.heading,
                            textNote?.content
                        )
                    )
                } else {
                    textNote = null
                    binding.textNoteContainer.visibility = View.GONE
                    if (imageNote == null && textNote == null) {
                        binding.notesIllustrationText.visibility = View.VISIBLE
                    }

                }
                listPopupWindow.dismiss()
            }
            showMenu(it, adapter, listener)

        }


        binding.editImages.setOnClickListener {
            val adapter = TextNoteOptionsAdapter(arrayListOf("Edit", "Delete"))
            val listener = AdapterView.OnItemClickListener { parent, view, position, id ->
                if (position == 0) {
                    if (imageNote != null) {
                        addImageGroupLauncher.launch(
                            ImageGroupActivity.getStartIntent(
                                this@AddNotesActivity,
                                imageNote!!.imageGroupName!!,
                                imageNote!!.selectedImages!!
                            )
                        )
                    }
                } else {
                    imageNote = null
                    binding.imageNoteContainer.visibility = View.GONE
                    if (imageNote == null && textNote == null) {
                        binding.notesIllustrationText.visibility = View.VISIBLE
                    }

                }
                listPopupWindow.dismiss()
            }
            showMenu(it, adapter, listener)

        }

        binding.floatingActionButton.setOnClickListener {
            val adapter = NoteTypeAdapter(arrayListOf("Text Note", "Image Group"))
            val listener = AdapterView.OnItemClickListener { parent, view, position, id ->
                if (position == 0) {
                    addTextNoteLauncher.launch(TextNoteActivity.getStartIntent(this@AddNotesActivity))
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
        addImageGroupLauncher.launch(
            ImageGroupActivity.getStartIntent(
                this@AddNotesActivity,
                imageGroupName
            )
        )
    }

}