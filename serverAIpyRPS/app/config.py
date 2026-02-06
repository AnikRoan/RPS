from dataclasses import dataclass

@dataclass(frozen=True)
class Settings:
    qdrant_path:str = "http://localhost:6333"
    collection: str = "recipes"
    vector_size: int = 384
    grpc_port: int = 50051
    model_name: str = "sentence-transformers/all-MiniLM-L6-v2"