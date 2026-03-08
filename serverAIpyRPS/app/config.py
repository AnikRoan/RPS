from dataclasses import dataclass


@dataclass(frozen=True)
class Settings:

    # Qdrant
    qdrant_path: str = "http://localhost:6333"
    collection: str = "recipes"
    vector_size: int = 384

    # gRPC
    grpc_port: int = 50051

    # Embedding model
    model_name: str = "sentence-transformers/all-MiniLM-L6-v2"

    # Kafka
    kafka_bootstrap_servers: str = "localhost:9092"
    kafka_group_id: str = "recipe-vector-group"
    kafka_topic: str = "recipes"
    kafka_auto_offset_reset: str = "earliest"