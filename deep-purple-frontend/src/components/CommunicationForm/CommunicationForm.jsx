import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import {
    Select,
    SelectContent,
    SelectGroup,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";
import { Card, CardContent } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { Pie, PieChart, Tooltip } from "recharts";
import {
    Alert,
    AlertDescription,
} from "@/components/ui/alert";
import { getAllModels } from "@/api";

const CommunicationForm = ({ setResponse, setAllCommunications, setDeleteNotification, clearNotification, clearResponse }) => {
    const [content, setContent] = useState('');
    const [operation, setOperation] = useState('');
    const [id, setId] = useState('');
    const [modelName, setModelName] = useState('');
    const [file, setFile] = useState(null); // State for the uploaded file
    const [fetchedData, setFetchedData] = useState(null);
    const [models, setModels] = useState([]);

    const handleFileChange = (e) => {
        setFile(e.target.files[0]); // Set the file selected by the user
    };

    const fetchModels = async () => {
        try {
          const response = await getAllModels();
          console.log("Fetched models:", response.data);
          setModels(response.data);
        } catch (error) {
          console.error("Error fetching models:", error);
        }
      };
    
      useEffect(() => {
        fetchModels();
      }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        clearNotification();
        clearResponse();
        setFetchedData(null);
        setAllCommunications([]);
    
        try {
            let res;
            const dataToSend = { content, modelName };
    
            // If the operation is "upload", create FormData and append the file along with modelName and classificationType
            if (operation === 'upload') {
                const formData = new FormData();
                formData.append('file', file); // Append the file
                formData.append('modelName', modelName); // Append the model name
    
                res = await axios.post('http://deep107.ap-southeast-1.elasticbeanstalk.com/api/communications/upload', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                });
            } else if (operation === 'save') {
                res = await axios.post('http://deep107.ap-southeast-1.elasticbeanstalk.com/api/communications', dataToSend);
            } else if (operation === 'update') {
                res = await axios.put(`http://deep107.ap-southeast-1.elasticbeanstalk.com/api/communications/${id}`, dataToSend);
            } else if (operation === 'delete') {
                await axios.delete(`http://localhost:8080/api/communications/${id}`);
                setDeleteNotification(`Communication with ID ${id} has been deleted.`);
                setContent('');
                setId('');
                return;
            } else if (operation === 'get') {
                res = await axios.get(`http://deep107.ap-southeast-1.elasticbeanstalk.com/api/communications/${id}`);
                if (res.data) setFetchedData(res.data);
                return;
            }
    
            setResponse(res.data);
            if (operation === 'save' || operation === 'update' || operation === 'upload') {
                setContent('');
                setId('');
            }
        } catch (error) {
            setResponse({ error: error.message });
        }
    };

    const handleGetAll = async () => {
        clearNotification();
        clearResponse();
        try {
            const res = await axios.get('http://deep107.ap-southeast-1.elasticbeanstalk.com/api/communications');
            setAllCommunications(res.data);
        } catch (error) {
            setResponse({ error: error.message });
        }
    };

    const getDescription = (operation) => {
        switch (operation) {
          case "save":
            return "Analyze the content and save the analysis.";
          case "update":
            return "Update existing analysis data by ID.";
          case "delete":
            return "Delete analysis data by ID.";
          case "get":
            return "Get analysis data by its ID.";
          case "upload":
            return "Upload a file for analysis.";
          default:
            return "Please select an operation.";
        }
    };

    // Prepare pie chart data from response
    const pieChartData = fetchedData ? [
        {
            name: fetchedData.primaryEmotion.emotion,
            value: fetchedData.primaryEmotion.percentage,
            fill: "hsl(var(--chart-1))", // Customize the color for primary emotion
        },
        ...fetchedData.secondaryEmotions.map((secEmotion, index) => {
            const fillColor = `hsl(var(--chart-${index + 2}))`;
            return {
                name: secEmotion.emotion,
                value: secEmotion.percentage,
                fill: fillColor,
            };
        })
    ] : [];

    return (
        <Card className="p-4">
            <CardContent>
                <form onSubmit={handleSubmit} className="w-[1000px] space-y-4">
                    {operation !== 'get' && operation !== 'delete' && (
                        <div>
                            <Label htmlFor="content">Content</Label>
                            <Textarea
                                id="content"
                                value={content}
                                onChange={(e) => setContent(e.target.value)}
                                placeholder="Enter communication content"
                                required={operation === 'save' || operation === 'update'}
                                maxLength={1000}
                            />
                        </div>
                    )}

                    <div>
                        <Label htmlFor="operation">Operation</Label>
                        <Select
                            id="operation"
                            value={operation}
                            onValueChange={setOperation}
                        >
                            <SelectTrigger className="w-[500px]">
                                <SelectValue placeholder="select operation" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectGroup>
                                    <SelectItem value="save">Save</SelectItem>
                                    <SelectItem value="update">Update</SelectItem>
                                    <SelectItem value="delete">Delete</SelectItem>
                                    <SelectItem value="get">Get by ID</SelectItem>
                                    <SelectItem value="upload">Upload File</SelectItem> {/* New Upload option */}
                                </SelectGroup>
                            </SelectContent>
                        </Select>
                        <Separator className="w-[500px] my-2" />
                        <Alert className="w-[500px] border-purple-800">
                            <AlertDescription className="text-purple-800">
                                {getDescription(operation)}
                            </AlertDescription>
                        </Alert>
                    </div>

                    {/* File input field for upload */}
                    {operation === 'upload' && (
                        <div>
                            <Label htmlFor="file">Select File</Label>
                            <Input
                                type="file"
                                id="file"
                                onChange={handleFileChange}
                                required
                            />
                        </div>
                    )}

                    <div>
                        <Label htmlFor="modelName">Model Name</Label>
                        <Select
                        id="modelName"
                        value={modelName} // The current selected model
                       onValueChange={(value) => setModelName(value)} // Update modelName when a model is selected
                         >
                        <SelectTrigger className="w-[500px]">
                         <SelectValue placeholder="Select model" />
                            </SelectTrigger>
                            <SelectContent>
                              <SelectGroup>
                               {/* Placeholder option */}
                                <SelectItem value="placeholder" disabled>Select a model</SelectItem>
                                {models.length > 0 ? (
                                  models.map((model) => (
                                <SelectItem key={model.id} value={model.name}>
                              {model.name}
                            </SelectItem>
          ))
        ) : (
          <SelectItem value="loading" disabled>Loading models...</SelectItem> 
        )}
      </SelectGroup>
    </SelectContent>
  </Select>
                    </div>

                    {operation !== 'upload' && operation !== 'save' && (
                        <div>
                            <Label htmlFor="id">ID</Label>
                            <Input
                                id="id"
                                type="number"
                                value={id}
                                onChange={(e) => setId(e.target.value)}
                                placeholder="Enter communication ID"
                                required={operation !== 'delete'}
                                className="w-[500px]"
                            />
                        </div>
                    )}

                    <Separator className="w-[500px] my-4" />
                    <Button type="submit" className="w-[500px]">
                        Submit
                    </Button>
                </form>

                <Button onClick={handleGetAll} className="w-[500px] mt-4">
                    Get All Communications
                </Button>
                <Separator className="my-4" />

                {fetchedData && (
        <CardContent className="mb-5">
          <h3 className="text-2xl font-semibold">Analysis Results:</h3>
          {fetchedData.error ? (
            <div className="text-red-500"><strong>Error:</strong> {fetchedData.error}</div>
          ) : (
            <>
              {/* Emotion Pie Chart */}
              <div className="mb-4">
                <h4 className="font-medium text-purple-800">Emotion Distribution</h4>
                <PieChart width={300} height={300}>
                  <Pie
                    data={pieChartData}
                    dataKey="value"
                    nameKey="name"
                    innerRadius={60}
                    outerRadius={80}
                    label
                  />
                  <Tooltip />
                </PieChart>
              </div>

              {/* Other Operation Details */}
              <div><strong>ID:</strong> {fetchedData.id || 'N/A'}</div>
              <div><strong>Content:</strong> {fetchedData.content || 'N/A'}</div>
              <div><strong>Primary Emotion:</strong>
                {fetchedData.primaryEmotion.emotion}
              </div>
              <div><strong>Secondary Emotions:</strong>
                {Array.isArray(fetchedData.secondaryEmotions) && fetchedData.secondaryEmotions.length > 0 ? (
                  <ul>
                    {fetchedData.secondaryEmotions.map((secEmotion, index) => (
                      <li key={index}>{secEmotion.emotion}</li>
                    ))}
                  </ul>
                ) : 'No secondary emotions available'}
              </div>
              <div><strong>Model:</strong> {fetchedData.modelName || 'N/A'}</div>
              <div><strong>Confidence Rating:</strong> {fetchedData.confidenceRating || 'N/A'}</div>
              <div><strong>Summary:</strong> {fetchedData.summary || 'N/A'}</div>
              <div><strong>Timestamp:</strong> {fetchedData.timestamp ? new Date(fetchedData.timestamp).toLocaleString() : 'N/A'}</div>
            </>
          )}
        </CardContent>
      )}
            </CardContent>
        </Card>
    );
};

export default CommunicationForm;
