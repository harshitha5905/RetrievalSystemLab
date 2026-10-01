import os
from openai import OpenAI
from dotenv import load_dotenv
from embeddings import search_similar_chunks

load_dotenv()
# Make sure you have OPENAI_API_KEY in your .env file
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

def generate_answer(question: str):
    """Generates an answer using the LLM based on retrieved context."""
    # 1. Search for relevant chunks
    context_chunks = search_similar_chunks(question)
    context_text = "\n\n".join(context_chunks)
    
    # 2. Build the prompt
    prompt = f"""You are a document assistant. Answer the user's question using only the provided document context. 
If the answer cannot be found in the context, clearly say that the information is not available in the document. 
Do not invent information.

Document Context:
{context_text}

Question:
{question}"""

    # 3. Call OpenAI LLM
    response = client.chat.completions.create(
        model="gpt-3.5-turbo",
        messages=[
            {"role": "system", "content": "You are a helpful assistant."},
            {"role": "user", "content": prompt}
        ],
        temperature=0
    )
    
    answer = response.choices[0].message.content
    return answer, context_chunks
