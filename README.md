# Mobile SMS Reader App

## Overview
This project is an Android application that reads SMS messages from the device's inbox and allows users to filter messages based on the sender's ID. The app uses Android's built-in SMS content provider and dynamically requests permissions to ensure a smooth user experience.

## Features
- **Filter Messages by Sender ID**: Users can input a sender ID to filter messages.
- **Dynamic Permissions**: Requests SMS-related permissions when required.
- **Real-time Updates**: Listens for new incoming messages and updates the view dynamically.
- **Edge-to-Edge UI**: Implements modern edge-to-edge design principles.

## Permissions
This application requires the following permissions to function:
- `android.permission.RECEIVE_SMS`: To receive new SMS messages.
- `android.permission.READ_SMS`: To read SMS messages from the inbox.
- `android.permission.READ_PHONE_STATE`: To access phone state information if needed.

Ensure you grant these permissions during runtime for the app to work as intended.

## How It Works
1. **MainActivity**:
   - Sets up the UI using `ActivityMainBinding`.
   - Handles permission checks and SMS reading logic.
   - Filters messages based on user input.

2. **SMS Broadcast Receiver**:
   - Listens for incoming SMS messages via `Telephony.Sms.Intents.SMS_RECEIVED_ACTION`.
   - Updates the UI dynamically when a new message is received.

3. **RecyclerView**:
   - Displays the list of messages using an adapter (`SmsAdapter`).

4. **Permissions Handling**:
   - Checks and requests `READ_SMS` and `RECEIVE_SMS` permissions dynamically.
   - Displays a `Toast` message if the permission is denied.

## Implementation Details
### Main Components
- **`MainActivity`**: Contains the core functionality.
- **BroadcastReceiver**: Handles incoming SMS updates.
- **Content Provider**: Reads messages from the inbox.

### XML Configuration
Add the following permissions and features in `AndroidManifest.xml`:
```xml
<uses-feature
    android:name="android.hardware.telephony"
    android:required="false" />

<uses-permission android:name="android.permission.RECEIVE_SMS" />
<uses-permission android:name="android.permission.READ_SMS" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
```

### Runtime Permissions
Permissions are dynamically requested at runtime using:
```kotlin
if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(android.Manifest.permission.READ_SMS, android.Manifest.permission.RECEIVE_SMS),
        101
    )
}
```

### SMS Filtering
Filter messages using:
```kotlin
filterSender = binding.senderIdEditText.text.toString().takeIf { it.isNotEmpty() }
if (filterSender == null || sender.contains(filterSender!!, ignoreCase = true)) {
    smsList.add(SmsModel(sender, body, time))
}
```

### BroadcastReceiver
Handles new SMS messages:
```kotlin
private val smsReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val sms = Telephony.Sms.Intents.getMessagesFromIntent(intent)[0]
            val sender = sms.displayOriginatingAddress
            val body = sms.displayMessageBody
            // Add to list if it matches the filter
        }
    }
}
```

## Setup and Usage
1. Clone the repository.
2. Open the project in Android Studio.
3. Run the app on an emulator or physical device with SMS capabilities.
4. Grant the requested permissions when prompted.
5. Enter a sender ID and press the "Filter" button to view filtered messages.

## Notes
- This application requires a device with SMS capabilities.
- Ensure the necessary permissions are granted for full functionality.

## Future Enhancements
- Add support for grouping messages by date or sender.
- Implement a search feature for message content.
- Add a dark mode toggle.

## License
This project is licensed under the MIT License. Feel free to use and modify as needed.

![1](https://github.com/user-attachments/assets/2b45c421-a31c-43c2-a556-5d412c338cfa)

![2](https://github.com/user-attachments/assets/a7d43ad2-1503-44bf-a2ef-fe1acc77134d)


![3](https://github.com/user-attachments/assets/5daabb77-cd27-40bf-a589-6dbf86d52deb)





