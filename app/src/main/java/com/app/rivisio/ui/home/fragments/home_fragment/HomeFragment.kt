package com.app.rivisio.ui.home.fragments.home_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rivisio.R
import com.app.rivisio.databinding.FragmentHomeBinding
import com.app.rivisio.ui.base.BaseFragment
import com.app.rivisio.ui.image_group.ItemOffsetDecoration
import com.app.rivisio.utils.NetworkResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val homeViewModel: HomeViewModel by viewModels()

    private var topicsAdapter = TopicsAdapter()

    private val binding
        get() = _binding!!

    //topicsMissedList

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setUp(view: View) {

        homeViewModel.userName.observe(this, Observer {
            binding.userName.text = it
        })

        homeViewModel.getUserDetails()

        val homeTabs = HomeTabs(binding)

        homeTabs.setTabClickListener { v ->
            binding.tabToday.isSelected = false
            binding.tabMissed.isSelected = false
            binding.tabUpcoming.isSelected = false
            (v as AppCompatTextView).isSelected = true
        }

        homeTabs.setTabSelected(0)

        binding.topicsList.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.topicsList.addItemDecoration(
            VerticalSpaceItemDecoration(
                requireContext().resources.getDimension(R.dimen.vertical_offset)
                    .toInt()
            )
        )
        binding.topicsList.adapter = topicsAdapter

        homeViewModel.topics.observe(this, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoading()

                    binding.homeIllustrationContainer.visibility = View.GONE
                    binding.homeTabs.visibility = View.VISIBLE
                    binding.topicsList.visibility = View.VISIBLE

                    val myType = object : TypeToken<ArrayList<TopicFromServer>>() {}.type

                    val topics = Gson().fromJson<ArrayList<TopicFromServer>>(
                        it.data.asJsonObject["topicsMissedList"].asJsonArray,
                        myType
                    )

                    createTopicsList(topics)

                }
                is NetworkResult.Loading -> {
                    hideLoading()
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

        homeViewModel.getTopicsData()
    }

    private fun createTopicsList(topics: ArrayList<TopicFromServer>) {
        topicsAdapter.updateItems(topics)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}