package com.app.rivisio.ui.home.fragments.home_fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.app.rivisio.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.shape.ShapeAppearanceModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

            itemView.findViewById<ChipGroup>(R.id.selected_tags).removeAllViews()

            topicFromServer.tagsList.forEach {
                addTagChip(it)
            }

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val dateTime = LocalDateTime.parse(topicFromServer.studiedOn, formatter)

            itemView.findViewById<AppCompatTextView>(R.id.started_on).text =
                "Started on : ${getFormattedDate(dateTime)}"

            itemView.findViewById<AppCompatTextView>(R.id.date_1).text =
                getFormattedDate(dateTime.plusDays(1))

            itemView.findViewById<AppCompatTextView>(R.id.date_2).text =
                getFormattedDate(dateTime.plusDays(3))

            itemView.findViewById<AppCompatTextView>(R.id.date_3).text =
                getFormattedDate(dateTime.plusDays(7))

            itemView.findViewById<AppCompatTextView>(R.id.date_4).text =
                getFormattedDate(dateTime.plusDays(10))

            if (!topicFromServer.rev1Status.isNullOrEmpty())
                itemView.findViewById<View>(R.id.circle_1).backgroundTintList =
                    getColorForRevision(topicFromServer.rev1Status)

            if (!topicFromServer.rev2Status.isNullOrEmpty())
                itemView.findViewById<View>(R.id.circle_2).backgroundTintList =
                    getColorForRevision(topicFromServer.rev2Status)

            if (!topicFromServer.rev3Status.isNullOrEmpty())
                itemView.findViewById<View>(R.id.circle_3).backgroundTintList =
                    getColorForRevision(topicFromServer.rev3Status)

            if (!topicFromServer.rev4Status.isNullOrEmpty())
                itemView.findViewById<View>(R.id.circle_4).backgroundTintList =
                    getColorForRevision(topicFromServer.rev4Status)
        }

        private fun getColorForRevision(revStatus: String?): ColorStateList {
            return when (revStatus) {
                "stop" -> {
                    ColorStateList.valueOf(Color.parseColor("#F69032"))
                }
                "wait" -> {
                    ColorStateList.valueOf(Color.parseColor("#FFB904"))
                }
                else -> { //done
                    ColorStateList.valueOf(Color.parseColor("#0E965E"))
                }
            }
        }

        private fun getFormattedDate(dateTime: LocalDateTime): String {
            val formatter2 = DateTimeFormatter.ofPattern("dd/MM")
            return dateTime.format(formatter2)
        }

        private fun addTagChip(tag: TagFromServer) {
            itemView.findViewById<ChipGroup>(R.id.selected_tags).addView(getChip(tag))
        }

        private fun getChip(tag: TagFromServer): Chip {
            return Chip(itemView.context).apply {
                text = tag.name
                shapeAppearanceModel =
                    ShapeAppearanceModel().withCornerSize(itemView.context.resources.getDimension(R.dimen.chip_corner_Radius))
                chipBackgroundColor =
                    ColorStateList.valueOf(Color.parseColor(if (tag.hexCode!!.startsWith("#")) tag.hexCode else "#" + tag.hexCode))
                isCloseIconVisible = false
                chipStrokeWidth = 0f
            }
        }
    }
}