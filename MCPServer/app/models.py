from pydantic import BaseModel,Field
from typing import List, Optional


class Ingredient(BaseModel):
    name: str = Field(..., description="Ingredient name")
    amount: float = Field(..., description="Quantity number, e.g. 2.5")
    unit: str = Field(..., description="Unit, e.g. g, ml, pcs")
    note: Optional[str] = Field("", description="Optional note")
    position: int = Field(0, description="Order in the list, starting from 1")

class RecipeRequest(BaseModel):
    name: str = Field(..., description="Recipe title")
    description: str = Field(..., description="Short description")
    ingredients: List[Ingredient] = Field(..., description="List of ingredients")
    time_to_cook_minutes: int = Field(..., description="Cooking time in minutes")
    creator: str = Field(..., description="Author name")





