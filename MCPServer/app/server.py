from fastmcp import FastMCP
from app.models import RecipeRequest
import logging

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)s %(name)s - %(message)s",
)

logger = logging.getLogger("mcp")

mcp = FastMCP("RPS SERVER MCP")

@mcp.tool
def create_recipe(recipe: RecipeRequest) -> str:
    logger.info("create_recipe called: %s", recipe.model_dump())
    return "ok"
