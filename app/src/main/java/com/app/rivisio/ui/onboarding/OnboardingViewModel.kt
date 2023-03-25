package com.app.rivisio.ui.onboarding

import androidx.lifecycle.viewModelScope
import com.app.rivisio.data.prefs.UserState
import com.app.rivisio.data.repository.MainRepository
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.utils.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(mainRepository) {

    fun setUserState(userState: UserState) {
        viewModelScope.launch {
            mainRepository.setUserState(userState = userState)
        }
    }
}