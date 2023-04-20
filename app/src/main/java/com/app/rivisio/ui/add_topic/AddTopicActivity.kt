package com.app.rivisio.ui.add_topic

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import com.app.rivisio.R
import com.app.rivisio.data.network.HEX_CODE
import com.app.rivisio.data.network.ID
import com.app.rivisio.data.network.NAME
import com.app.rivisio.databinding.ActivityAddTopicBinding
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.ui.home.HomeActivity
import com.app.rivisio.utils.NetworkResult
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class AddTopicActivity : BaseActivity(), ModalBottomSheet.Callback {

    private val addTopicViewModel: AddTopicViewModel by viewModels()

    private lateinit var binding: ActivityAddTopicBinding

    private val tags = mutableListOf<Tag>()

    companion object {
        fun getStartIntent(context: Context) = Intent(context, AddTopicActivity::class.java)
    }

    override fun getViewModel(): BaseViewModel = addTopicViewModel

    private val selectedTags = mutableListOf<Tag>()

    private lateinit var listPopupWindow: ListPopupWindow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTopicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpTagsUi()

        binding.selectDate.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setTitleText("Select dates")
                    .setTheme(R.style.ThemeOverlay_App_DatePicker)
                    .build()

            datePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
        }

        addTopicViewModel.tags.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoading()
                    try {
                        tags.clear()
                        it.data.asJsonArray.forEach { tag ->
                            tags.add(
                                Tag(
                                    tag.asJsonObject[ID].asInt,
                                    tag.asJsonObject[NAME].asString,
                                    tag.asJsonObject[HEX_CODE].asString
                                )
                            )
                        }
                    } catch (e: Exception) {
                        Timber.e("Json parsing issue: ")
                        Timber.e(e)
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
        }

        addTopicViewModel.addTags.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoading()
                    addTopicViewModel.getTopics()
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
        }

        addTopicViewModel.getTopics()
    }

    private fun setUpTagsUi() {
        listPopupWindow = ListPopupWindow(this)
        listPopupWindow.anchorView = binding.tagsField

        binding.tagsField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                if (listPopupWindow.isShowing) {
                    listPopupWindow.dismiss()
                }

                val filteredTags = mutableListOf<Tag>()

                if (s.toString().isNotEmpty()) {
                    tags.forEach {
                        if (it.name.uppercase(Locale.getDefault())
                                .startsWith(s.toString().uppercase(Locale.getDefault()))
                        ) {
                            filteredTags.add(it)
                        }
                    }

                    showMenu(filteredTags)
                }
            }
        })
    }

    private fun showMenu(filteredTags: MutableList<Tag>) {

        if (filteredTags.isEmpty())
            filteredTags.add(Tag(-1, "Create Tag", ""))

        val adapter = ArrayAdapter(this, R.layout.item_drop_down, filteredTags)
        listPopupWindow.setAdapter(adapter)
        listPopupWindow.setBackgroundDrawable(
            ContextCompat.getDrawable(this@AddTopicActivity, R.drawable.bg_popup_menu)
        );

        listPopupWindow.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->

            val clickedTag = parent?.getItemAtPosition(position) as Tag

            if (clickedTag.name == "Create Tag") {

                showCreateTagUI()

                binding.tagsField.setText("")
                listPopupWindow.dismiss()
                return@setOnItemClickListener

            }

            if (selectedTags.contains(clickedTag)) {
                showError("You have already selected $clickedTag")
                binding.tagsField.setText("")
                listPopupWindow.dismiss()
                return@setOnItemClickListener
            }

            addPlanetChip(clickedTag)
        }

        listPopupWindow.show()
    }

    private fun showCreateTagUI() {
        val modalBottomSheet = ModalBottomSheet()
        modalBottomSheet.show(supportFragmentManager, ModalBottomSheet.TAG)
        modalBottomSheet.setCallback(this)
    }

    private fun addPlanetChip(tag: Tag) {
        selectedTags.add(tag)
        binding.tagsField.setText("")
        listPopupWindow.dismiss()
        binding.selectedTags.addView(getChip(tag))
    }

    private fun getChip(tag: Tag): Chip {
        return Chip(this@AddTopicActivity).apply {
            text = tag.name
            isCloseIconVisible = true
            chipBackgroundColor =
                ColorStateList.valueOf(Color.parseColor(if (tag.hexCode.startsWith("#")) tag.hexCode else "#" + tag.hexCode))
            closeIcon = AppCompatResources.getDrawable(this@AddTopicActivity, R.drawable.ic_clear)
            setOnCloseIconClickListener {
                selectedTags.remove(tag)
                (it.parent as ChipGroup).removeView(it)
            }
        }
    }

    override fun onAddTagClick(tagName: String, tagColor: String) {
        Timber.e("Tag: $tagName, $tagColor")
        addTopicViewModel.addTag(tagName, tagColor)
    }
}