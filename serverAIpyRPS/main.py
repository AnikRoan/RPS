import sys
import os
import torch
import grpc
import threading

from concurrent import futures
from app.config import Settings
from app.embeddings import Embedder
from app.qdrant_repo import QdrantRepo
from app.recipe_search_service import RecipeSearchService
from app.kafka_consumer import start_consumer
from app.recipe_processor import process_recipe_batch
from generated import recipe_search_pb2_grpc

sys.path.append(os.path.join(os.path.dirname(__file__), "generated"))
torch.set_num_threads(1)


def main():
    s = Settings()

    embedder = Embedder(s.model_name)
    repo = QdrantRepo(s.qdrant_path, s.collection, s.vector_size)

    service = RecipeSearchService(embedder, repo)

    server = grpc.server(futures.ThreadPoolExecutor(max_workers=4))

    recipe_search_pb2_grpc.add_RecipeSearchServiceServicer_to_server(
        service, server
    )

    server.add_insecure_port("0.0.0.0:50051")

    server.start()

    kafka_thread = threading.Thread(
        target=start_consumer,
        args=(lambda data: process_recipe_batch(data, embedder, repo),),
        daemon=True
    )
    kafka_thread.start()

    print("gRPC server started")
    print("Kafka consumer started")

    server.wait_for_termination()


if __name__ == "__main__":
    main()
