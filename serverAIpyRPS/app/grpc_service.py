from generated import echo_pb2, echo_pb2_grpc

class EchoGrpcService(echo_pb2_grpc.EchoServiceServicer):
    def __init__(self, embedder, repo):
        self.embedder = embedder
        self.repo = repo
        self._id = 1

    def Echo(self, request, context):
        text = request.text

        vector = self.embedder.embed(text)
        point_id = self._id
        self._id += 1

        self.repo.upsert_text(point_id, vector, text)

        return echo_pb2.EchoReply(text=f"Saved id={point_id}: Text: {request.text}")

    def Search(self,request, context):
        text = request.text

        vector = self.embedder.embed(text)
        return self.repo.search(vector=vector)
