from qdrant_client import QdrantClient
from qdrant_client.models import Distance, VectorParams, PointStruct
from qdrant_client import models


class QdrantRepo:
    def __init__(self, path: str, collection: str, vector_size: int):
        self.collection = collection
        self.client = QdrantClient(url=path)

        # create if not exist
        existing = {c.name for c in self.client.get_collections().collections}
        if collection not in existing:
            self.client.create_collection(
                collection_name=collection,
                vectors_config=VectorParams(
                    size=vector_size,
                    distance=Distance.COSINE
                ),
                hnsw_config=models.HnswConfigDiff(
                    m=32,
                    ef_construct=200
                )
            )

    def upsert_points(self, points):
        self.client.upsert(
            collection_name=self.collection,
            points=points
        )

    def search(self, vector: list[float], limit: int = 50):
        result = self.client.query_points(
            collection_name=self.collection,
            query=vector,
            limit=limit,
            with_payload=False,
            with_vectors=False,
            timeout=3
        )

        return [str(point.id) for point in result.points]
