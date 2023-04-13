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
import com.app.rivisio.databinding.ActivityAddTopicBinding
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.ui.home.HomeActivity
import com.app.rivisio.utils.NetworkResult
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class AddTopicActivity : BaseActivity() {

    private val addTopicViewModel: AddTopicViewModel by viewModels()

    private lateinit var binding: ActivityAddTopicBinding

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

        addTopicViewModel.tags.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoading()
                    startActivity(HomeActivity.getStartIntent(this@AddTopicActivity))
                    finish()
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
        listPopupWindow.anchorView = binding.tagsTextView

        binding.tagsTextView.addTextChangedListener(object : TextWatcher {
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
            filteredTags.add(Tag("Create Tag", -1, ""))

        val adapter = ArrayAdapter(this, R.layout.item_drop_down, filteredTags)
        listPopupWindow.setAdapter(adapter)
        listPopupWindow.setBackgroundDrawable(
            ContextCompat.getDrawable(this@AddTopicActivity, R.drawable.bg_popup_menu)
        );

        listPopupWindow.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->

            val clickedTag = parent?.getItemAtPosition(position) as Tag

            if (clickedTag.name == "Create Tag") {
                showMessage("Show create tag ui here")
                binding.tagsTextView.setText("")
                listPopupWindow.dismiss()
                return@setOnItemClickListener
            }

            if (selectedTags.contains(clickedTag)) {
                showError("You have already selected $clickedTag")
                binding.tagsTextView.setText("")
                listPopupWindow.dismiss()
                return@setOnItemClickListener
            }

            addPlanetChip(clickedTag)
        }

        listPopupWindow.show()
    }

    private fun addPlanetChip(tag: Tag) {
        selectedTags.add(tag)
        binding.tagsTextView.setText("")
        listPopupWindow.dismiss()
        binding.selectedTags.addView(getChip(tag))
    }

    private fun getChip(tag: Tag): Chip {
        return Chip(this@AddTopicActivity).apply {
            text = tag.name
            isCloseIconVisible = true
            chipBackgroundColor = ColorStateList.valueOf(Color.parseColor(tag.color))
            closeIcon = AppCompatResources.getDrawable(this@AddTopicActivity, R.drawable.ic_clear)
            setOnCloseIconClickListener {
                selectedTags.remove(tag)
                (it.parent as ChipGroup).removeView(it)
            }
        }
    }
}