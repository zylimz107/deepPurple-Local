import React, { useState, useEffect } from "react";
import ModelManagement from "@/components/ModelManagement";
import EmotionCategoryManager from "@/components/EmotionCategoryManager/EmotionCategoryManager";
import { getAllModels } from "@/api";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";

const EmotionCategoryPage = () => {
  const [models, setModels] = useState([]);
  const [selectedModel, setSelectedModel] = useState(null);

  // Fetch models from the backend
  const fetchModels = async () => {
    try {
      const response = await getAllModels();
      console.log("Fetched models:", response.data);
      setModels(response.data);
    } catch (error) {
      console.error("Error fetching models:", error);
    }
  };

  // Fetch models on initial render
  useEffect(() => {
    fetchModels();
  }, []);

  // Pass the fetchModels function to ModelManagement so it can re-fetch models after a new one is added
  return (
    <div className="container mx-auto mt-8 space-y-1">
      <h1 className="text-2xl font-bold text-center">Emotion Model Manager</h1>
      <div className="flex items-stretch">
            <ModelManagement refreshModels={fetchModels} />
          <div>
          <Card className="p-4 shadow-lg">
            <CardHeader>
              <CardTitle>Select a Model</CardTitle>
            </CardHeader>
            <CardContent>
              <Select
                value={selectedModel?.id || ""}
                onValueChange={(modelId) => {
                  const model = models.find((model) => model.id === modelId);
                  setSelectedModel(model);
                }}
              >
                <SelectTrigger className="w-[500px]">
                  <SelectValue placeholder="Choose a model" />
                </SelectTrigger>
                <SelectContent>
                  <SelectGroup>
                    <SelectItem value="placeholder" disabled>
                      Select a model
                    </SelectItem>
                    {models.length > 0 ? (
                      models.map((model) => (
                        <SelectItem key={model.id} value={model.id}>
                          {model.name}
                        </SelectItem>
                      ))
                    ) : (
                      <SelectItem value="loading" disabled>
                        Loading models...
                      </SelectItem>
                    )}
                  </SelectGroup>
                </SelectContent>
              </Select>
            </CardContent>
          </Card>

          {selectedModel && (
                <EmotionCategoryManager selectedModelId={selectedModel.id} />
          )}
          </div>
        </div>
        </div>
  );
};

export default EmotionCategoryPage;
