from qdrant_client.models import PointStruct


def process_recipe_batch(recipes, embedder, qdrant):
    if not recipes:
        return

    texts = []

    for recipe in recipes:
        ingredients = " ".join(
            i["name"] for i in recipe.get("ingredientResponseList", [])
        )

        text = f"""
               Recipe: {recipe['name']}
               Description: {recipe['description']}
               Ingredients: {ingredients}
               """

        texts.append(text)

    vectors = embedder.embed_batch(texts, batch_size=16)

    points = []

    for recipe, vector in zip(recipes, vectors):
        points.append(
            PointStruct(
                id=recipe["uuid"],
                vector=vector,
                payload=recipe
            )
        )

    qdrant.upsert_points(points)
