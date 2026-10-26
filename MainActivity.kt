package com.personal.jarvis

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var status: TextView
    private lateinit var tts: TextToSpeech
    private val requestSpeech = 101
    private val requestMic = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 40, 28, 40)
        }
        val title = TextView(this).apply {
            text = "J.A.R.V.I.S."
            textSize = 32f
            gravity = Gravity.CENTER
        }
        status = TextView(this).apply {
            text = "Your assistant is ready. Tap the microphone and speak."
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 24)
        }
        val listen = Button(this).apply {
            text = "🎙 Speak to JARVIS"
            setOnClickListener { startListening() }
        }
        val help = Button(this).apply {
            text = "Show example commands"
            setOnClickListener {
                status.text = "Try: open YouTube; open WhatsApp; search cats; play relaxing music; set a reminder for 7 PM."
                speak("Try saying open YouTube, open WhatsApp, search cats, or set a reminder for seven PM.")
            }
        }
        layout.addView(title)
        layout.addView(status)
        layout.addView(listen)
        layout.addView(help)
        setContentView(layout)
    }

    override fun onInit(statusCode: Int) {
        if (statusCode == TextToSpeech.SUCCESS) tts.language = Locale("en", "IN")
    }

    private fun startListening() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), requestMic)
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to JARVIS in Hindi, English or Hinglish")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }
        try {
            startActivityForResult(intent, requestSpeech)
        } catch (_: Exception) {
            status.text = "Speech recognition is not available on this phone."
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestMic && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening()
        } else if (requestCode == requestMic) {
            status.text = "Microphone permission is needed for voice commands."
        }
    }

    @Deprecated("Deprecated by Android, kept for broad compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestSpeech && resultCode == RESULT_OK) {
            val spoken = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull().orEmpty()
            status.text = "You said: $spoken"
            handleCommand(spoken.lowercase(Locale.ROOT))
        }
    }

    private fun handleCommand(command: String) {
        when {
            command.contains("open youtube") || command.contains("यूट्यूब खोल") -> {
                launchPackage("com.google.android.youtube", "YouTube")
            }
            command.contains("open whatsapp") || command.contains("व्हाट्सएप खोल") -> {
                launchPackage("com.whatsapp", "WhatsApp")
            }
            command.contains("search ") || command.contains("सर्च ") || command.contains("खोजो") -> {
                val query = command
                    .replace("search", "")
                    .replace("सर्च", "")
                    .replace("खोजो", "")
                    .trim()
                val url = "https://www.google.com/search?q=" + Uri.encode(query)
                openUrl(url, "Searching the web.")
            }
            command.contains("play ") || command.contains("गाना") || command.contains("म्यूजिक") -> {
                val query = command.replace("play", "").trim().ifBlank { "music" }
                openUrl("https://www.youtube.com/results?search_query=" + Uri.encode(query), "Opening YouTube results.")
            }
            command.contains("settings") || command.contains("सेटिंग") -> {
                try {
                    startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
                    reply("Opening phone settings.")
                } catch (_: Exception) { reply("I could not open settings.") }
            }
            command.contains("reminder") || command.contains("alarm") || command.contains("रिमाइंडर") || command.contains("अलार्म") -> {
                try {
                    val i = Intent(android.provider.AlarmClock.ACTION_SET_ALARM)
                        .putExtra(android.provider.AlarmClock.EXTRA_MESSAGE, "JARVIS reminder")
                        .putExtra(android.provider.AlarmClock.EXTRA_SKIP_UI, false)
                    startActivity(i)
                    reply("Opening your alarm app. Set the time to finish the reminder.")
                } catch (_: Exception) { reply("I could not open the alarm app.") }
            }
            command.contains("message") || command.contains("whatsapp") || command.contains("मैसेज") -> {
                try {
                    val i = packageManager.getLaunchIntentForPackage("com.whatsapp")
                    if (i != null) startActivity(i) else openUrl("https://www.whatsapp.com/", "Opening WhatsApp.")
                    reply("Opening WhatsApp. Please choose the contact and check your message before sending.")
                } catch (_: Exception) { reply("I could not open WhatsApp.") }
            }
            else -> {
                reply("I heard you, but this command is not supported yet. Try open YouTube or search followed by your topic.")
            }
        }
    }

    private fun launchPackage(packageName: String, appName: String) {
        val launch = packageManager.getLaunchIntentForPackage(packageName)
        if (launch != null) {
            startActivity(launch)
            reply("Opening $appName.")
        } else {
            reply("$appName does not appear to be installed.")
        }
    }

    private fun openUrl(url: String, response: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            reply(response)
        } catch (_: Exception) { reply("I could not open that link.") }
    }

    private fun reply(text: String) {
        status.text = text
        speak(text)
    }

    private fun speak(text: String) {
        if (::tts.isInitialized) tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "jarvis_reply")
    }

    override fun onDestroy() {
        if (::tts.isInitialized) tts.shutdown()
        super.onDestroy()
    }
}
