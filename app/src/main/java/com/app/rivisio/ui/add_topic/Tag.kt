package com.app.rivisio.ui.add_topic

data class Tag(
    val id: Int,
    val name: String,
    val hexCode: String
) {
    override fun toString(): String {
        return name
    }
}

