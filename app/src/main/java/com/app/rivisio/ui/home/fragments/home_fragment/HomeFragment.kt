package com.app.rivisio.ui.home.fragments.home_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.app.rivisio.R
import com.app.rivisio.data.network.AWS_URL
import com.app.rivisio.databinding.FragmentHomeBinding
import com.app.rivisio.ui.add_topic.TOPIC_NAME
import com.app.rivisio.ui.add_topic.Topic
import com.app.rivisio.ui.base.BaseFragment
import com.app.rivisio.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val homeViewModel: HomeViewModel by viewModels()

    private val binding
        get() = _binding!!

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

        homeViewModel.topics.observe(this, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoading()
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

        homeViewModel.getTopics()
    }
}