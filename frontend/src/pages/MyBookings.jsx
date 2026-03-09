import { useEffect, useState } from "react"
import { apiCall } from "../services/api"
import { toast } from "react-toastify"

export default function MyBookings() {

  const [bookings, setBookings] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {

    const fetchBookings = async () => {

      const res = await apiCall("/bookings/my-bookings")

      if (res.status === "success") {
        setBookings(res.data)
      } else {
        toast.error("Failed to load bookings")
      }

      setLoading(false)

    }

    fetchBookings()

  }, [])


  if (loading) {
    return <p className="text-center mt-20">Loading bookings...</p>
  }

  const handleCancel = async (bookingId) => {

    const res = await apiCall(`/bookings/${bookingId}`, "DELETE");

    if (res.status === "success") {

      toast.success("Booking cancelled successfully");

      setBookings(bookings.filter(b => b.id !== bookingId));

    } else {
      toast.error(res.message || "Cancellation failed");
    }

  };

  return (

    <div className="min-h-screen bg-gray-50 pt-20 px-6">

      <h1 className="text-3xl font-bold mb-8">
        My Bookings
      </h1>

      {bookings.length === 0 ? (

        <p className="text-gray-500">
          You haven't booked any trips yet.
        </p>

      ) : (

        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">

          {bookings.map((b) => (

            <div
              key={b.id}
              className="bg-white p-6 rounded-xl shadow-lg"
            >

              <h2 className="text-xl font-semibold">
                {b.tripTitle}
              </h2>

              <p className="text-gray-500 mt-2">
                Seats: {b.numberOfSeats}
              </p>

              <p className="text-gray-700 font-semibold mt-2">
                Total: ₹{b.totalPrice}
              </p>

              <p className="text-green-600 text-sm">
                {b.status}
              </p>

              <button
                onClick={() => handleCancel(b.id)}
                className="mt-4 bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600"
              >
                Cancel Booking
              </button>

            </div>

          ))}

        </div>

      )}

    </div>

  )
}