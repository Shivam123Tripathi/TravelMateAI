import { useEffect, useState } from "react";
import { apiCall } from "../services/api";
import { Link } from "react-router-dom";
import { toast } from "react-toastify";

export default function Trips() {

  const [trips, setTrips] = useState([]);
  const [search, setSearch] = useState("");

   useEffect(() => {

    const loadTrips = async () => {

      const endpoint = search
        ? `/trips/search?destination=${search}`
        : "/trips?page=0&size=10";

      const res = await apiCall(endpoint);

      if (res.status === "success") {
        setTrips(res.data.content || res.data);
      } else {
        toast.error("Failed to load trips");
      }

    };

    loadTrips();

  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleSearch = async (e) => {
    e.preventDefault();

    const endpoint = search
      ? `/trips/search?destination=${search}`
      : "/trips?page=0&size=10";

    const res = await apiCall(endpoint);

    if (res.status === "success") {
      setTrips(res.data.content || res.data);
    } else {
      toast.error("Search failed");
    }
  };
  
  return (
    <div className="p-10">

      <h1 className="text-3xl font-bold mb-6">
        Available Trips
      </h1>

       {/* Search Bar */}

      <form
        onSubmit={handleSearch}
        className="flex gap-4 mb-8"
      >

        <input
          type="text"
          placeholder="Search destination..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="border p-3 rounded-lg w-72"
        />

        <button
          type="submit"
          className="bg-blue-600 text-white px-5 py-3 rounded-lg hover:bg-blue-700"
        >
          Search
        </button>

      </form>

        {/* Trips Grid */}

      <div className="grid md:grid-cols-3 gap-8">

        {trips.map((trip) => (

          <div
            key={trip.id}
            className="bg-white rounded-xl shadow-md overflow-hidden"
          >

            <img
              src={trip.imageUrl}
              className="h-48 w-full object-cover"
            />

            <div className="p-4">

              <h2 className="text-xl font-bold">
                {trip.title}
              </h2>

              <p className="text-gray-500">
                {trip.destination}
              </p>

              <p className="font-semibold mt-2">
                ₹ {trip.price}
              </p>

              <Link
                to={`/trip/${trip.id}`}
                className="inline-block mt-3 bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600"
              >
                View Details
              </Link>

            </div>

          </div>

        ))}

      </div>

    </div>
  );
}