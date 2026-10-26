# JARVIS AI — Android starter project

A small native Android starter app for Samsung Galaxy M31 / Android 12. It currently supports:
- Tap-to-speak using Android speech recognition (Hindi-first recognition setting)
- Spoken replies via Android Text-to-Speech
- Opening YouTube and WhatsApp
- Web search and YouTube search
- Opening Android Settings and the alarm screen for reminders

## Important limitations
- This is source code, **not a ready-built APK**.
- The custom always-listening wake phrase "Hey Jarvis" is not implemented. Android does not provide a safe, unrestricted always-on custom wake word by default; this needs a dedicated wake-word engine and foreground service, plus battery/privacy testing.
- The current version does not yet have a general AI backend. Web search opens Google results rather than generating an AI answer.
- WhatsApp sending is not automated; it opens the app so the user can choose a contact and review the message.
- Some device actions require Android permissions, unlock, or user confirmation.
- No API keys are included. Keep any future API keys out of public source repositories.

## Build APK without a computer
1. Create/sign in to a GitHub account.
2. Create a new repository named `jarvis-ai-android`.
3. Upload the files in this ZIP, preserving folder structure.
4. Open the repository's **Actions** tab.
5. Choose **Build JARVIS APK** and press **Run workflow** (or push to `main`).
6. When the workflow finishes, open its run and download the `JARVIS-AI-debug-apk` artifact.
7. Extract the downloaded artifact and install `app-debug.apk` on your phone. Android may ask you to allow installs from that source.

## Next development stages
1. Test this basic app on the M31.
2. Add better Hindi/Hinglish intent recognition and editable command aliases.
3. Add a local reminder database and notifications.
4. Add a privacy-aware AI answer service (free tier availability may change; never embed private API keys in the APK).
5. Add a wake-word engine with a foreground service and visible microphone notification.
6. Add accessibility automation only for specific tasks, with explicit user setup and safeguards.
