package com.app.rivisio.ui.add_topic

data class Tag(
    val name: String,
    val id: Int,
    val color: String
) {
    override fun toString(): String {
        return name
    }
}

var tags = listOf(
    Tag("History", 1, "#F74940"),
    Tag("Science", 2, "#FFB904"),
    Tag("Maths", 3, "#FA8A06"),
    Tag("Geography", 4, "#9AC002"),
    Tag("Chemistry", 5, "#1CB0F7"),
    Tag("Biology", 6, "#A141FB"),
    Tag("Computers", 7, "#A17D42")
)

