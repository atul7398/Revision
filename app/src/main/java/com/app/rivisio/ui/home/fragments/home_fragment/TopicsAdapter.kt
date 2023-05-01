package com.app.rivisio.ui.home.fragments.home_fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.app.rivisio.R
import javax.inject.Inject

class TopicsAdapter(var topic: ArrayList<TopicFromServer> = arrayListOf()) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    fun updateItems(topic: ArrayList<TopicFromServer>) {
        this.topic = topic
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.topic_list_item, parent, false)

        return TopicViewHolder(view)
    }

    override fun getItemCount() = topic.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TopicViewHolder).onBind(topic[position])
    }

    inner class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun onBind(topicFromServer: TopicFromServer) {
            itemView.findViewById<AppCompatTextView>(R.id.topic_name).text = topicFromServer.name
        }
    }
}