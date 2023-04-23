package com.app.rivisio.ui.image_group

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.app.rivisio.databinding.ActivityImageGroupBinding
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.base.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint

const val IMAGE_GROUP_NAME = "image_group_name"

@AndroidEntryPoint
class ImageGroupActivity : BaseActivity() {

    private val imageGroupViewModel: ImageGroupViewModel by viewModels()

    private lateinit var binding: ActivityImageGroupBinding

    override fun getViewModel(): BaseViewModel = imageGroupViewModel

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
    }
}