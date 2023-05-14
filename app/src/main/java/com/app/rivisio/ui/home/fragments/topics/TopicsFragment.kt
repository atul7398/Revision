package com.app.rivisio.ui.home.fragments.topics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rivisio.R
import com.app.rivisio.databinding.FragmentHomeBinding
import com.app.rivisio.databinding.FragmentTopicsBinding
import com.app.rivisio.ui.base.BaseFragment
import com.app.rivisio.ui.home.fragments.home_fragment.HomeFragment
import com.app.rivisio.ui.home.fragments.home_fragment.HomeViewModel
import com.app.rivisio.ui.home.fragments.home_fragment.TopicFromServer
import com.app.rivisio.ui.home.fragments.home_fragment.TopicsAdapter
import com.app.rivisio.ui.home.fragments.home_fragment.VerticalSpaceItemDecoration
import com.app.rivisio.ui.topic_details.TopicDetailsActivity
import com.app.rivisio.utils.NetworkResult
import com.app.rivisio.utils.makeGone
import com.app.rivisio.utils.makeInVisible
import com.app.rivisio.utils.makeVisible
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class TopicsFragment : BaseFragment(), TopicsAdapter.Callback {

    private var _binding: FragmentTopicsBinding? = null
    private val topicViewModel: TopicViewModel by viewModels()

    private var topicsAdapter = TopicsAdapter()

    private val binding
        get() = _binding!!

    companion object {
        @JvmStatic
        fun newInstance() = TopicsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTopicsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setUp(view: View) {

        binding.topicList.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.topicList.addItemDecoration(
            VerticalSpaceItemDecoration(
                requireContext().resources.getDimension(R.dimen.vertical_offset)
                    .toInt()
            )
        )
        binding.topicList.adapter = topicsAdapter
        topicsAdapter.setCallback(this)

        topicViewModel.topics.observe(this, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoading()
                    try {

                        val myType = object : TypeToken<ArrayList<TopicFromServer>>() {}.type

                        val topics = Gson().fromJson<ArrayList<TopicFromServer>>(
                            it.data.asJsonArray,
                            myType
                        )

                        if (topics.isNotEmpty()) {
                            binding.topicsIllustrationImage.makeGone()
                            binding.topicsIllustrationMessage.makeGone()
                            binding.topicsIllustrationText.makeGone()
                            binding.topicList.makeVisible()
                            topicsAdapter.updateItems(topics)
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

        topicViewModel.getTopicsData()
    }

    override fun onTopicClick(topicFromServer: TopicFromServer) {
        startActivity(TopicDetailsActivity.getStartIntent(requireContext(), topicFromServer.id))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}