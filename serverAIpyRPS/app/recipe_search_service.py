import grpc

from generated import recipe_search_pb2
from generated import recipe_search_pb2_grpc


class RecipeSearchService(recipe_search_pb2_grpc.RecipeSearchServiceServicer):

    def __init__(self, embedder, repo):
        print("init recipe search")
        self.embedder = embedder
        self.repo = repo

    def SearchRecipes(self, request, context):
        try:
            print("STEP 1 request:", request.query)

            vector = self.embedder.embed(request.query)
            print("STEP 2 vector created")

            ids = self.repo.search(vector)
            print("STEP 3 search finished")

            return recipe_search_pb2.SearchResponse(
                recipe_ids=[str(i) for i in ids]
            )

        except Exception as e:
            import traceback
            traceback.print_exc()
            context.set_details(str(e))
            context.set_code(grpc.StatusCode.INTERNAL)
            raise
