package com.app.rivisio.ui.add_notes

import com.app.rivisio.data.repository.MainRepository
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.utils.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddNotesViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(mainRepository) {

}