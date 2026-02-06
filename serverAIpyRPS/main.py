from concurrent import futures
import grpc

from app.config import Settings
from app.embeddings import Embedder
from app.qdrant_repo import QdrantRepo
from app.grpc_service import EchoGrpcService
from generated import echo_pb2_grpc

def main():
    s = Settings()

    embedder = Embedder(s.model_name)
    repo = QdrantRepo(s.qdrant_path, s.collection, s.vector_size)

    service = EchoGrpcService(embedder, repo)

    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    echo_pb2_grpc.add_EchoServiceServicer_to_server(service, server)
    server.add_insecure_port(f"[::]:{s.grpc_port}")
    server.start()
    server.wait_for_termination()

if __name__ == "__main__":
    main()
