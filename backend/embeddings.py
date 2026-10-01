import os
from langchain_openai import OpenAIEmbeddings
from langchain_community.vectorstores import FAISS
from dotenv import load_dotenv

load_dotenv()

def create_vector_store(chunks):
    """Creates a FAISS vector store from text chunks using OpenAI embeddings."""
    embeddings = OpenAIEmbeddings()
    vector_store = FAISS.from_texts(chunks, embeddings)
    # Save the index locally
    vector_store.save_local("faiss_index")
    return vector_store

def load_vector_store():
    """Loads the FAISS vector store from local storage."""
    embeddings = OpenAIEmbeddings()
    return FAISS.load_local("faiss_index", embeddings, allow_dangerous_deserialization=True)

def search_similar_chunks(query, k=4):
    """Searches for the most similar chunks to the query."""
    vector_store = load_vector_store()
    docs = vector_store.similarity_search(query, k=k)
    return [doc.page_content for doc in docs]
