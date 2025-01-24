package com.example.mobilesmsread

import androidx.paging.PagingSource
import androidx.paging.PagingState
import android.content.ContentResolver
import android.provider.Telephony
import java.text.SimpleDateFormat
import java.util.*

class SmsPagingSource(
    private val contentResolver: ContentResolver,
    private val filterSender: String?
) : PagingSource<Int, SmsModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SmsModel> {
        try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            val offset = page * pageSize

            val smsList = mutableListOf<SmsModel>()
            val cursor = contentResolver.query(
                Telephony.Sms.Inbox.CONTENT_URI,
                arrayOf(Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE),
                null,
                null,
                "${Telephony.Sms.DEFAULT_SORT_ORDER} LIMIT $pageSize OFFSET $offset"
            )

            cursor?.use {
                val addressIndex = it.getColumnIndex(Telephony.Sms.ADDRESS)
                val bodyIndex = it.getColumnIndex(Telephony.Sms.BODY)
                val dateIndex = it.getColumnIndex(Telephony.Sms.DATE)

                while (it.moveToNext()) {
                    val sender = it.getString(addressIndex)
                    val body = it.getString(bodyIndex)
                    val date = it.getLong(dateIndex)
                    val time = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date(date))

                    if (filterSender == null || sender.contains(filterSender, ignoreCase = true)) {
                        smsList.add(SmsModel(sender, body, time))
                    }
                }
            }

            val nextKey = if (smsList.size < pageSize) null else page + 1
            return LoadResult.Page(data = smsList, prevKey = null, nextKey = nextKey)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, SmsModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
