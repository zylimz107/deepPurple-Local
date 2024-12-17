import React, { useState } from "react";
import VisualizationDashboard from "@/components/VisualizationDashboard";
import axios from "axios";
import { Button } from "@/components/ui/button";

const UserDashboard = () => {
    const [communicationsData, setCommunicationsData] = useState(null);

    const handleGetAll = async () => {
        try {
            const res = await axios.get("http://localhost:8080/communications");
            const data = res.data;

            // Process data here (as shown earlier)
            const primaryEmotionCounts = {};
            const secondaryEmotionCounts = {};

            data.forEach(communication => {
                const primaryEmotion = communication.primaryEmotion?.emotion || "Unknown";
                primaryEmotionCounts[primaryEmotion] = (primaryEmotionCounts[primaryEmotion] || 0) + 1;

                communication.secondaryEmotions?.forEach(secEmotion => {
                    const emotion = secEmotion.emotion || "Unknown";
                    secondaryEmotionCounts[emotion] = (secondaryEmotionCounts[emotion] || 0) + 1;
                });
            });

            setCommunicationsData({
                total: data.length,
                primaryEmotionCounts,
                secondaryEmotionCounts,
            });
        } catch (error) {
            console.error(error);
        }
    };

    return (
        <div>
            <Button variant="outline" className="border-purple-800 text-purple-800" onClick={handleGetAll} >Fetch Communications</Button>
            <VisualizationDashboard communicationsData={communicationsData} />
        </div>
    );
};

export default UserDashboard;
