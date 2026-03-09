import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiCall } from "../services/api";
import { toast } from "react-toastify";

export default function AddTrip() {

  const navigate = useNavigate();

  const [form, setForm] = useState({
    title: "",
    destination: "",
    description: "",
    startDate: "",
    endDate: "",
    totalSeats: "",
    price: "",
    imageUrl: ""
  });

  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // ✅ Basic validation
    if (
      !form.title ||
      !form.destination ||
      !form.description ||
      !form.startDate ||
      !form.endDate ||
      !form.totalSeats ||
      !form.price
    ) {
      toast.error("Please fill all required fields");
      return;
    }

    const tripData = {
      ...form,
      price: Number(form.price),
      totalSeats: Number(form.totalSeats)
    };

    try {
      setLoading(true);

      const res = await apiCall("/trips", "POST", tripData);

      if (res) {
        toast.success("Trip added successfully 🚀");

        // redirect to admin trips page
        navigate("/admin/trips");
      } else {
        toast.error("Failed to add trip");
      }

    } catch (error) {
      console.error(error);
      toast.error("Something went wrong");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-3xl mx-auto p-8">

      <h1 className="text-3xl font-bold mb-6">
        Add New Trip
      </h1>

      <form onSubmit={handleSubmit} className="space-y-4">

        <input
          name="title"
          placeholder="Trip Title"
          value={form.title}
          onChange={handleChange}
          className="w-full border p-2 rounded"
        />

        <input
          name="destination"
          placeholder="Destination"
          value={form.destination}
          onChange={handleChange}
          className="w-full border p-2 rounded"
        />

        <textarea
          name="description"
          placeholder="Trip Description"
          value={form.description}
          onChange={handleChange}
          className="w-full border p-2 rounded"
        />

        <div className="grid grid-cols-2 gap-4">

          <input
            type="date"
            name="startDate"
            value={form.startDate}
            onChange={handleChange}
            className="border p-2 rounded"
          />

          <input
            type="date"
            name="endDate"
            value={form.endDate}
            onChange={handleChange}
            className="border p-2 rounded"
          />

        </div>

        <input
          type="number"
          name="totalSeats"
          placeholder="Total Seats"
          value={form.totalSeats}
          onChange={handleChange}
          className="w-full border p-2 rounded"
        />

        <input
          type="number"
          name="price"
          placeholder="Price"
          value={form.price}
          onChange={handleChange}
          className="w-full border p-2 rounded"
        />

        <input
          name="imageUrl"
          placeholder="Image URL"
          value={form.imageUrl}
          onChange={handleChange}
          className="w-full border p-2 rounded"
        />

        <button
          type="submit"
          disabled={loading}
          className="bg-blue-500 text-white px-6 py-2 rounded"
        >
          {loading ? "Adding Trip..." : "Add Trip"}
        </button>

      </form>

    </div>
  );
}