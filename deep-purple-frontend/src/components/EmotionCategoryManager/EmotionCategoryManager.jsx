import React, { useState, useEffect } from "react";
import { createCategory, getCategoriesByModel } from "@/api";
import { Input } from "@/components/ui/input"; // Replace with the correct shadcn Input component import
import { Button } from "@/components/ui/button"; // Replace with the correct shadcn Button component import
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"; // Replace with the correct Card imports


const EmotionCategoryManager = ({ selectedModelId }) => {
  const [categories, setCategories] = useState([]);
  const [newCategoryName, setNewCategoryName] = useState("");

  const fetchCategories = async () => {
    try {
      const response = await getCategoriesByModel(selectedModelId);
      console.log("Fetched categories:", response.data);
      setCategories(response.data);
    } catch (error) {
      console.error("Error fetching categories:", error);
    }
  };

  const handleAddCategory = async () => {
    if (!newCategoryName.trim()) {
      alert("Category name cannot be empty!");
      return;
    }

    try {
      await createCategory(selectedModelId, newCategoryName);
      setNewCategoryName("");
      fetchCategories();
    } catch (error) {
      console.error("Error adding category:", error);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, [selectedModelId]);

  return (
    <Card className="p-4 shadow-lg">
      <CardHeader>
        <CardTitle className="text-xl font-semibold">Manage Emotion Categories</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="flex items-center gap-2 mb-4">
          <Input
            type="text"
            placeholder="Enter category name"
            value={newCategoryName}
            onChange={(e) => setNewCategoryName(e.target.value)}
            className="w-full"
          />
          <Button onClick={handleAddCategory} variant="default">
            Add
          </Button>
        </div>
        <ul className="bg-gray-50 p-2 rounded divide-y divide-gray-200">
          {categories.map((category) => (
            <li key={category.id} className="py-2 px-3">
              {category.emotion}
            </li>
          ))}
        </ul>
      </CardContent>
    </Card>
  );
};

export default EmotionCategoryManager;
