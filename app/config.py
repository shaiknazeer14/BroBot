import os 
from dotenv import load_dotenv

load_dotenv()
ANTHROPIC_API_KEY = os.getenv("ANTHROPIC_API_KEY")
WHISPER_MODEL_NAME="base"
CLAUDE_MODEL_NAME="claude-sonnet-4-6"
TEMP_AUDIO_DIR="temp_audio"