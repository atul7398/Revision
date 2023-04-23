package com.app.rivisio.ui.home.fragments.topics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.rivisio.R

class TopicsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_topics, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = TopicsFragment()
    }
}