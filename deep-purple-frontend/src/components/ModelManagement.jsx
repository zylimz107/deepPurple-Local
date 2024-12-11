import React, { useState, useEffect } from "react";
import { getAllModels, createModel, deleteModel } from "@/api";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";

const ModelManagement = ({ refreshModels }) => {
  const [models, setModels] = useState([]); // Manage models in this component
  const [newModelName, setNewModelName] = useState("");

  // Fetch models from the backend
  const fetchModels = async () => {
    try {
      const response = await getAllModels();
      setModels(response.data); // Update the models state
    } catch (error) {
      console.error("Error fetching models:", error);
      alert("Failed to fetch models.");
    }
  };

  // Handle model creation
  const handleAddModel = async () => {
    if (!newModelName.trim()) {
      alert("Model name cannot be empty!");
      return;
    }
    try {
      await createModel(newModelName);
      setNewModelName("");
      fetchModels(); // Refresh the list of models after adding a new model
      refreshModels(); // Optionally call the parent function to update any parent state
    } catch (error) {
      console.error("Error creating model:", error);
      alert(error.response?.data?.message || "Failed to create model");
    }
  };

  // Handle model deletion
  const handleDeleteModel = async (id) => {
    if (!window.confirm("Are you sure you want to delete this model?")) return;
    try {
      await deleteModel(id);
      fetchModels(); // Refresh the list of models after deletion
      refreshModels(); // Optionally call the parent function to update any parent state
    } catch (error) {
      console.error("Error deleting model:", error);
      alert("Failed to delete model");
    }
  };

  // Fetch models on component mount
  useEffect(() => {
    fetchModels();
  }, []);

  return (
    <div className="w-[500px]">
      <Card className="p-4 shadow-md">
        <CardHeader>
          <CardTitle>Add a New Model</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex items-center gap-2">
            <Input
              type="text"
              placeholder="Enter model name"
              value={newModelName}
              onChange={(e) => setNewModelName(e.target.value)}
              className="w-full"
            />
            <Button onClick={handleAddModel} variant="default">
              Add
            </Button>
          </div>
          <Separator className="w-[420px] my-2" />
          <div>          
            {models.length > 0 ? (
            <ul className="space-y-3">
              {models.map((model) => (
                <li
                  key={model.id}
                  className="flex justify-between items-center border p-2 rounded shadow-sm"
                >
                  <strong className="text-lg">{model.name}</strong>
                  <Button
                    onClick={() => handleDeleteModel(model.id)}
                    variant="destructive"
                  >
                    Delete
                  </Button>
                </li>
              ))}
            </ul>
          ) : (
            <p className="text-gray-500">No models available.</p>
          )}</div>
        </CardContent>
      </Card>
    </div>
  );
};

export default ModelManagement;
