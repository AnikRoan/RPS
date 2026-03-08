from sentence_transformers import SentenceTransformer

class Embedder:
    def __init__(self, model_name:str):
        self._model = SentenceTransformer(model_name)

    def embed(self,text:str)-> list[float]:
        return self._model.encode(text, normalize_embeddings=True).tolist()

    def embed_batch(self, texts: list[str]) -> list[list[float]]:
        vectors = self._model.encode(
            texts,
            normalize_embeddings=True
        )
        return vectors.tolist()