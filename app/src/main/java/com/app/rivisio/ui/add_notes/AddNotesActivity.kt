package com.app.rivisio.ui.add_notes

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.ListPopupWindow
import androidx.lifecycle.Observer
import com.app.rivisio.R
import com.app.rivisio.data.network.AWS_URL
import com.app.rivisio.databinding.ActivityAddNotesBinding
import com.app.rivisio.ui.add_topic.*
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.ui.home.HomeActivity
import com.app.rivisio.ui.image_group.IMAGE_GROUP_NAME
import com.app.rivisio.ui.image_group.IMAGE_LIST
import com.app.rivisio.ui.image_group.ImageGroupActivity
import com.app.rivisio.ui.text_note.CONTENT
import com.app.rivisio.ui.text_note.HEADING
import com.app.rivisio.ui.text_note.TextNoteActivity
import com.app.rivisio.utils.NetworkResult
import com.app.rivisio.utils.getPopupMenu
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class AddNotesActivity : BaseActivity(), CreateImageGroupBottomSheetDialog.Callback {

    private val addNotesViewModel: AddNotesViewModel by viewModels()

    private lateinit var binding: ActivityAddNotesBinding

    private var textNote: TextNote? = null
    private var imageNote: ImageNote? = null
    private var uploadCount = 0
    private var uploadedImages = ArrayList<String>()

    companion object {
        fun getStartIntent(
            context: Context,
            topic: String,
            studiedOn: String,
            tags: ArrayList<Tag>
        ): Intent {
            val intent = Intent(context, AddNotesActivity::class.java)
            intent.putExtra(TOPIC_NAME, topic)
            intent.putExtra(STUDIED_ON, studiedOn)
            intent.putParcelableArrayListExtra(TAGS, tags)
            return intent
        }
    }

    override fun getViewModel(): BaseViewModel = addNotesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {

        /*val topic = intent.getStringExtra(TOPIC_NAME)
        val studiedOn = intent.getStringExtra(STUDIED_ON)
        val tags = intent.getParcelableArrayListExtra<Tag>(TAGS)

        Timber.e("Topic: $topic")
        Timber.e("Studied On: $studiedOn")

        tags?.forEach {
            Timber.e("Tag: ${it.name}, ${it.id}")
        }*/

        setupButtonClicks()

        binding.createTopicButton.setOnClickListener {
            if (textNote == null && imageNote == null) {
                showError("Please add notes")
                return@setOnClickListener
            }

            uploadImages()
        }

        addNotesViewModel.imageUploaded.observe(this, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    uploadedImages.add(it.data.asJsonObject[AWS_URL].asString)
                    uploadCount++
                    if (uploadCount < imageNote!!.selectedImages!!.size) {
                        uploadImages()
                    } else {

                        addNotesViewModel.addTopic(
                            Topic(
                                uploadedImages,
                                intent.getStringExtra(TOPIC_NAME)!!,
                                getStringifiedJsonNote(textNote!!),
                                getStudiedOnDateString(),
                                getTags()
                            )
                        )

                        hideLoading()
                        showMessage("Images uploaded successfully")
                    }
                }
                is NetworkResult.Loading -> {
                    showLoading()
                }
                is NetworkResult.Error -> {
                    hideLoading()
                    showError(it.message)
                    uploadCount = 0
                }
                is NetworkResult.Exception -> {
                    hideLoading()
                    showError(it.e.message)
                    uploadCount = 0
                }
                else -> {
                    hideLoading()
                    Timber.e(it.toString())
                    uploadCount = 0
                }
            }
        })

        addNotesViewModel.topicAdded.observe(this, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    showMessage("Topic Created successfully.")
                    showTopicCreatedDialog()
                }
                is NetworkResult.Loading -> {
                    showLoading()
                }
                is NetworkResult.Error -> {
                    hideLoading()
                    showError(it.message)
                }
                is NetworkResult.Exception -> {
                    hideLoading()
                    showError(it.e.message)
                }
                else -> {
                    hideLoading()
                    Timber.e(it.toString())
                }
            }
        })
    }

    private fun showTopicCreatedDialog() {

        var dialog: AlertDialog? = null

        val view =
            LayoutInflater.from(this@AddNotesActivity).inflate(R.layout.topic_created_dialog, null)

        val dialogBuilder =
            MaterialAlertDialogBuilder(
                this@AddNotesActivity,
                R.style.ThemeOverlay_App_MaterialAlertDialog
            )
                .setCancelable(false)
                .setView(view)


        view.findViewById<AppCompatButton>(R.id.topic_created_button)?.setOnClickListener {
            dialog?.dismiss()
            startActivity(HomeActivity.getStartIntentNewTask(this@AddNotesActivity))
            finish()
        }

        dialog = dialogBuilder.show()


    }

    private fun getStudiedOnDateString(): String {
        val dateString = intent.getStringExtra(STUDIED_ON)

        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault(Locale.Category.FORMAT))
        val date = formatter.parse(dateString!!)

        val formatter2 =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault(Locale.Category.FORMAT))

        return formatter2.format(date!!).toString()
    }

    private fun setupButtonClicks() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.editNote.setOnClickListener {

            var popup: ListPopupWindow? = null

            val adapter = TextNoteOptionsAdapter(arrayListOf("Edit", "Delete"))
            val listener = AdapterView.OnItemClickListener { _, _, position, _ ->
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
                popup?.dismiss()
            }

            popup = getPopupMenu(this@AddNotesActivity, it, adapter, listener)

            popup.show()

        }

        binding.editImages.setOnClickListener {
            var popup: ListPopupWindow? = null

            val adapter = TextNoteOptionsAdapter(arrayListOf("Edit", "Delete"))
            val listener = AdapterView.OnItemClickListener { _, _, position, _ ->
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
                popup?.dismiss()
            }
            popup = getPopupMenu(this@AddNotesActivity, it, adapter, listener)

            popup?.show()

        }

        binding.floatingActionButton.setOnClickListener {

            var popup: ListPopupWindow? = null

            val adapter = NoteTypeAdapter(arrayListOf("Text Note", "Image Group"))
            val listener = AdapterView.OnItemClickListener { _, _, position, _ ->
                if (position == 0) {
                    if (textNote == null) {
                        addTextNoteLauncher.launch(TextNoteActivity.getStartIntent(this@AddNotesActivity))
                    } else {
                        showError("Not allowed")
                    }
                } else {
                    if (imageNote == null) {
                        val createImageGroupBottomSheetDialog = CreateImageGroupBottomSheetDialog()
                        createImageGroupBottomSheetDialog.show(
                            supportFragmentManager,
                            CreateTagBottomSheetDialog.TAG
                        )
                        createImageGroupBottomSheetDialog.setCallback(this@AddNotesActivity)
                    } else {
                        showError("Not allowed")
                    }
                }
                popup?.dismiss()
            }
            popup = getPopupMenu(this@AddNotesActivity, it, adapter, listener)

            popup?.show()

        }
    }

    private fun getTags(): ArrayList<Int> {
        val tags = intent.getParcelableArrayListExtra<Tag>(TAGS)
        val tagsList = ArrayList<Int>()
        tags!!.forEach {
            tagsList.add(it.id)
        }
        return tagsList
    }

    private fun getStringifiedJsonNote(textNote: TextNote): String {
        val jsonNote = JsonObject()
        jsonNote.addProperty("title", textNote.heading!!)
        jsonNote.addProperty("body", textNote.content!!)
        return jsonNote.toString()
    }

    private fun uploadImages() {
        addNotesViewModel.uploadImage(imageNote!!.selectedImages!![uploadCount])
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
                uploadedImages = ArrayList() //clear the old uploaded images, because the user might have changed the images
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
}