from generated import recipe_search_pb2
from generated import recipe_search_pb2_grpc


class RecipeSearchService(recipe_search_pb2_grpc.RecipeSearchServiceServicer):

    def __init__(self, embedder, repo):
        self.embedder = embedder
        self.repo = repo

    def SearchRecipes(self, request, context):

        vector = self.embedder.embed(request.query)

        ids = self.repo.search(vector, request.limit)

        return recipe_search_pb2.SearchResponse(
            recipe_ids=ids
        )