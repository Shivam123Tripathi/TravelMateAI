import { useEffect, useState } from "react";
import { apiCall } from "../services/api";

export default function Recommendations() {

  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  
  const fetchRecommendations = async () => {
  try {
    setLoading(true);

    const res = await apiCall("/recommendations");

    setRecommendations(res?.data || []);

  } catch (error) {
    console.error("Recommendation service failed", error);
    setError(true);
  } finally {
    setLoading(false);
  }
};

useEffect(() => {
  fetchRecommendations();
}, []);

if (loading) {
    return (
      <div className="flex justify-center mt-20">
        <div className="animate-spin h-10 w-10 border-4 border-blue-500 rounded-full"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center mt-20 text-red-500">
        Recommendation service unavailable
      </div>
    );
  }

  if (!recommendations || recommendations.length === 0) {
    return (
      <div className="text-center mt-20 text-gray-500">
        No recommendations available right now.
      </div>
    );
  }

  return(

    <div className="max-w-6xl mx-auto p-10">

      <h1 className="text-3xl font-bold mb-6">
        AI Recommended Trips
      </h1>

      <div className="grid md:grid-cols-3 gap-6">

        {recommendations.map(trip=>(
          <div key={trip.id} className="border p-4 rounded-lg">

            <img
            src={trip.imageUrl}
            alt={trip.title}
            className="h-48 w-full object-cover rounded"
            />

            <h2 className="text-xl font-semibold mt-3">
              {trip.title}
            </h2>

            <p>{trip.destination}</p>

            <p className="font-semibold">
              ₹{trip.price}
            </p>

          </div>
        ))}

      </div>

    </div>

  )

}