package com.app.rivisio.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.rivisio.data.prefs.UserState
import com.app.rivisio.data.repository.MainRepository
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.utils.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(mainRepository) {

    private val _isUserLoggedIn = MutableLiveData<Boolean>()
    val isUserLoggedIn: LiveData<Boolean>
        get() = _isUserLoggedIn

    fun setUserState(userState: UserState) {
        viewModelScope.launch {
            mainRepository.setUserState(userState = userState)
        }
    }

    fun setUserDetails(email: String, displayName: String) {
        viewModelScope.launch {
            mainRepository.setUserEmail(email)
            mainRepository.setName(displayName)
            _isUserLoggedIn.value = true
        }
    }
}