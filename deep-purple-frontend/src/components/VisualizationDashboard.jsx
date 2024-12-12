import React from "react";
import { BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid, Legend } from "recharts";

const VisualizationDashboard = ({ communicationsData }) => {
    if (!communicationsData) {
        return <p className="text-center text-red-500">No data available for visualization.</p>;
    }

    // Prepare data for combined visualization
    const emotionCounts = {};
    for (const [emotion, count] of Object.entries(communicationsData.primaryEmotionCounts)) {
        emotionCounts[emotion] = { primary: count, secondary: 0 };
    }
    for (const [emotion, count] of Object.entries(communicationsData.secondaryEmotionCounts)) {
        if (!emotionCounts[emotion]) {
            emotionCounts[emotion] = { primary: 0, secondary: count };
        } else {
            emotionCounts[emotion].secondary = count;
        }
    }
    const combinedData = Object.entries(emotionCounts).map(([emotion, counts]) => ({
        name: emotion,
        Primary: counts.primary,
        Secondary: counts.secondary,
    }));

    return (
        <div className="p-4">
            {/* Combined Emotions Bar Chart */}
            <div className="mb-8">
                <h3 className="text-xl font-semibold text-center">Emotion Distribution</h3>
                <BarChart width={600} height={400} data={combinedData} className="mx-auto">
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="name" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    <Bar dataKey="Primary" stackId="a" fill="hsl(var(--chart-1))" />
                    <Bar dataKey="Secondary" stackId="a" fill="hsl(var(--chart-2))" />
                </BarChart>
            </div>

            {/* Total Communications Count */}
            <div className="text-center text-lg font-medium">
                Total Communications: {communicationsData.total}
            </div>
        </div>
    );
};

export default VisualizationDashboard;
