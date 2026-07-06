from google import genai
from google.genai import types
from app.config import GEMINI_API_KEY, GEMINI_MODEL_NAME

client = genai.Client(api_key=GEMINI_API_KEY)

SYSTEM_PROMPT = """You are a BroBot, a voice note assistant.
The user juste spoke a raw,possibly messy voice transcript.

Your job:
1.Understand the user's intent(reminder,note,task,idea,etc)
2.Fix grammar and filler words from speech-to-text
3.Structure it into a short,clean note.

Title:
Reminder:
Tasks:

Rules:
-If there is no reminder,write the "Remainder:None".
-If there are no tasks,write "Tasks:None".
-return only the structured note.
-Do not add greetings or explanations.

Respond only with the final structured note text.No preamble, no explanations."""
def structure_note(raw_text:str)->str:
    response=client.models.generate_content(
        model=GEMINI_MODEL_NAME,
        contents=raw_text,
        config=types.GenerateContentConfig(
        system_instruction=SYSTEM_PROMPT,
        temperature=0.3,
        max_output_tokens=1024
        )
    )
    return response.text.strip()

if __name__ == "__main__":
    print("Agent started")

    sample_text = sample_text = """
Hey bro tomorrow remind me to finish my BroBot backend.
Also complete the FastAPI endpoint and push the code to GitHub.
"""

    print("Calling Gemini...")

    result = structure_note(sample_text)

    print(result)

    print("Finished")