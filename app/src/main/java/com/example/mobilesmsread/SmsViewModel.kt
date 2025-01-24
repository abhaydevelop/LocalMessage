package com.example.mobilesmsread

import android.content.ContentResolver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

class SmsViewModel(private val contentResolver: ContentResolver) : ViewModel() {

    fun getSmsPagedData(filterSender: String?): Flow<PagingData<SmsModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SmsPagingSource(contentResolver, filterSender) }
        ).flow
    }
}
