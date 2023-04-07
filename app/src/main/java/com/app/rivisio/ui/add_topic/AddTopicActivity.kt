package com.app.rivisio.ui.add_topic

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.app.rivisio.R
import com.app.rivisio.databinding.ActivityAddTopicBinding
import com.app.rivisio.databinding.ActivityHomeBinding
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.ui.home.HomeViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTopicActivity : BaseActivity() {

    private val addTopicViewModel: AddTopicViewModel by viewModels()

    private lateinit var binding: ActivityAddTopicBinding

    companion object {
        fun getStartIntent(context: Context) = Intent(context, AddTopicActivity::class.java)
    }

    override fun getViewModel(): BaseViewModel = addTopicViewModel

    private val planetList = listOf(
        "Mercury",
        "Venus",
        "Earth",
        "Mars",
        "Jupiter",
        "Saturn",
        "Uranus",
        "Neptune",
        "Pluto"
    )

    private val selectedPlanets = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTopicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ArrayAdapter(this@AddTopicActivity, R.layout.item_drop_down, planetList)
        binding.tagsSelector.setAdapter(adapter)

        binding.tagsSelector.setOnItemClickListener { parent, _, position, _ ->
            val selectedPlanet = parent.getItemAtPosition(position) as String
            if (selectedPlanets.contains(selectedPlanet)) {
                Toast.makeText(
                    this@AddTopicActivity,
                    "You have already selected $selectedPlanet",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                addPlanetChip(selectedPlanet)
            }
            binding.tagsSelector.setText("")
        }
    }

    private fun addPlanetChip(planetName: String) {
        selectedPlanets.add(planetName)
        binding.selectedTags.addView(getChip(planetName))
    }

    private fun getChip(name: String): Chip {
        val colorInt: Int = getColor(R.color.white)

        return Chip(this@AddTopicActivity).apply {
            text = name
            isCloseIconVisible = true
            chipBackgroundColor = ColorStateList.valueOf(colorInt)
            closeIcon = AppCompatResources.getDrawable(this@AddTopicActivity, R.drawable.ic_clear)
            setOnCloseIconClickListener {
                selectedPlanets.remove((it as Chip).text)
                (it.parent as ChipGroup).removeView(it)
            }
        }
    }
}