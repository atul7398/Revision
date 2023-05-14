package com.app.rivisio.ui.edit_image_note

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.app.rivisio.databinding.ActivityEditImageNoteBinding
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.home.fragments.home_fragment.TopicFromServer
import com.app.rivisio.ui.topic_details.TOPIC_ID
import com.app.rivisio.utils.NetworkResult
import com.app.rivisio.utils.makeGone
import com.app.rivisio.utils.makeVisible
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class EditImageNoteActivity : BaseActivity(), EditImageAdapter.Callback {

    @Inject
    lateinit var editImageAdapter: EditImageAdapter

    private val editImageNoteViewModel: EditImageNoteViewModel by viewModels()

    private lateinit var binding: ActivityEditImageNoteBinding

    private lateinit var topicFromServer: TopicFromServer

    override fun getViewModel() = editImageNoteViewModel

    companion object {
        fun getStartIntent(context: Context, id: Int?): Intent {
            val intent = Intent(context, EditImageNoteActivity::class.java)
            intent.putExtra(TOPIC_ID, id)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditImageNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.imageGrid.layoutManager = GridLayoutManager(this, 2)
        binding.imageGrid.adapter = editImageAdapter
        //binding.imageGrid.addItemDecoration(ItemOffsetDecoration(R.dimen.image_grid_spacing))
        editImageAdapter.setCallback(this)

        editImageNoteViewModel.topicData.observe(this, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoading()
                    showMessage("Network call is successful")
                    try {

                        topicFromServer = Gson().fromJson(
                            it.data.asJsonObject,
                            TopicFromServer::class.java
                        )

                        binding.imageGroupIllustration.makeGone()
                        binding.imageGrid.makeVisible()

                        editImageAdapter.updateItems(topicFromServer.imageUrls)
                        editImageAdapter.notifyDataSetChanged()

                    } catch (e: Exception) {
                        Timber.e("Json Parsing Error:")
                        Timber.e(e)
                        showError("Something went wrong")
                    }
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

        editImageNoteViewModel.update.observe(this, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoading()
                    val id = intent.getIntExtra(TOPIC_ID, -1)

                    if (id != -1)
                        editImageNoteViewModel.getTopicDetails(id)
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

        val id = intent.getIntExtra(TOPIC_ID, -1)

        if (id != -1)
            editImageNoteViewModel.getTopicDetails(id)
    }

    override fun onDeleteImageClick(imageUrl: String) {
        val id = intent.getIntExtra(TOPIC_ID, -1)

        topicFromServer.imageUrls.remove(imageUrl)

        editImageNoteViewModel.updateImageNote(
            id,
            topicFromServer
        )
    }
}