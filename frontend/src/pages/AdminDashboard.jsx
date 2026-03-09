import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiCall } from "../services/api";
import { toast } from "react-toastify";

export default function AdminDashboard() {

  const navigate = useNavigate();

  const [stats, setStats] = useState({
    totalTrips: 0,
    totalBookings: 0,
    totalUsers: 0,
    revenue: 0,
  });

const [recentBookings, setRecentBookings] = useState([]);

  useEffect(() => {

    const fetchDashboard = async () => {

      const res = await apiCall("/reports/dashboard");

      if (res.status === "success") {

      setStats({
        totalBookings: res.data.bookings?.totalBookings || 0,
        revenue: res.data.revenue?.totalRevenue || 0
      });

        setRecentBookings(res.data.recentBookings || []);

      } else {
        toast.error("Failed to load dashboard data");
      }

    };

    fetchDashboard();

  }, []);
  return (
    <div className="min-h-screen bg-gray-100 p-8">
      <h1 className="text-3xl font-bold mb-8 text-gray-800">
        📊 Admin Dashboard
      </h1>

      {/* Stats Section */}
      <div className="grid md:grid-cols-4 gap-6 mb-10">
        <div className="bg-white p-6 rounded-xl shadow-md">
          <p className="text-gray-500 text-sm">Total Trips</p>
          <h2 className="text-2xl font-bold text-blue-600">
            {stats.totalTrips}
          </h2>
        </div>

        <div className="bg-white p-6 rounded-xl shadow-md">
          <p className="text-gray-500 text-sm">Total Bookings</p>
          <h2 className="text-2xl font-bold text-green-600">
            {stats.totalBookings}
          </h2>
        </div>

        <div className="bg-white p-6 rounded-xl shadow-md">
          <p className="text-gray-500 text-sm">Total Users</p>
          <h2 className="text-2xl font-bold text-purple-600">
            {stats.totalUsers}
          </h2>
        </div>

        <div className="bg-white p-6 rounded-xl shadow-md">
          <p className="text-gray-500 text-sm">Total Revenue</p>
          <h2 className="text-2xl font-bold text-red-500">
            ₹{stats.revenue}
          </h2>
        </div>
      </div>

      {/* Manage Trips Section */}
      <div className="bg-white p-6 rounded-xl shadow-md mb-10">
        <h2 className="text-xl font-semibold mb-4 text-gray-700">
          🚍 Manage Trips
        </h2>

        <div className="flex gap-4">

          <button 
            onClick={() => navigate("/admin/add-trip")}
            className="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition">
              Add New Trip
          </button>

          <button
            onClick={() => navigate("/admin/trips")}
            className="bg-yellow-500 text-white px-4 py-2 rounded-lg hover:bg-yellow-600 transition">
              Edit Trips
          </button>

          <button 
            onClick={() => navigate("/admin/trips")}
            className="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition">
              Delete Trips
          </button>
        </div>
      </div>

      {/* Recent Bookings Table */}
      <div className="bg-white p-6 rounded-xl shadow-md">
        <h2 className="text-xl font-semibold mb-4 text-gray-700">
          📖 Recent Bookings
        </h2>

        <table className="w-full text-left">
          <thead>
            <tr className="border-b">
              <th className="py-2">User</th>
              <th className="py-2">Trip</th>
              <th className="py-2">Amount</th>
            </tr>
          </thead>

          <tbody>
            {recentBookings.map((booking) => (
              <tr key={booking.id} className="border-b hover:bg-gray-50">

                <td className="py-3">
                  {booking.userName}
                </td>

                <td className="py-3">
                  {booking.tripTitle}
                </td>

                <td className="py-3 font-semibold text-green-600">
                  ₹{booking.amount}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}