package com.app.rivisio.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.rivisio.data.repository.Repository
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.utils.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: Repository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(repository) {

    private val _notificationSetting = MutableLiveData<Boolean>()
    val notificationSetting: LiveData<Boolean>
        get() = _notificationSetting

    fun isNotificationEnabled() {
        viewModelScope.launch {

            _notificationSetting.value = repository.isNotificationEnabled()
        }
    }
}