import { useState } from "react"
import { toast } from "react-toastify"

const Planner = () => {

  const [formData, setFormData] = useState({
    destination: "",
    budget: "",
    days: "",
    interests: "",
    travelType: "Solo"
  })

  const [loading, setLoading] = useState(false)
  const [trip, setTrip] = useState(null)
  const [darkMode, setDarkMode] = useState(false)

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    })
  }

  const handleSubmit = (e) => {
    e.preventDefault()

    setLoading(true)
    setTrip(null)

    const days = parseInt(formData.days)
    const generatedItinerary = []

    for (let i = 1; i <= days; i++) {
      generatedItinerary.push({
        day: i,
        activity: `Exciting activities planned for Day ${i} in ${formData.destination} based on your interest in ${formData.interests || "exploration"}`
      })
    }

    const dummyTrip = {
      id: Date.now(),
      destination: formData.destination,
      days: formData.days,
      travelType: formData.travelType,
      interests: formData.interests,
      itinerary: generatedItinerary,
      estimatedCost: formData.budget,
      createdAt: new Date().toLocaleDateString()
    }

    setTimeout(() => {
      setTrip(dummyTrip)
      setLoading(false)
    }, 1500)
  }

  const saveTrip = () => {
    if (!trip) return

    const savedTrips = JSON.parse(localStorage.getItem("trips")) || []
  localStorage.setItem("trips", JSON.stringify([...savedTrips, trip]))

  toast.success("Trip Saved Successfully! ✈️")
  }

  return (
    <div className={`min-h-screen py-20 px-6 transition-all duration-500
      ${darkMode 
        ? "bg-gray-900 text-white" 
        : "bg-gradient-to-br from-sky-100 via-white to-blue-100"
      }`}
    >
      <div className="max-w-4xl mx-auto">

         {/* Dark Mode Toggle */}
        <div className="flex justify-end mb-6">
          <button
            onClick={() => setDarkMode(!darkMode)}
            className="px-4 py-2 rounded-lg text-sm font-semibold bg-gray-200 hover:bg-gray-300 transition"
          >
            {darkMode ? "☀ Light Mode" : "🌙 Dark Mode"}
          </button>
        </div>

        <h1 className="text-4xl font-bold text-center bg-gradient-to-r from-sky-600 to-blue-600 bg-clip-text text-transparent mb-12">
          ✈️ AI Trip Planner
        </h1>

        {/* FORM */}
        <form 
          onSubmit={handleSubmit}
          className="backdrop-blur-lg bg-white/70 border border-white/40 p-8 rounded-3xl shadow-xl space-y-6"
        >
          {/* Destination */}
          <input
            type="text"
            name="destination"
            placeholder="Destination"
            value={formData.destination}
            onChange={handleChange}
            className="w-full border p-3 rounded-xl focus:ring-2 focus:ring-sky-400 outline-none"
            required
          />

          {/* Travel Type Dropdown */}
          <select
            name="travelType"
            value={formData.travelType}
            onChange={handleChange}
            className="w-full border p-3 rounded-xl focus:ring-2 focus:ring-sky-400 outline-none"
          >
            <option value="Solo">Solo Trip</option>
            <option value="Family">Family Trip</option>
            <option value="Friends">Friends Trip</option>
            <option value="Couple">Couple Trip</option>
          </select>

          {/* Budget */}
          <input
            type="number"
            name="budget"
            placeholder="Budget (₹)"
            value={formData.budget}
            onChange={handleChange}
            className="w-full border p-3 rounded-xl focus:ring-2 focus:ring-sky-400 outline-none"
            required
          />

          {/* Days */}
          <input
            type="number"
            name="days"
            placeholder="Number of Days"
            value={formData.days}
            onChange={handleChange}
            className="w-full border p-3 rounded-xl focus:ring-2 focus:ring-sky-400 outline-none"
            required
          />

          {/* Interests */}
          <input
            type="text"
            name="interests"
            placeholder="Interests (adventure, food, photography...)"
            value={formData.interests}
            onChange={handleChange}
            className="w-full border p-3 rounded-xl focus:ring-2 focus:ring-sky-400 outline-none"
          />
 
          {/* Submit button */ }
          <button
            type="submit"
            disabled={loading}
            className={`w-full py-3 rounded-xl font-semibold transition-all duration-300 
            ${loading 
              ? "bg-gray-400 cursor-not-allowed" 
              : "bg-gradient-to-r from-sky-500 to-blue-600 text-white hover:scale-105 hover:shadow-lg"
            }`}
          >
            {loading ? "Generating AI Plan..." : "Generate Trip Plan"}
          </button>

        </form>

        {loading && (
          <div className="mt-8 text-center text-sky-600 font-semibold animate-pulse">
            🤖 AI is creating your personalized travel plan...
          </div>
        )}

        {/* RESULT SECTION */}
        {trip && (
          <div className="mt-12 backdrop-blur-lg bg-white/80 border border-white/40 p-8 rounded-3xl shadow-xl transition-all duration-500">

            <h2 className="text-2xl font-bold text-sky-600 mb-4">
              🌍 Your Trip to {trip.destination}
            </h2>

             <div className="text-gray-600 mb-6 space-y-1">
              <p><strong>Duration:</strong> {trip.days} Days</p>
              <p><strong>Travel Type:</strong> {trip.travelType}</p>
              <p><strong>Interests:</strong> {trip.interests || "General Exploration"}</p>
              <p><strong>Estimated Budget:</strong> ₹{trip.estimatedCost}</p>
              <p><strong>Created On:</strong> {trip.createdAt}</p>
            </div>

            <div className="space-y-4">
              {trip.itinerary.map((item, index) => (
                <div 
                  key={index}
                  className="p-4 rounded-xl bg-gradient-to-r from-sky-50 to-blue-50 border hover:shadow-md transition"
                >
                  <h3 className="font-semibold text-sky-700">
                    Day {item.day}
                  </h3>
                  <p className="text-gray-600">
                    {item.activity}
                  </p>
                </div>
              ))}
            </div>

            <p className="mt-6 font-semibold text-gray-800">
              Estimated Budget: ₹{trip.estimatedCost}
            </p>

            <button
              onClick={saveTrip}
              className="mt-6 bg-green-600 text-white px-6 py-2 rounded-xl hover:bg-green-700 hover:scale-105 transition-all duration-300"
            >
              Save Trip
            </button>

          </div>
        )}

      </div>
    </div>
  )
}

export default Planner