import os
import shutil
from fastapi import FastAPI, UploadFile, File
from app.config import TEMP_AUDIO_DIR
from app.transcription import transcribe_audio
from app.agent import structure_note
from app.drive import save_note_to_drive
from app.schemas import ProcessAudioResponse

app = FastAPI(title="BroBot Backend")
os.makedirs(TEMP_AUDIO_DIR, exist_ok=True)

@app.post("/process-audio", response_model=ProcessAudioResponse)
async def process_audio(audio: UploadFile = File(...)):
    # Save the uploaded audio file to a temporary location
    temp_file_path = os.path.join(TEMP_AUDIO_DIR, audio.filename)
    with open(temp_file_path, "wb") as f:
        shutil.copyfileobj(audio.file, f)

    raw_text = transcribe_audio(temp_file_path)
    structured_note = structure_note(raw_text)
    save_note_to_drive(structured_note, file_name="note.txt")

    try:
     os.remove(temp_file_path)  # Clean up the temporary file
    except PermissionError:
     pass  # File still in use, skip cleanup for now
    return ProcessAudioResponse(
        status="success",
        message="Note saved to Google Drive successfully.",
        note=structured_note
    )