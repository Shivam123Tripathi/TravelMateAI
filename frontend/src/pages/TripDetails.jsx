import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { apiCall } from "../services/api";
import { toast } from "react-toastify";

export default function TripDetails() {

  const { id } = useParams();
  const [trip, setTrip] = useState(null);
  const [seats, setSeats] = useState(1)

  useEffect(() => {

    const fetchTrip = async () => {

      const res = await apiCall(`/trips/${id}`);

      if (res.status === "success") {
        setTrip(res.data);
      } else {
        toast.error("Trip not found");
      }

    };

    fetchTrip();

  }, [id]);

 const handleBooking = async () => {

    const res = await apiCall("/bookings", "POST", {
      tripId: Number(id),
      numberOfSeats: seats
    });

    if (res.status === "success") {
      toast.success("Trip booked successfully!")
    } else {
      toast.error(res.message || "Booking failed")
    }

  }

  if (!trip) {
    return <p className="text-center mt-20">Loading trip...</p>;
  }

  return (
    <div className="max-w-5xl mx-auto p-10">

      <img
        src={trip.imageUrl}
        className="w-full h-96 object-cover rounded-xl mb-6"
      />

      <h1 className="text-3xl font-bold mb-2">
        {trip.title}
      </h1>

      <p className="text-gray-500 mb-4">
        {trip.destination}
      </p>

      <p className="mb-6">
        {trip.description}
      </p>

      <p className="text-xl font-semibold mb-6">
        Price: ₹ {trip.price}
      </p>

      <div className="mb-6">

        <label className="font-semibold mr-3">
          Select Seats:
        </label>

        <input
          type="number"
          min="1"
          max="10"
          value={seats}
          onChange={(e) => setSeats(Number(e.target.value))}
          className="border p-2 w-20"
        />

      </div>

      <button
        onClick={handleBooking}
        className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700"
      >
        Book This Trip
      </button>

    </div>
  );
}