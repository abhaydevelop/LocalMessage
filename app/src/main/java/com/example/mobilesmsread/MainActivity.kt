package com.example.mobilesmsread

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Telephony
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobilesmsread.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val smsList = mutableListOf<SmsModel>()
    private lateinit var adapter: SmsAdapter
    private var filterSender: String? = null
    private lateinit var binding: ActivityMainBinding

    private lateinit var smsViewModel: SmsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.color_121212)
        // Set white content (icons and text) in the status bar
        window.decorView.systemUiVisibility = 0 // Clear LIGHT_STATUS_BAR flag

        // Initialize the custom ViewModelFactory
        val factory = SmsViewModelFactory(contentResolver)
        smsViewModel = ViewModelProvider(this, factory).get(SmsViewModel::class.java)

        adapter = SmsAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Set filter sender ID
        binding.filterButton.setOnClickListener {
            filterSender = binding.senderIdEditText.text.toString().takeIf { it.isNotEmpty() }
            loadPagedData(filterSender)
        }

        // Register broadcast receiver for new SMS
        registerReceiver(smsReceiver, IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
    }

   /* private fun loadSms() {
        smsList.clear()
        val cursor = contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            arrayOf(Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE),
            null,
            null,
            Telephony.Sms.DEFAULT_SORT_ORDER
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

                if (filterSender == null || sender.contains(filterSender!!, ignoreCase = true)) {
                    smsList.add(SmsModel(sender, body, time))
                }
            }
        }
        adapter.notifyDataSetChanged()
    }*/

    private fun loadPagedData(filterSender: String?) {
        lifecycleScope.launch {
            smsViewModel.getSmsPagedData(filterSender).collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private val smsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
                val bundle = intent.extras
                val pdus = bundle?.get("pdus") as? Array<*>
                pdus?.forEach { pdu ->
                    val sms = Telephony.Sms.Intents.getMessagesFromIntent(intent)[0]
                    val sender = sms.displayOriginatingAddress
                    val body = sms.displayMessageBody
                    val time = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date(sms.timestampMillis))

                    if (filterSender == null || sender.contains(filterSender!!, ignoreCase = true)) {
                        loadPagedData(null)
                        smsList.add(0, SmsModel(sender, body, time))
                        adapter.notifyItemInserted(0)
                    }else{
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(smsReceiver)
    }

    override fun onResume() {
        super.onResume()

        // Request permissions
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_SMS, android.Manifest.permission.RECEIVE_SMS), 101)
        } else {
            loadPagedData(null)
        }
    }
}