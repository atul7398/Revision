package com.app.rivisio.ui.subscribe

import com.app.rivisio.data.repository.Repository
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.utils.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SubscribeViewModel @Inject constructor(
    private val repository: Repository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(repository) {


}
