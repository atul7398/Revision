package com.app.rivisio.ui.home.fragments.topics

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rivisio.R
import com.app.rivisio.databinding.FragmentTopicsBinding
import com.app.rivisio.ui.base.BaseFragment
import com.app.rivisio.ui.home.fragments.home_fragment.TopicFromServer
import com.app.rivisio.ui.home.fragments.home_fragment.VerticalSpaceItemDecoration
import com.app.rivisio.ui.topic_details.TopicDetailsActivity
import com.app.rivisio.utils.NetworkResult
import com.app.rivisio.utils.makeGone
import com.app.rivisio.utils.makeVisible
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class TopicsFragment : BaseFragment(), TopicsAdapterNew.Callback {

    private var _binding: FragmentTopicsBinding? = null
    private val topicViewModel: TopicViewModel by viewModels()

    private var topicsAdapter = TopicsAdapterNew()

    val statusFilter = arrayListOf<String>()

    private val binding
        get() = _binding!!

    companion object {

        const val TOPIC_STATUS_IN_PROGRESS = "in_progress"
        const val TOPIC_STATUS_CREATED = "created"
        const val TOPIC_STATUS_STOP = "stop"

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

        binding.searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                topicsAdapter.filterList(s.toString())
            }
        })

        binding.filter.setOnClickListener {

            val filterView = layoutInflater.inflate(R.layout.filter_dialog_layout, null)
            val dialog = BottomSheetDialog(requireContext())

            if (statusFilter.contains(TOPIC_STATUS_IN_PROGRESS))
                filterView.findViewById<CheckBox>(R.id.on_track).isChecked = true

            if (statusFilter.contains(TOPIC_STATUS_CREATED))
                filterView.findViewById<CheckBox>(R.id.created).isChecked = true

            if (statusFilter.contains(TOPIC_STATUS_STOP))
                filterView.findViewById<CheckBox>(R.id.stopped).isChecked = true

            val listener: (buttonView: CompoundButton, isChecked: Boolean) -> Unit =
                { compoundButton: CompoundButton, isChecked: Boolean ->
                    if (isChecked)
                        statusFilter.add(compoundButton.tag.toString())
                    else
                        statusFilter.remove(compoundButton.tag.toString())
                    Timber.e("=================================")
                    statusFilter.forEach {
                        Timber.e(it)
                    }
                }

            filterView.findViewById<CheckBox>(R.id.on_track).setOnCheckedChangeListener(listener)
            filterView.findViewById<CheckBox>(R.id.created).setOnCheckedChangeListener(listener)
            filterView.findViewById<CheckBox>(R.id.stopped).setOnCheckedChangeListener(listener)

            filterView.findViewById<AppCompatButton>(R.id.apply_button).setOnClickListener {
                topicsAdapter.filterListByStatus(statusFilter)
                dialog.dismiss()
                binding.filterCount.text = statusFilter.size.toString()
                binding.filterCount.makeVisible()
            }

            filterView.findViewById<AppCompatTextView>(R.id.clear_all).setOnClickListener {
                statusFilter.clear()
                topicsAdapter.filterListByStatus(statusFilter)
                dialog.dismiss()
                binding.filterCount.text = statusFilter.size.toString()
                binding.filterCount.makeGone()
            }

            dialog.setContentView(filterView)
            dialog.setCancelable(true)
            dialog.show()

        }

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
                            topicsAdapter.addItems(topics)
                        } else {
                            binding.topicsIllustrationImage.makeVisible()
                            binding.topicsIllustrationMessage.makeVisible()
                            binding.topicsIllustrationText.makeVisible()
                            binding.topicList.makeGone()
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

    override fun onTopicReviseButtonClick(topicFromServer: TopicFromServer) {
        //Do nothing here
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}