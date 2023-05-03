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
                    try {

                        val myType = object : TypeToken<ArrayList<TopicFromServer>>() {}.type

                        val topicsMissedList = Gson().fromJson<ArrayList<TopicFromServer>>(
                            it.data.asJsonObject["topicsMissedList"].asJsonArray,
                            myType
                        )

                        val topicsTodayList = Gson().fromJson<ArrayList<TopicFromServer>>(
                            it.data.asJsonObject["topicsTodayList"].asJsonArray,
                            myType
                        )

                        val topicsUpcomingList = Gson().fromJson<ArrayList<TopicFromServer>>(
                            it.data.asJsonObject["topicsUpcomingList"].asJsonArray,
                            myType
                        )

                        if (topicsTodayList.isEmpty() && topicsMissedList.isEmpty() && topicsUpcomingList.isEmpty()) {
                            binding.topicsList.visibility = View.GONE
                            binding.homeTabs.visibility = View.GONE
                            binding.homeIllustrationContainer.visibility = View.VISIBLE
                            binding.homeIllustration.setImageResource(R.drawable.start_journey)
                            binding.homeIllustrationMessage.text = "Start Your Journey"
                            binding.homeIllustrationText.text =
                                "Every big step start with small step. Add your first topic and start your journey!"
                        } else {
                            binding.homeTabs.visibility = View.VISIBLE
                            homeTabs.setTabClickListener { v ->
                                when (v.id) {
                                    R.id.tab_today -> {
                                        homeTabs.setTabSelected(0)
                                        createTopicsList(topicsTodayList)
                                        if (topicsTodayList.isEmpty()) {
                                            binding.topicsList.visibility = View.GONE
                                            binding.homeTabs.visibility = View.VISIBLE
                                            binding.homeIllustrationContainer.visibility =
                                                View.VISIBLE
                                            binding.homeIllustration.setImageResource(R.drawable.meditation)
                                            binding.homeIllustrationMessage.text = "No topics here"
                                            binding.homeIllustrationText.text = ""
                                        } else {
                                            binding.topicsList.visibility = View.VISIBLE
                                            binding.homeTabs.visibility = View.VISIBLE
                                            binding.homeIllustrationContainer.visibility = View.GONE
                                        }
                                    }
                                    R.id.tab_missed -> {
                                        homeTabs.setTabSelected(1)
                                        createTopicsList(topicsMissedList)
                                        if (topicsMissedList.isEmpty()) {
                                            binding.topicsList.visibility = View.GONE
                                            binding.homeTabs.visibility = View.VISIBLE
                                            binding.homeIllustrationContainer.visibility =
                                                View.VISIBLE
                                            binding.homeIllustration.setImageResource(R.drawable.meditation)
                                            binding.homeIllustrationMessage.text = "No topics here"
                                            binding.homeIllustrationText.text = ""

                                        } else {
                                            binding.topicsList.visibility = View.VISIBLE
                                            binding.homeTabs.visibility = View.VISIBLE
                                            binding.homeIllustrationContainer.visibility = View.GONE
                                        }
                                    }
                                    R.id.tab_upcoming -> {
                                        homeTabs.setTabSelected(2)
                                        createTopicsList(topicsUpcomingList)
                                        if (topicsUpcomingList.isEmpty()) {
                                            binding.topicsList.visibility = View.GONE
                                            binding.homeTabs.visibility = View.VISIBLE
                                            binding.homeIllustrationContainer.visibility =
                                                View.VISIBLE
                                            binding.homeIllustration.setImageResource(R.drawable.meditation)
                                            binding.homeIllustrationMessage.text = "No topics here"
                                            binding.homeIllustrationText.text = ""

                                        } else {
                                            binding.topicsList.visibility = View.VISIBLE
                                            binding.homeTabs.visibility = View.VISIBLE
                                            binding.homeIllustrationContainer.visibility = View.GONE
                                        }
                                    }
                                }
                            }


                            homeTabs.setTabSelected(0)
                            createTopicsList(topicsTodayList)
                            if (topicsTodayList.isEmpty()) {
                                binding.topicsList.visibility = View.GONE
                                binding.homeTabs.visibility = View.VISIBLE
                                binding.homeIllustrationContainer.visibility = View.VISIBLE
                                binding.homeIllustration.setImageResource(R.drawable.meditation)
                                binding.homeIllustrationMessage.text = "No topics here"
                                binding.homeIllustrationText.text = ""
                            } else {
                                binding.topicsList.visibility = View.VISIBLE
                                binding.homeTabs.visibility = View.VISIBLE
                                binding.homeIllustrationContainer.visibility = View.GONE
                            }
                        }

                    } catch (e: Exception) {
                        Timber.e("Json parsing issue: ")
                        Timber.e(e)
                        showError("Something went wrong")
                    }

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