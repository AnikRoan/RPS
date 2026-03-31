import torch
from sentence_transformers import SentenceTransformer


class Embedder:
    def __init__(self, model_name: str):
        self._model = SentenceTransformer(model_name)
        # warmup
        self._model.encode(["warmup"], show_progress_bar=False)

    def embed(self, text: str) -> list[float]:
        with torch.no_grad():
            return self._model.encode(
                text,
                normalize_embeddings=True
            ).tolist()

    def embed_batch(self, texts: list[str], batch_size: int = 32) -> list[list[float]]:
        vectors = self._model.encode(
            texts,
            batch_size=batch_size,
            normalize_embeddings=True,
            show_progress_bar=False
        )
        return vectors.tolist()
