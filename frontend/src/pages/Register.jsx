import { apiCall } from "../services/api";
import { toast } from "react-toastify";
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";


export default function Register() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
    phone: "",
  });

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };
const handleSubmit = async (e) => {
  e.preventDefault();

  const response = await apiCall("/users/register", "POST", form);

  if (response.status === "success") {
    toast.success("Account created successfully 🎉");
    navigate("/login");
  } else {
    toast.error(response.message || "Registration failed");
  }
};

  return (
    <div className="min-h-screen flex items-center justify-center bg-green-50">
      <div className="bg-white p-8 rounded-xl shadow-lg w-96">
        <h2 className="text-2xl font-bold text-green-600 text-center mb-2">
          Create Account ✨
        </h2>
        <p className="text-gray-500 text-sm text-center mb-6">
          Join us and start exploring amazing trips
        </p>

        <form onSubmit={handleSubmit}>
        <input
  type="text"
  name="name"
  placeholder="Enter your full name"
  value={form.name}
  onChange={handleChange}
  className="w-full p-2 border border-gray-300 rounded-lg mb-4 focus:outline-none focus:ring-2 focus:ring-green-300"
  required
/>

<input
  type="email"
  name="email"
  placeholder="Enter your email"
  value={form.email}
  onChange={handleChange}
  className="w-full p-2 border border-gray-300 rounded-lg mb-4 focus:outline-none focus:ring-2 focus:ring-green-300"
  required
/>

<input
  type="password"
  name="password"
  placeholder="Create a password"
  value={form.password}
  onChange={handleChange}
  className="w-full p-2 border border-gray-300 rounded-lg mb-4 focus:outline-none focus:ring-2 focus:ring-green-300"
  required
/>

<input
 type="text"
 name="phone"
 placeholder="Phone (optional)"
 value={form.phone}
 onChange={handleChange}
  className="w-full p-2 border border-gray-300 rounded-lg mb-4 focus:outline-none focus:ring-2 focus:ring-green-300"
/>
          <button className="w-full bg-green-600 text-white py-3 rounded-lg hover:bg-green-700 transition duration-300">
            Register
          </button>
        </form>

        <p className="text-sm text-center text-gray-600 mt-6">
          Already have an account?{" "}
          <Link
            to="/login"
            className="text-green-600 font-medium hover:underline"
          >
            Login here
          </Link>
        </p>
      </div>
    </div>
  );
}