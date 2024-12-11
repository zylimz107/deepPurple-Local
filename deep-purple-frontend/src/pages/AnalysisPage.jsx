import React, { useState, useEffect, useCallback } from 'react';
import { Card } from "@/components/ui/card";
import CommunicationForm from "@/components/CommunicationForm/CommunicationForm";
import { Pie, PieChart, Tooltip } from "recharts"; // Import necessary parts of recharts

const AnalysisPage = () => {
  const [response, setResponse] = useState(null);
  const [allCommunications, setAllCommunications] = useState([]);
  const [deleteNotification, setDeleteNotification] = useState('');

  const clearNotification = useCallback(() => {
    setDeleteNotification('');
  }, []);

  const clearResponse = useCallback(() => {
    setResponse(null);
  }, []);

  useEffect(() => {
    if (deleteNotification) {
      const timer = setTimeout(clearNotification, 3000);
      return () => clearTimeout(timer);
    }
  }, [deleteNotification]);

  // Prepare pie chart data from response
  const pieChartData = response ? [
    {
      name: response.primaryEmotion.emotion,
      value: response.primaryEmotion.percentage,
      fill: "hsl(var(--chart-1))", // You can customize the color for primary emotion
    },
    ...response.secondaryEmotions.map((secEmotion, index) => {
      const fillColor = `hsl(var(--chart-${index + 2}))`;
      return {
        name: secEmotion.emotion,
        value: secEmotion.percentage,
        fill: fillColor, };})
  ] : [];

  return (
    <div className="p-5">
      <h1 className="text-3xl font-bold mb-4">Deep Purple Analysis</h1>
      <CommunicationForm
        setResponse={setResponse}
        setAllCommunications={setAllCommunications}
        setDeleteNotification={setDeleteNotification}
        clearNotification={clearNotification}
        clearResponse={clearResponse}
      />

      {response && (
        <Card className="mb-5">
          <h3 className="text-2xl font-semibold">Analysis Results:</h3>
          {response.error ? (
            <div className="text-red-500"><strong>Error:</strong> {response.error}</div>
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
              <div><strong>ID:</strong> {response.id || 'N/A'}</div>
              <div><strong>Content:</strong> {response.content || 'N/A'}</div>
              <div><strong>Primary Emotion:</strong>
                {response.primaryEmotion.emotion}
              </div>
              <div><strong>Secondary Emotions:</strong>
                {Array.isArray(response.secondaryEmotions) && response.secondaryEmotions.length > 0 ? (
                  <ul>
                    {response.secondaryEmotions.map((secEmotion, index) => (
                      <li key={index}>{secEmotion.emotion}</li>
                    ))}
                  </ul>
                ) : 'No secondary emotions available'}
              </div>
              <div><strong>Model:</strong> {response.modelName || 'N/A'}</div>
              <div><strong>Confidence Rating:</strong> {response.confidenceRating || 'N/A'}</div>
              <div><strong>Summary:</strong> {response.summary || 'N/A'}</div>
              <div><strong>Timestamp:</strong> {response.timestamp ? new Date(response.timestamp).toLocaleString() : 'N/A'}</div>
            </>
          )}
        </Card>
      )}

      {allCommunications.length > 0 && (
        <Card className="mb-5">
          <h2 className="text-2xl font-semibold">All Communications:</h2>
          <ul>
            {allCommunications.map((comm) => (
              <li key={comm.id} className="mb-5 p-3 border border-gray-300 rounded">
                <div><strong>ID:</strong> {comm.id}</div>
                <div><strong>Content:</strong> {comm.content}</div>
                <div><strong>Primary Emotion:</strong> {comm.primaryEmotion.emotion} ({comm.primaryEmotion.percentage}%)</div>
                <div><strong>Secondary Emotions:</strong>
                  {Array.isArray(comm.secondaryEmotions) && comm.secondaryEmotions.length > 0 ? (
                    <ul>
                      {comm.secondaryEmotions.map((secEmotion, index) => (
                        <li key={index}>{secEmotion.emotion} ({secEmotion.percentage}%)</li>
                      ))}
                    </ul>
                  ) : <p>No secondary emotions available</p>}
                </div>
                <div><strong>Confidence Rating:</strong> {comm.confidenceRating}</div>
                <div><strong>Summary:</strong> {comm.summary}</div>
                <div><strong>Timestamp:</strong> {new Date(comm.timestamp).toLocaleString()}</div>
              </li>
            ))}
          </ul>
        </Card>
      )}

      {deleteNotification && (
        <div className="text-green-500 mt-2">{deleteNotification}</div>
      )}
    </div>
  );
};

export default AnalysisPage;
