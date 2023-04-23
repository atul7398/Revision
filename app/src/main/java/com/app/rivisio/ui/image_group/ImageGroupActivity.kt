package com.app.rivisio.ui.image_group

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.app.rivisio.R
import com.app.rivisio.databinding.ActivityImageGroupBinding
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.base.BaseViewModel
import com.esafirm.imagepicker.features.*
import com.esafirm.imagepicker.model.Image
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

const val IMAGE_GROUP_NAME = "image_group_name"

@AndroidEntryPoint
class ImageGroupActivity : BaseActivity() {

    @Inject
    lateinit var imagesAdapter: ImagesAdapter

    private val imageGroupViewModel: ImageGroupViewModel by viewModels()

    private lateinit var binding: ActivityImageGroupBinding

    override fun getViewModel(): BaseViewModel = imageGroupViewModel

    private val selectedImages = arrayListOf<Image>()

    private val config = ImagePickerConfig {
        mode = ImagePickerMode.MULTIPLE // default is multi image mode
        language = "in" // Set image picker language
        theme = R.style.Theme_Rivisio

        // set whether pick action or camera action should return immediate result or not. Only works in single mode for image picker
        returnMode = if (false) ReturnMode.ALL else ReturnMode.NONE
        arrowColor = Color.WHITE // set toolbar arrow up color
        folderTitle = "Folder" // folder selection title
        imageTitle = "Tap to select" // image selection title
        doneButtonText = "DONE" // done button text
        limit = 10 // max images can be selected (99 by default)
        isShowCamera = true // show camera or not (true by default)
        savePath =
            ImagePickerSavePath("Camera") // captured image directory name ("Camera" folder by default)
        savePath =
            ImagePickerSavePath(
                Environment.getExternalStorageDirectory().path,
                isRelative = false
            ) // can be a full path
    }

    private val launcher = registerImagePicker { result: List<Image> ->
        result.forEach { image ->
            Timber.e(image.path)
            selectedImages.add(image)
        }

        if (selectedImages.isEmpty()) {

        } else {
            binding.imageGroupIllustration.visibility = View.GONE
            binding.imageGrid.visibility = View.VISIBLE
            imagesAdapter.updateItems(selectedImages)
        }
    }

    companion object {
        fun getStartIntent(context: Context, imageGroupName: String): Intent {
            val intent = Intent(context, ImageGroupActivity::class.java)
            intent.putExtra(IMAGE_GROUP_NAME, imageGroupName)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageGroupName = intent.getStringExtra(IMAGE_GROUP_NAME)
        binding.imageGroupName.text = imageGroupName

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.selectImages.setOnClickListener {
            launcher.launch(config)
        }

        binding.saveImages.setOnClickListener {
            if (selectedImages.isEmpty()) {
                showError("Select s few images")
                return@setOnClickListener
            }


        }


        binding.imageGrid.layoutManager = GridLayoutManager(this, 2)
        binding.imageGrid.adapter = imagesAdapter
        //binding.imageGrid.addItemDecoration(ItemOffsetDecoration(R.dimen.image_grid_spacing))
        //imagesAdapter.setCallback(this)
    }
}