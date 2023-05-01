package com.app.rivisio.ui.home.fragments.home_fragment

data class TopicFromServer(
    var id: Int? = null,
    var version: Int? = null,
    var name: String? = null,
    var status: String? = null,
    var userId: Int? = null,
    var imageUrls: String? = null,
    var notes: String? = null,
    var createdOn: String? = null,
    var modifiedOn: String? = null,
    var studiedOn: String? = null,
    var tagsList: String? = null
)