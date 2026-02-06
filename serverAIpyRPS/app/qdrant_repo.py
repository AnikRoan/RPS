from qdrant_client import QdrantClient
from qdrant_client.models import Distance, VectorParams, PointStruct


class QdrantRepo:
    def __init__(self, path:str, collection:str,vector_size:int):
        self.collection = collection
        self.client = QdrantClient(url=path)

        #create if not exist
        existing = {c.name for c in self.client.get_collections().collections}
        if collection not in existing:
            self.client.create_collection(
                collection_name=collection,
                vectors_config=VectorParams(size=vector_size,distance=Distance.COSINE),
            )

    def upsert_text(self,point_id:int, vector: list[float],text:str):
        self.client.upsert(
            collection_name=self.collection,
            points=[
                PointStruct(
                    id=point_id,
                    vector=vector,
                    payload={"text":text},
                )
            ],
        )


    def search(self,vector:list[float], limit:int = 5):
        hits = self.client.query_points(
            collection_name=self.collection,
            query_vector=vector,
            limit=limit,
            with_payload=True,
            with_vectors=False,
        )
        return [
            {"id": h.id, "score": float(h.score),"payload": h.payload}
            for h in hits.points
        ]
        