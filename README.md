# 🤖 BroBot — Voice Activated Personal AI Agent

> **"Hey Bro, write a note about my project idea"**
> BroBot listens, understands, and saves it automatically. No buttons. No typing. 100% hands-free.

---

## 👥 Team

| 👨‍💻 Person | 🎯 Role | 🌿 Branch |
|---|---|---|
| Nazeer | Android Developer | `android` |
| G. Ruchitha | Backend Developer | `backend` |

---

## 🔄 How BroBot Works

```
📱 You say "Hey Bro"
        │
        ▼
🎙️  Vosk detects wake word
    (offline · no internet needed · always listening)
        │
        ▼
⏺️  MediaRecorder records your command
    (5 seconds · saves as command.mp4)
        │
        ▼
📤  OkHttp sends audio to FastAPI backend
    (POST /process-audio · multipart/form-data)
        │
        ▼
🔊  Whisper converts audio → text
    (open source · highly accurate)
        │
        ▼
🧠  Groq + Llama 3 understands your intent
    (free tier · structures your command)
        │
        ▼
🔧  MCP Tools execute the action
    (Google Drive · Gmail · Calendar)
        │
        ▼
✅  Result sent back to your phone
    ("Note saved!" · "Email sent!" · "Alarm set!")
```

---

## 🛠️ Tech Stack

| Layer | Technology | Why We Use It |
|---|---|---|
| 🎙️ Wake Word | Vosk | 100% free · offline · detects "Hey Bro" in background |
| ⏺️ Audio Recording | MediaRecorder (Android) | Built-in Android class · records command after wake word |
| 🔊 Speech to Text | Whisper (OpenAI) | Open source · highly accurate · works with any accent |
| 🧠 AI Agent | Groq + Llama 3 | Free tier · fast · understands intent and structures note |
| 🔧 MCP Tools | Google Drive · Gmail · Calendar | Executes real actions on real apps |
| ⚙️ Backend | Python + FastAPI | Receives audio · runs AI pipeline · returns result |
| 📱 Android App | Kotlin + ForegroundService | Always-on background listening · no battery drain tricks |

---

## 🗣️ Planned Voice Commands

| 🎙️ You Say | ⚡ BroBot Does | 🔧 Tool Used |
|---|---|---|
| "Hey Bro, write a note about..." | Saves clean structured note | Google Drive MCP |
| "Hey Bro, play music" | Starts music playback | Android MediaSession |
| "Hey Bro, stop music" | Stops music playback | Android MediaSession |
| "Hey Bro, volume up 30%" | Increases volume by 30% | Android AudioManager |
| "Hey Bro, volume down 20%" | Decreases volume by 20% | Android AudioManager |
| "Hey Bro, email [person] about..." | Writes and sends email | Gmail MCP |
| "Hey Bro, set alarm for 8am" | Creates alarm | Android AlarmManager |

---

## 📋 Project Phases

```
Phase 1  ──────────────────────────────────────────  ✅ COMPLETE
         Wake word detection
         "Hey Bro" detected via Vosk (offline)
         Confirmed working on OnePlus CPH2491 · Android 16

Phase 2  ──────────────────────────────────────────  ✅ COMPLETE
         Audio recording
         MediaRecorder records 5 sec after wake word
         command.mp4 saved to phone storage
         Mic handoff: Vosk → MediaRecorder → Vosk

Phase 3  ──────────────────────────────────────────  🔨 IN PROGRESS
         Send audio to backend
         OkHttp HTTP POST → FastAPI /process-audio
         Receive JSON response

Phase 4  ──────────────────────────────────────────  ✅ COMPLETE
         Always-on background service
         ForegroundService keeps BroBot alive
         Works even when app is closed

Phase 5  ──────────────────────────────────────────  🎯 PLANNED
         Voice biometric security
         Only Nazeer's voice activates BroBot
         Deep learning speaker verification
         PyTorch → .tflite model on Android
```

---

## 📡 API Contract

> ⚠️ **This contract must NEVER change without both Nazeer and Ruchitha agreeing first.**

### Endpoint
```
POST /process-audio
```

### What Android Sends  →
```
Content-Type  :  multipart/form-data
Field name    :  audio
File format   :  .mp4
```

### What Backend Returns  ←

**Success:**
```json
{
  "status": "success",
  "message": "Note saved to Google Drive!",
  "note": "Structured note text here"
}
```

**Error:**
```json
{
  "status": "error",
  "message": "Failed to process audio",
  "note": ""
}
```

---

## 📁 Project Structure

```
BroBot/
│
├── 📱 app/src/main/
│   │
│   ├── java/com/nazeer/brobot/
│   │   ├── MainActivity.kt          →  Entry point · asks mic permission · starts service
│   │   ├── BroBotService.kt         →  ForegroundService · manages all components
│   │   ├── WakeWordDetector.kt      →  Vosk wake word · detects "Hey Bro"
│   │   ├── AudioRecorder.kt         →  MediaRecorder · records 5 sec command
│   │   └── ApiClient.kt             →  OkHttp · sends audio to backend (Phase 3)
│   │
│   ├── assets/
│   │   └── model/                   →  Vosk language model files
│   │
│   └── AndroidManifest.xml          →  Permissions · service registration
│
├── app/build.gradle.kts             →  Dependencies (Vosk · OkHttp · etc.)
└── README.md                        →  This file
```

---

## ⚙️ Setup Instructions

### 📱 Android App (Nazeer)
```bash
# 1. Clone the repo
git clone https://github.com/shaiknazeer14/BroBot.git

# 2. Switch to android branch
git checkout android

# 3. Open in Android Studio
# 4. Connect phone via USB
# 5. Click ▶ Run
```

### 🐍 Backend (G. Ruchitha)
```bash
# 1. Switch to backend branch
git checkout backend

# 2. Install dependencies
pip install fastapi uvicorn openai-whisper groq python-multipart

# 3. Run the server
uvicorn main:app --reload

# 4. Share URL using ngrok for Android testing
ngrok http 8000
```

---

## 🌿 Git Branch Strategy

```
main  ──────────────────────────────────  Final working code only
  │
  ├── android  ───────────────────────── Nazeer's work (Kotlin · Android)
  │
  └── backend  ───────────────────────── Ruchitha's work (Python · FastAPI)
```

> **Rule:** Never push directly to `main`. Merge only when both parts are tested and working together.

---

## 📞 Microphone Handoff Flow

```
App starts
    │
    ▼
🎙️ Vosk holds microphone
    │  (listening for "Hey Bro")
    │
    ▼  ── "Hey Bro" detected ──
    │
    ▼
🔄 Vosk releases microphone
    │
    ▼
⏺️ MediaRecorder takes microphone
    │  (recording for 5 seconds)
    │
    ▼
🔄 MediaRecorder releases microphone
    │
    ▼
🎙️ Vosk takes microphone back
    │  (listening for next "Hey Bro")
    ▼
  🔁 Loop forever
```

---

*🔥 Built from documentation, not guesswork*
*Docs → Understand → Code → Test → Ship*
