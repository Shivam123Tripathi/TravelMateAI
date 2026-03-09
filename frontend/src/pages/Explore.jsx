import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import  goaImg from "../assets/images/goa.jpg";
import manaliImg from "../assets/images/manali.jpeg";
import kashmirImg from "../assets/images/kashmir.jpeg";
import rishikeshImg from "../assets/images/rishikesh.jpeg";
import jaipurImg from "../assets/images/jaipur.jpeg";
import mumbaiImg from "../assets/images/mumbai.jpeg";
import agraImg from "../assets/images/agra.jpeg";
import varanasiImg from "../assets/images/varanasi.jpeg";
import travelImg from "../assets/images/travel.jpeg";
import amritsarImg from "../assets/images/amritsar.jpeg";
import vrindawanImg from "../assets/images/vrindawan.jpeg";
import rannImg from "../assets/images/rannutsav.jpeg";
import keralaImg from "../assets/images/kerala.jpeg";
import sikkimImg from "../assets/images/sikkim.jpeg";
import laImg from "../assets/images/la.jpeg";

const destinations = [
  {
    id: 1,
    name: "Goa",
    category: "Beach",
    image: goaImg,
  },
  {
    id: 2,
    name: "Manali",
    category: "Mountains",
    image: manaliImg,
  },
  {
    id: 3,
    name: "Jaipur",
    category: "Heritage",
    image: jaipurImg,
  },
  {
    id: 4,
    name: "Kashmir",
    category: "Mountains",
    image: kashmirImg,
  },
  {
    id: 5,
    name: "Rishikesh",
    category: "Adventure",
    image: rishikeshImg,
  },
  {
    id: 6,
    name: "Mumbai",
    category: "Beach",
    image: mumbaiImg,
  },
  {
    id: 7,
    name: "Agra",
    category: "Heritage",
    image: agraImg,
  },
  {
    id: 8,
    name: "Varanasi",
    category: "Spritual",
    image: varanasiImg,
  },
  {
    id: 9,
    name: "Kerala",
    category: "Nature",
    image: keralaImg,
  },
  {
    id: 10,
    name: "Sikkim",
    category: "Nature",
    image: sikkimImg,
  },
  {
    id: 11,
    name: "lakshdweep & Andmon Island",
    category: "Beach",
    image: laImg,
  },
  {
    id: 12,
    name: "Amritsar",
    category: "Spritual",
    image: amritsarImg,
  },
  {
    id: 13,
    name: "Vrindawan",
    category: "Spritual",
    image: vrindawanImg,
  },
  {
    id: 14,
    name: "Rann of kutch",
    category: "Nature",
    image: rannImg,
  },

];

const Explore = () => {
  const [search, setSearch] = useState("");
  const [activeCategory, setActiveCategory] = useState("All");

  const categories = ["All", "Beach", "Mountains", "Heritage", "Spritual", "Nature", "Adventure"];

  const filteredDestinations = destinations.filter((place) => {
    const matchesSearch = place.name
      .toLowerCase()
      .includes(search.toLowerCase());

    const matchesCategory =
      activeCategory === "All" || place.category === activeCategory;

    return matchesSearch && matchesCategory;
  });

  const navigate = useNavigate();
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-100 via-white to-purple-100">

      {/* HERO SECTION */}
      <div className="relative h-[60vh] flex items-center justify-center text-center">
        <img
          src = {travelImg}
          alt="Travel"
          className="absolute w-full h-full object-cover"
        />
        <div className="absolute inset-0 bg-black/40"></div>

        <div className="relative z-10 text-white px-4">
          <h1 className="text-5xl font-bold mb-4">
            Discover Your Next Adventure ✈️
          </h1>
          <p className="text-lg opacity-90">
            Explore beautiful destinations around the world
          </p>
        </div>
      </div>

      {/* SEARCH BAR */}
      <div className="flex justify-center mt-10 px-4">
        <input
          type="text"
          placeholder="Search destination..."
          className="w-full md:w-1/2 p-3 rounded-full shadow-md border focus:outline-none focus:ring-2 focus:ring-blue-400"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {/* CATEGORY FILTER */}
      <div className="flex flex-wrap justify-center gap-4 mt-6 px-4">
        {categories.map((cat) => (
          <button
            key={cat}
            onClick={() => setActiveCategory(cat)}
            className={`px-5 py-2 rounded-full text-sm font-medium transition ${
              activeCategory === cat
                ? "bg-blue-600 text-white shadow-md"
                : "bg-white text-gray-700 shadow hover:bg-blue-100"
            }`}
          >
            {cat}
          </button>
        ))}
      </div>

      {/* DESTINATION CARDS */}
      <div className="grid md:grid-cols-3 sm:grid-cols-2 gap-8 p-10">
        {filteredDestinations.map((place) => (
          <div
            key={place.id}
            className="bg-white rounded-2xl shadow-lg overflow-hidden hover:scale-105 hover:shadow-2xl transition duration-300"
          >
            <img
              src={place.image}
              alt={place.name}
              className="h-56 w-full object-cover"
            />

            <div className="p-5">
              <h2 className="text-xl font-semibold text-gray-800">
                {place.name}
              </h2>
              <p className="text-sm text-gray-500 mt-1">
                Category: {place.category}
              </p>

             <button
               onClick={() => navigate(`/destination/${place.id}`)}
               className="mt-4 w-full bg-gradient-to-r from-blue-500 to-purple-500 text-white py-2 rounded-lg hover:opacity-90 transition"
             >
             View Details
             </button>
            </div>
          </div>
        ))}
      </div>

    </div>
  );
};

export default Explore;