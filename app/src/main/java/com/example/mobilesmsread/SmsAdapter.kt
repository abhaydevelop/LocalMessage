package com.example.mobilesmsread

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilesmsread.databinding.ItemSmsBinding

class SmsAdapter(private val smsList: List<SmsModel>) : RecyclerView.Adapter<SmsAdapter.SmsViewHolder>() {

    class SmsViewHolder(val itemsBinding: ItemSmsBinding) : RecyclerView.ViewHolder(itemsBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsViewHolder {
        val itemBinding = ItemSmsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SmsViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: SmsViewHolder, position: Int) {
        val sms = smsList[position]
        holder.itemsBinding.senderTextView.text = sms.sender
        holder.itemsBinding.messageTextView.text = sms.message
        holder.itemsBinding.timeTextView.text = sms.time
    }

    override fun getItemCount(): Int = smsList.size
}
