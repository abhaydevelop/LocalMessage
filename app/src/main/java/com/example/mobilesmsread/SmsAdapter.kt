package com.example.mobilesmsread

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.mobilesmsread.databinding.ItemSmsBinding
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class SmsAdapter : PagingDataAdapter<SmsModel, SmsAdapter.SmsViewHolder>(SMS_COMPARATOR) {

    class SmsViewHolder(val itemsBinding: ItemSmsBinding) : RecyclerView.ViewHolder(itemsBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsViewHolder {
        val itemBinding = ItemSmsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SmsViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: SmsViewHolder, position: Int) {
        val sms = getItem(position)
        sms?.let {
            holder.itemsBinding.senderTextView.text = it.sender
            holder.itemsBinding.messageTextView.text = it.message
            holder.itemsBinding.timeTextView.text = it.time
        }
    }

    companion object {
        private val SMS_COMPARATOR = object : DiffUtil.ItemCallback<SmsModel>() {
            override fun areItemsTheSame(oldItem: SmsModel, newItem: SmsModel): Boolean {
                return oldItem.sender == newItem.sender && oldItem.time == newItem.time
            }

            override fun areContentsTheSame(oldItem: SmsModel, newItem: SmsModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}

