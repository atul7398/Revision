package com.app.rivisio.ui.home.fragments.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.rivisio.data.repository.MainRepository
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.utils.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(mainRepository) {

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String>
        get() = _userEmail

    fun getUserDetails() {
        viewModelScope.launch {
            _userName.value = mainRepository.getName()
            _userEmail.value = mainRepository.getUserEmail()
        }
    }
}