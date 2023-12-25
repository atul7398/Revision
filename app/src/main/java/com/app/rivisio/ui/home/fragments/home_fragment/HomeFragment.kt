    package com.app.rivisio.ui.home.fragments.home_fragment
    
    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.webkit.WebView
    import androidx.appcompat.app.AlertDialog
    import androidx.appcompat.widget.AppCompatButton
    import androidx.appcompat.widget.AppCompatImageView
    import androidx.appcompat.widget.AppCompatTextView
    import androidx.fragment.app.viewModels
    import androidx.lifecycle.Observer
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.app.rivisio.R
    import com.app.rivisio.databinding.FragmentHomeBinding
    import com.app.rivisio.ui.base.BaseFragment
    import com.app.rivisio.ui.refer.ReferActivity
    import com.app.rivisio.ui.subscribe.SubscribeActivity
    import com.app.rivisio.ui.topic_details.TopicDetailsActivity
    import com.app.rivisio.utils.CommonUtils
    import com.app.rivisio.utils.NetworkResult
    import com.app.rivisio.utils.makeGone
    import com.app.rivisio.utils.makeVisible
    import com.google.android.material.bottomsheet.BottomSheetDialog
    import com.google.android.material.dialog.MaterialAlertDialogBuilder
    import com.google.gson.Gson
    import com.google.gson.JsonElement
    import com.google.gson.reflect.TypeToken
    import dagger.hilt.android.AndroidEntryPoint
    import org.json.JSONObject
    import timber.log.Timber
    import java.time.LocalDateTime
    import java.time.format.DateTimeFormatter
    
    @AndroidEntryPoint
    class HomeFragment : BaseFragment(), TopicsAdapter.Callback {
    
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
            savedInstanceState: Bundle?,
        ): View {
            _binding = FragmentHomeBinding.inflate(inflater, container, false)
            return binding.root
        }
    
        override fun setUp(view: View) {
    
            binding.goodMorningText.text = CommonUtils.getGreetingMessage()
    
            binding.referButton.setOnClickListener {
                startActivity(ReferActivity.getStartIntent(requireContext()))
            }
    
            binding.subscribe.setOnClickListener {
                startActivity(SubscribeActivity.getStartIntent(requireContext()))
            }
    
            binding.statsButton.setOnClickListener {
                homeViewModel.getUserStats()
            }
    
            binding.howToButton.setOnClickListener {
                showHowToBottomSheet()
            }
    
            binding.learnNow.setOnClickListener {
                homeViewModel.getDailyVocab()
            }
    
            homeViewModel.saveVocab.observe(this, Observer { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        hideLoading()
                    }
                    is NetworkResult.Loading -> {
                        showLoading()
                    }

                    is NetworkResult.Error -> {
                        hideLoading()
                        showError(result.message)
                        if (result.code == 1021){
                            startActivity(SubscribeActivity.getStartIntent(requireContext()))
                        }
                    }
                    is NetworkResult.Exception -> {
                        hideLoading()
                        showError(result.e.message)
                    }
                    else -> {
                        hideLoading()
                        Timber.e(result.toString())
                    }
                }
            })
    
    
            homeViewModel.userStats.observe(this, Observer {
                when (it) {
                    is NetworkResult.Success -> {
                        hideLoading()
                        showStatsBottomSheet(it.data)
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
            topicsAdapter.setCallback(this)
    
            homeViewModel.update.observe(this, Observer {
                when (it) {
                    is NetworkResult.Success -> {
                        hideLoading()
    
                        homeViewModel.getTopicsData()
    
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
                                binding.homeIllustrationMessage.text = "Add Topics to Revise"
                                binding.homeIllustrationText.text =
                                    "Our revision cycle helps you remember anything permanently."
                            } else {
                                binding.homeTabs.visibility = View.VISIBLE
                                homeTabs.setTabClickListener { v ->
                                    when (v.id) {
                                        R.id.tab_today -> {
                                            homeTabs.setTabSelected(0)
                                            showTopicsList(topicsTodayList, true)
                                            if (topicsTodayList.isEmpty()) {
                                                renderEmptyListIllustration()
                                            } else {
                                                renderList()
                                            }
                                        }
    
                                        R.id.tab_missed -> {
                                            homeTabs.setTabSelected(1)
                                            showTopicsList(topicsMissedList)
                                            if (topicsMissedList.isEmpty()) {
                                                renderEmptyListIllustration()
    
                                            } else {
                                                renderList()
                                            }
                                        }
    
                                        R.id.tab_upcoming -> {
                                            homeTabs.setTabSelected(2)
                                            showTopicsList(topicsUpcomingList)
                                            if (topicsUpcomingList.isEmpty()) {
                                                renderEmptyListIllustration()
    
                                            } else {
                                                renderList()
                                            }
                                        }
                                    }
                                }
    
    
                                homeTabs.setTabSelected(0)
                                showTopicsList(topicsTodayList, true)
                                if (topicsTodayList.isEmpty()) {
                                    renderEmptyListIllustration()
                                } else {
                                    renderList()
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
    
    
            homeViewModel.dailyVocab.observe(this, Observer {
                when (it) {
                    is NetworkResult.Success -> {
                        hideLoading()
                        showVocabBottomSheet(it.data)
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
    
            homeViewModel.saveVocab.observe(this, Observer {
                when (it) {
                    is NetworkResult.Success -> {
                        hideLoading()
                        showMessage("Topic Created successfully.")
                        homeViewModel.getTopicsData()
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
    
            homeViewModel.getTopicsData()
        }
    
        private fun showStatsBottomSheet(data: JsonElement) {
            try {
                val referralView = layoutInflater.inflate(R.layout.stats_dialog_layout, null)
                referralView.findViewById<AppCompatTextView>(R.id.total_topic).text =
                    data.asJsonObject["totalCount"].asString
                referralView.findViewById<AppCompatTextView>(R.id.missed_topic).text =
                    data.asJsonObject["missedCount"].asString
                referralView.findViewById<AppCompatTextView>(R.id.ontrack_topic).text =
                    data.asJsonObject["inprogressCount"].asString
                referralView.findViewById<AppCompatTextView>(R.id.completed_topic).text =
                    data.asJsonObject["completedCount"].asString
                val dialog = BottomSheetDialog(requireContext())
                dialog.setContentView(referralView)
                dialog.setCancelable(true)
                dialog.show()
            } catch (e: Exception) {
                Timber.e("Json parsing issue: ")
                Timber.e(e)
                showError("Something went wrong")
            }
        }
    
        private fun renderList() {
            binding.topicsList.makeVisible()
            binding.homeTabs.makeVisible()
            binding.homeIllustrationContainer.makeGone()
        }
    
        private fun renderEmptyListIllustration() {
            binding.topicsList.makeGone()
            binding.homeTabs.makeVisible()
            binding.homeIllustrationContainer.makeVisible()
            binding.homeIllustration.setImageResource(R.drawable.meditation)
            binding.homeIllustrationMessage.text = "No Topics to Revise"
            binding.homeIllustrationText.text =
                "Our revision cycle helps you remember anything permanently."
        }
    
        private fun showTopicsList(topics: ArrayList<TopicFromServer>, isTodaysTopic: Boolean = false) {
            topicsAdapter.updateItems(topics, isTodaysTopic)
        }
    
        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    
        override fun onTopicClick(topicFromServer: TopicFromServer) {
            startActivity(TopicDetailsActivity.getStartIntent(requireContext(), topicFromServer.id))
        }
    
        override fun onTopicReviseButtonClick(topicFromServer: TopicFromServer) {
    
            var revision: Map<String, String>
    
            if (topicFromServer.rev1Status == "wait") {
                revision = mapOf(
                    "rev1" to LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                )
    
                showRevisePrompt(topicFromServer, revision)
                return
            }
    
            if (topicFromServer.rev2Status == "wait") {
                revision = mapOf(
                    "rev2" to LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                )
    
                showRevisePrompt(topicFromServer, revision)
                return
            }
    
            if (topicFromServer.rev3Status == "wait") {
                revision = mapOf(
                    "rev3" to LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                )
    
                showRevisePrompt(topicFromServer, revision)
                return
            }
    
            if (topicFromServer.rev4Status == "wait") {
                revision = mapOf(
                    "rev4" to LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                )
    
                showRevisePrompt(topicFromServer, revision)
                return
            }
        }
    
        private fun showRevisePrompt(topicFromServer: TopicFromServer, revision: Map<String, String>) {
            var dialog: AlertDialog? = null
    
            val view =
                LayoutInflater.from(requireContext()).inflate(R.layout.revise_dialog, null)
    
            val dialogBuilder =
                MaterialAlertDialogBuilder(
                    requireContext(),
                    R.style.ThemeOverlay_App_MaterialAlertDialog
                )
                    .setCancelable(true)
                    .setView(view)
    
    
            view.findViewById<AppCompatButton>(R.id.revise)?.setOnClickListener {
                dialog?.dismiss()
                homeViewModel.reviseTopic(topicFromServer.id, revision)
            }
    
            view.findViewById<AppCompatButton>(R.id.cancel)?.setOnClickListener {
                dialog?.dismiss()
            }
    
            dialog = dialogBuilder.show()
        }
    
    //    private fun showHowToBottomSheet() {
    //        val howToView = layoutInflater.inflate(R.layout.how_to_dialog_layout, null)
    //
    //        val dialog = BottomSheetDialog(requireContext())
    //        dialog.setContentView(howToView)
    //        dialog.setCancelable(true)
    //        dialog.show()
    //
    //        // Find the WebView component in the inflated layout
    //        val webView = howToView.findViewById<WebView>(R.id.how_to_layout)
    //
    //        // Set WebViewClient to handle page loading
    //        webView.webViewClient = WebViewClient()
    //
    //        // Enable JavaScript and other settings
    //        webView.settings.javaScriptEnabled = true
    //        webView.settings.domStorageEnabled = true
    //        webView.settings.builtInZoomControls = true
    //        webView.settings.useWideViewPort = true
    //        webView.settings.loadWithOverviewMode = true
    //
    //        webView.loadUrl("https://snapdragon-consonant-027.notion.site/FAQs-Revu-879b2cc69b3b45dd9e16158c7ca11e83?pvs=4")
    //
    //        webView.makeVisible()
    //    }
    
        private fun showVocabBottomSheet(data: JsonElement) {
            try {
                val vocabView = layoutInflater.inflate(R.layout.vocab_bottom_sheet_layout, null)
                val word: String = data.asJsonObject["word"].asString
                val meaning: String = data.asJsonObject["meaning"].asString
                // Fetch totalCount from userStats LiveData if it's a NetworkResult<JsonElement>
    
                vocabView.findViewById<AppCompatTextView>(R.id.vocab_word).text = word
                vocabView.findViewById<AppCompatTextView>(R.id.vocab_word_desc).text = meaning
    
                val dialog = BottomSheetDialog(requireContext())
                dialog.setContentView(vocabView)
                dialog.setCancelable(true)
    
                vocabView.findViewById<AppCompatImageView>(R.id.shuffle).setOnClickListener {
                    dialog.dismiss()
                    homeViewModel.getDailyVocab()
                }
    
                vocabView.findViewById<AppCompatButton>(R.id.save_vocab).setOnClickListener {
                    Timber.d("Button Clicked")
    //                homeViewModel.getTopicVocab()
                    homeViewModel.saveWordAsTopic(word, meaning)
                }
    //            homeViewModel.topicVocab.observe(this, Observer { result ->
    //                when (result) {
    //                    is NetworkResult.Success -> {
    //                        hideLoading()
    //
    //                        val data = result.data
    //                        val totalCount = data.asJsonObject["totalCount"].asInt
    //                        val limitStats = data.asJsonObject["limitStats"].asJsonObject
    //                        val topicLimit = limitStats.asJsonObject["currentLimit"].asInt + limitStats.asJsonObject["addtionalTopics"].asInt
    //
    //                        if (totalCount < limitStats.asJsonObject["currentLimit"].asInt) {
    //                            dialog.dismiss()
    //                            homeViewModel.saveWordAsTopic(word, meaning)
    //                        }
    //                        else {
    //                            val isActivePlan = data.asJsonObject["isActive"]?.asBoolean ?: false
    //                            if (isActivePlan) {
    //                                homeViewModel.saveWordAsTopic(word, meaning)
    //                            } else if (totalCount < topicLimit) {
    //                                homeViewModel.saveWordAsTopic(word, meaning)
    //                            } else {
    //                                startActivity(SubscribeActivity.getStartIntent(requireContext()))
    //                            }
    //                        }
    //                        dialog.dismiss()
    //                    }
    //                    else -> {
    //                        Timber.e(result.toString())
    //                    }
    //                }})
    
                dialog.show()
            } catch (e: Exception) {
                Timber.e("Json parsing issue: ")
                Timber.e(e)
                showError("Something went wrong")
            }
        }
    
    
    
        private fun showHowToBottomSheet() {
            val howToView = layoutInflater.inflate(R.layout.how_to_dialog_layout, null)
            val dialog = BottomSheetDialog(requireContext())
            dialog.setContentView(howToView)
            dialog.setCancelable(true)
            dialog.show()
    
            // Find the WebView component in the inflated layout
            val webView = howToView.findViewById<WebView>(R.id.how_to_layout)
    
            // Create a sample HTML content to be displayed
            val sampleHtml = ("<html><body>"
                    + "<h1 style=\"font-weight: bold; font-size: 28px;\">How Does It Work?</h1>"
                    + "<p style=\"font-weight: bold; font-size: 18px;\">1. How do we help you remember things forever?</p>"
                    + "<p style=\"font-size: 18px;\">Our algorithm follows Spaced Repetition method for creating revision cycles, that are scientifically proven for maximum retention.</p>"
                    + "<ul style=\"font-size: 18px; list-style-type: disc;\">"
                    + "<li>Day 0 – You Read a Topic</li>"
                    + "<li>Day 1 – Revision 1</li>"
                    + "<li>Day 7 – Revision 2</li>"
                    + "<li>Day 30 – Revision 3</li>"
                    + "<li>Day 90 – Revision 4</li>"
                    + "</ul>"
                    + "<p style=\"font-size: 18px;\">You read a topic on a day, and you revise it the next day, the 7th day, 30th day, and 90th day. This stores the information in your permanent memory for long-term retention.</p>"
                    + "<p style=\"font-weight: bold; font-size: 18px;\">2. What is Forgetting Curve?</p>"
                    + "<p style=\"font-size: 18px;\">According to Ebbinghaus Forgetting Curve, we remember:</p>"
                    + "<ul style=\"font-size: 18px; list-style-type: disc;\">"
                    + "<li>100% of what we learn immediately after we study it</li>"
                    + "<li>33% after 1 Day</li>"
                    + "<li>25% after 1 Week</li>"
                    + "</ul>"
                    + "<p style=\"font-size: 18px;color: red;\">Hence, our retention falls steeply with elapsed time.</p>"
                    + "<p style=\"font-size: 18px;\">Spaced Repetition method flattens the forgetting curve and hence enables us to retain the information long-term.</p>"
                    + "</body></html>")
    
            // Load the sample HTML content into the WebView
            webView.loadData(sampleHtml, "text/html", "UTF-8")
    
            webView.makeVisible()
        }
    
    
    }