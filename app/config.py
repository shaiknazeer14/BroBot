import os 
from dotenv import load_dotenv

load_dotenv()
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")

WHISPER_MODEL_NAME="base"
GEMINI_MODEL_NAME="gemini-2.5-flash"
TEMP_AUDIO_DIR="temp_audio"