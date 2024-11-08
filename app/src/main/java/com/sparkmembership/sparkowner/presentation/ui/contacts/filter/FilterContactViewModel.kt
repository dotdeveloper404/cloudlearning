package com.sparkmembership.sparkowner.presentation.ui.contacts.filter

import com.sparkmembership.sparkowner.config.AppConfig
import com.sparkmembership.sparkowner.domain.repository.LocalRepository
import com.sparkmembership.sparkowner.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FilterContactViewModel @Inject constructor(
    private val appConfig: AppConfig,
    private val localRepository: LocalRepository
) : BaseViewModel() {


}