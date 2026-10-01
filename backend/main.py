import os
import shutil
from fastapi import FastAPI, UploadFile, File, HTTPException
from pydantic import BaseModel
from typing import List

from pdf_processor import extract_text_from_pdf, split_text
from embeddings import create_vector_store
from rag import generate_answer

app = FastAPI()

# Global variable to store the path of the uploaded file
UPLOAD_DIR = "uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)

class QuestionRequest(BaseModel):
    question: str

class AskResponse(BaseModel):
    answer: str
    sources: List[str]

@app.get("/health")
async def health_check():
    return {"status": "ok"}

@app.post("/upload")
async def upload_file(file: UploadFile = File(...)):
    if not file.filename.endswith(".pdf"):
        raise HTTPException(status_code=400, detail="Only PDF files are supported")
    
    file_path = os.path.join(UPLOAD_DIR, "current_doc.pdf")
    with open(file_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)
    
    return {"message": f"Successfully uploaded {file.filename}"}

@app.post("/process")
async def process_doc():
    file_path = os.path.join(UPLOAD_DIR, "current_doc.pdf")
    if not os.path.exists(file_path):
        raise HTTPException(status_code=404, detail="No document uploaded")
    
    try:
        # 1. Extract
        text = extract_text_from_pdf(file_path)
        # 2. Split
        chunks = split_text(text)
        # 3. Embed and store
        create_vector_store(chunks)
        return {"message": "Document processed and indexed successfully"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/ask", response_model=AskResponse)
async def ask(request: QuestionRequest):
    try:
        answer, sources = generate_answer(request.question)
        return AskResponse(answer=answer, sources=sources)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
