import whisper
from app.config import WHISPER_MODEL_NAME
model=whisper.load_model(WHISPER_MODEL_NAME)
def transcribe_audio(file_path: str)->str:
     result=model.transcribe(
          file_path,
          language="en",
          task="transcribe",
          fp16=False
     )
     return result["text"].strip()