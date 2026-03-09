import { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { toast } from "react-toastify"

const SavedTrips = () => {

  const [trips, setTrips] = useState([])
  const navigate = useNavigate()

  const loadTrips = () => {
    const savedTrips = JSON.parse(localStorage.getItem("trips")) || []
    setTrips(savedTrips)
  }

  // First Load
  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    loadTrips()
  }, [])

  // Reload when page comes into focus (important!)
  useEffect(() => {
    window.addEventListener("focus", loadTrips)
    return () => window.removeEventListener("focus", loadTrips)
  }, [])

  const deleteTrip = (id) => {
  const updatedTrips = trips.filter(trip => trip.id !== id)
  localStorage.setItem("trips", JSON.stringify(updatedTrips))
  setTrips(updatedTrips)

  toast.error("Trip Deleted 🗑️")
}

  return (
    <div className="min-h-screen py-20 px-6 bg-gradient-to-br from-sky-100 via-white to-blue-100">
      <div className="max-w-5xl mx-auto">

        <h1 className="text-4xl font-bold text-center mb-12 text-sky-600">
          💾 Saved Trips
        </h1>

        {trips.length === 0 ? (
          <div className="text-center text-gray-500 text-lg">
            No trip saved yet 😔
          </div>
        ) : (
          <div className="grid md:grid-cols-2 gap-6">
            {trips.map((trip) => (
              <div
                key={trip.id}
                className="bg-white p-6 rounded-2xl shadow-lg border hover:shadow-xl transition"
              >
                <h2 className="text-xl font-bold text-sky-600 mb-2">
                  🌍 {trip.destination}
                </h2>

                <p className="text-gray-600 mb-1">
                  <strong>Duration:</strong> {trip.days} Days
                </p>

                <p className="text-gray-600 mb-1">
                  <strong>Travel Type:</strong> {trip.travelType}
                </p>

                <p className="text-gray-600 mb-3">
                  <strong>Budget:</strong> ₹{trip.estimatedCost}
                </p>

                <div className="flex justify-between mt-4">
                  <button
                    onClick={() => navigate("/planner")}
                    className="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition"
                  >
                    View
                  </button>

                  <button
                    onClick={() => deleteTrip(trip.id)}
                    className="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition"
                  >
                    Delete
                  </button>
                </div>

              </div>
            ))}
          </div>
        )}

      </div>
    </div>
  )
}

export default SavedTrips