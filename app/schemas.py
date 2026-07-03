from pydantic import BaseModel
class ProcessAudioResponse(BaseModel):
    status: str
    message: str
    note: str