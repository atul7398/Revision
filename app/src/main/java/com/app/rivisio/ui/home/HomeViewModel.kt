package com.app.rivisio.ui.home

import com.app.rivisio.data.repository.Repository
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.utils.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel  @Inject constructor(
    private val repository: Repository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(repository) {
}