package com.app.rivisio.ui.topic_details

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import com.app.rivisio.BuildConfig
import com.app.rivisio.R
import com.app.rivisio.data.network.NAME
import com.app.rivisio.data.network.NOTES
import com.app.rivisio.data.network.STUDIEDON
import com.app.rivisio.databinding.ActivityTopicDetailsBinding
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.ui.image_group.IMAGE_GROUP_NAME
import com.app.rivisio.utils.NetworkResult
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


const val TOPIC_ID = "topic_id"

@AndroidEntryPoint
class TopicDetailsActivity : BaseActivity() {

    companion object {
        fun getStartIntent(context: Context, id: Int?): Intent {
            val intent = Intent(context, TopicDetailsActivity::class.java)
            intent.putExtra(TOPIC_ID, id)
            return intent
        }
    }

    private val topicDetailsViewModel: TopicDetailsViewModel by viewModels()

    private lateinit var binding: ActivityTopicDetailsBinding

    override fun getViewModel(): BaseViewModel {
        return topicDetailsViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopicDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }

        topicDetailsViewModel.topicData.observe(this, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoading()
                    showMessage("Network call is successful")
                    try {
                        binding.topicName.text = it.data.asJsonObject[NAME].asString

                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val dateTime = LocalDateTime.parse(
                            it.data.asJsonObject[STUDIEDON].asString,
                            formatter
                        )

                        binding.date1.text = getFormattedDate(dateTime.plusDays(1))
                        binding.date2.text = getFormattedDate(dateTime.plusDays(3))
                        binding.date3.text = getFormattedDate(dateTime.plusDays(7))
                        binding.date4.text = getFormattedDate(dateTime.plusDays(10))

                        if (!it.data.asJsonObject["rev1Status"].isJsonNull)
                            binding.circle1.backgroundTintList =
                                getColorForRevision(it.data.asJsonObject["rev1Status"].asString)
                        if (!it.data.asJsonObject["rev2Status"].isJsonNull)
                            binding.circle2.backgroundTintList =
                                getColorForRevision(it.data.asJsonObject["rev2Status"].asString)
                        if (!it.data.asJsonObject["rev3Status"].isJsonNull)
                            binding.circle3.backgroundTintList =
                                getColorForRevision(it.data.asJsonObject["rev3Status"].asString)
                        if (!it.data.asJsonObject["rev4Status"].isJsonNull)
                            binding.circle4.backgroundTintList =
                                getColorForRevision(it.data.asJsonObject["rev4Status"].asString)

                        val noteJsonObject =
                            JsonParser().parse(it.data.asJsonObject[NOTES].asString).asJsonObject

                        binding.textNoteHeading.text =
                            noteJsonObject["title"].asString
                        binding.textNoteContent.text =
                            noteJsonObject["body"].asString

                        for (i in 0 until it.data.asJsonObject["imageUrls"].asJsonArray.size()) {
                            when (i) {
                                0 -> {
                                    Glide.with(this@TopicDetailsActivity)
                                        .asBitmap()
                                        .load(BuildConfig.BASE_URL + "/users/getfile?awsUrl=" + it.data.asJsonObject["imageUrls"].asJsonArray[i].asString)
                                        .centerCrop()
                                        .into(binding.image1)
                                }
                                1 -> {
                                    Glide.with(this@TopicDetailsActivity)
                                        .asBitmap()
                                        .load(BuildConfig.BASE_URL + "/users/getfile?awsUrl=" + it.data.asJsonObject["imageUrls"].asJsonArray[i].asString)
                                        .centerCrop()
                                        .into(binding.image2)
                                }
                                2 -> {
                                    Glide.with(this@TopicDetailsActivity)
                                        .asBitmap()
                                        .load(BuildConfig.BASE_URL + "/users/getfile?awsUrl=" + it.data.asJsonObject["imageUrls"].asJsonArray[i].asString)
                                        .centerCrop()
                                        .into(binding.image3)
                                }
                                3 -> {
                                    Glide.with(this@TopicDetailsActivity)
                                        .asBitmap()
                                        .load(BuildConfig.BASE_URL + "/users/getfile?awsUrl=" + it.data.asJsonObject["imageUrls"].asJsonArray[i].asString)
                                        .centerCrop()
                                        .into(binding.image4)
                                }
                            }
                        }


                    } catch (e: Exception) {
                        Timber.e("JSon Parsing Error:")
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

        val id = intent.getIntExtra(TOPIC_ID, -1)

        if (id != -1)
            topicDetailsViewModel.getTopicDetails(id)

    }

    private fun getFormattedDate(dateTime: LocalDateTime): String {
        val formatter2 = DateTimeFormatter.ofPattern("dd/MM")
        return dateTime.format(formatter2)
    }

    private fun getColorForRevision(revStatus: String?): ColorStateList {
        return when (revStatus) {
            "stop" -> {
                ColorStateList.valueOf(Color.parseColor("#F69032"))
            }
            "wait" -> {
                ColorStateList.valueOf(Color.parseColor("#FFB904"))
            }
            else -> { //done
                ColorStateList.valueOf(Color.parseColor("#0E965E"))
            }

        }
    }
}