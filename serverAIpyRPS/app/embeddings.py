from sentence_transformers import SentenceTransformer

class Embedder:
    def __init__(self, model_name:str):
        self._model = SentenceTransformer(model_name)

    def embed(self,text:str)-> list[float]:
        return self._model.encode(text, normalize_embeddings=True).tolist()