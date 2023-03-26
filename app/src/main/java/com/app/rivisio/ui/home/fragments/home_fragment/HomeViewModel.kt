package com.app.rivisio.ui.home.fragments.home_fragment

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
class HomeViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(mainRepository) {

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName

    fun getUserDetails() {
        viewModelScope.launch {
            _userName.value = mainRepository.getName()
        }
    }
}