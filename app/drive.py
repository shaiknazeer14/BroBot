import os
from googleapiclient.http import MediaInMemoryUpload
from google.auth.transport.requests import Request
from google.oauth2.credentials import Credentials
from google_auth_oauthlib.flow import InstalledAppFlow
from googleapiclient.discovery import build
from googleapiclient.http import MediaFileUpload

SCOPES = ["https://www.googleapis.com/auth/drive.file"]
def get_drive_service():
    creds = None
    if os.path.exists("token.json"):
        creds = Credentials.from_authorized_user_file("token.json", SCOPES)
    if not creds or not creds.valid:
        if creds and creds.expired and creds.refresh_token:
            creds.refresh(Request())
        else:
            flow = InstalledAppFlow.from_client_secrets_file(
                "credentials.json", SCOPES
            )
            creds = flow.run_local_server(port=0)
        with open("token.json", "w") as token:
            token.write(creds.to_json())
    return build("drive", "v3", credentials=creds)
def save_note_to_drive(note_text: str, file_name: str):
    service = get_drive_service()
    file_metadata = {"name": file_name}
    media=MediaInMemoryUpload(note_text.encode("utf-8"), mimetype="text/plain")
    uploaded_file = service.files().create(
         body=file_metadata,
          media_body=media, 
          fields="id").execute()
    return uploaded_file.get("id")