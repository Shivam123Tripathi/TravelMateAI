import React from "react";
import { useParams, useNavigate } from "react-router-dom";
import  goaImg from "../assets/images/goa.jpg";
import manaliImg from "../assets/images/manali.jpeg";
import kashmirImg from "../assets/images/kashmir.jpeg";
import rishikeshImg from "../assets/images/rishikesh.jpeg";
import jaipurImg from "../assets/images/jaipur.jpeg";
import mumbaiImg from "../assets/images/mumbai.jpeg";
import agraImg from "../assets/images/agra.jpeg";
import varanasiImg from "../assets/images/varanasi.jpeg";
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
    image: goaImg,
    description:
      "Goa is famous for its stunning beaches, vibrant nightlife, Portuguese heritage, and seafood, making it a top year-round destination.",
    bestTime: "November to February",
    budget: "₹10,000 - ₹15,000 (3-4 days approx)",
    attractions: [
      "Baga Beach",
      "Calangute Beach",
      "Chapora Fort",
      "Dudhsagar Waterfalls",
      "Anjuna Flea Market",

    ],
  },
  {
    id: 2,
    name: "Manali",
    image: manaliImg,
    description:
      "Manali is a premier Himalayan hill station famous for the 'Valley of Gods' scenery, adventure sports (skiing, paragliding), and snowfall,",
    bestTime: "December to June",
    budget: "₹6,000 - ₹15,000",
    attractions: [
      "Solang Valley",
      "Rohtang Pass",
      "Hadimba Temple",
      "Old Manali",
      "Mall Road",
    ],
  },
  {
    id: 3,
    name: "Jaipur",
    image: jaipurImg,
    description:
      "Jaipur, the 'Pink City' is famous for its UNESCO-listed forts, royal palaces, vibrant bazaars (jewelry/textiles), and rich Rajput culture.",
    bestTime: "October to March",
    budget: "₹8,000 - ₹18,000 (3-4 days approx)",
    attractions: [
      "Hawa Mahal",
      "Amber Palace (Amer Fort)",
      "City Palace",
      "Jantar Mantar",
      "Jal Mahal",
      "Bapu Bazar",
      "Chokhi Dhani for traditional Rajasthani food and culture.",
    ],
  },
  {
    id: 4,
    name: "Kashmir",
    image: kashmirImg,
    description:
      "Kashmir, the 'Heaven on Earth,' is famous for its stunning Himalayan landscapes, serene Dal Lake houseboats, Mughal gardens, and vibrant handicrafts (carpets, shawls).",
    bestTime: "March to August",
    budget: "₹8,000 - ₹15,000",
    attractions: [
      "Dal Lake",
      "Nigeen Lake",
      "Mughal Gardens(Shalimar Bagh, Nishat Bagh, Chashme Shahi)",
      "Shankaracharya Temple.",
      "Gulmarg",
    ],
  },
    {
    id: 5,
    name: "Rishikesh",
    image: rishikeshImg,
    description:
      "Rishikesh, the 'Yoga Capital of the World' on the Ganges, is famous for spiritual ashrams, white-water rafting, bungee jumping, and cafes.",
    bestTime: "September to November or March to May",
    budget: "₹7,000 - ₹15,000 (3-4 days approx)",
    attractions: [
      "Parmarth Niketan Ashram (evening Ganga Aarti)",
      "Triveni Ghat",
      "Neel Kanth Mahadev Temple",
      "White-water rafting (Shivpuri/Brahmapuri)",
      "bungee jumping",
      "Laxman Jhula and Ram Jhula suspension bridges",
      "Neer Garh Waterfall and hiking in Rajaji National Park",
    ],
  },
  {
    id: 6,
    name: "Mumbai",
    image: mumbaiImg,
    description:
      "Mumbai, the 'City of Dreams,' is famous for the Gateway of India, Bollywood, Marine Drive, street food (vada pav), and colonial architecture.",
    bestTime: "October to February",
    budget: "₹8,000 - ₹20,000",
    attractions: [
      "Gateway of India",
      "Chhatrapati Shivaji Terminus (CST)",
      "Siddhivinayak Temple",
      "Elephanta Caves",
      "Haji Ali Dargah",
      "Juhu Beach",
      "Sanjay Gandhi National Park",
      "Marine Drive",
      "Mani Bhavan",
      "Chhatrapati Shivaji Maharaj Vastu Sangrahalaya",
    ],
  },
  {
    id: 7,
    name: "Agra",
    image: agraImg,
    description:
      "Agra is world-famous for the Taj Mahal, a UNESCO World Heritage site, and its rich Mughal history, including magnificent forts and mausoleums.",
    bestTime: "October to March",
    budget: "₹7,000 - ₹20,000 (3-4 days approx)",
    attractions: [
      "Taj Mahal",
      "Agra Fort",
      "Fatehpur Sikri",
      "Baby Taj",
      "Mehtab Bagh",
      "Sikandra",
    ],
  },
  {
    id: 8,
    name: "Varanasi",
    image: varanasiImg,
    description:
      "Varanasi, the spiritual capital of India, is famous for its 80+ ancient riverfront Ghats, the sacred Ganges, the mesmerizing evening Ganga Aarti, and centuries-old temples",
    bestTime: "October to March",
    budget: "₹8,000 - ₹25,000",
    attractions: [
      "Shri Kashi Vishwanath Temple",
      "Sankat Mochan Hanuman Temple",
      "Dashashwamedh Ghat (Aarti)",
      "Assi Ghat (sunrise)",
      "Sarnath",
      "Banaras Hindu University (BHU)",
    ],
  },
  {
    id: 9,
    name: "Kerala",
    image: keralaImg,
    description:
      "Kerala is world-famous for its tranquil backwaters, lush tea plantations in Munnar, Ayurvedic treatments, coconut-fringed beaches, and houseboat cruises.",
    bestTime: "November to February",
    budget: "₹8,000 - ₹18,000",
    attractions: [
      "November to February(houseboat cruises)",
      "Fort Kochi",
      "Dutch Palace",
      "Tea gardens",
      "Mattupetty Dam",
      "Eravikulam National Park",
    ],
  },
   {
    id: 10,
    name: "Sikkim",
    image: sikkimImg,
    description:
      "Sikkim, a serene Himalayan state, is famous for the towering Kanchenjunga peak, ancient Buddhist monasteries, pristine alpine lakes like Tsomgo, and vibrant rhododendron valleys",
    bestTime: "March to May and October to mid-December ",
    budget: "₹8,000 - ₹15,000",
    attractions: [
      "Kanchenjunga (world's 3rd highest peak)",
      "Yumthang Valley",
      "river rafting",
      "Tsomgo Lake",
      "Historic Indo-China border pass",
      "MG Marg for shopping",
    ],
  },
   {
    id: 11,
    name: "Lakshadweep & Andaman Islands",
    image: laImg,
    description:
      "Lakshadweep and Andaman are premier Indian island destinations famous for pristine beaches, turquoise lagoons, vibrant coral reefs, and water sports like scuba diving and snorkeling. ",
    bestTime: "October to February",
    budget: "₹16,000 - ₹35,000",
    attractions: [
      "Bangaram Island",
      "Agatti Island",
      "Kavaratti Island",
      "Radhanagar Beach ",
      "Cellular Jail (Port Blair)",
      "Neil Island",
    ],
  },
   {
    id: 12,
    name: "Amritsar",
    image: amritsarImg,
    description:
      "Amritsar, the spiritual and cultural center of Punjab, is world-famous for the sacred Golden Temple, the historic Jallianwala Bagh, and the patriotic Wagah Border ceremony.",
    bestTime: "October to March",
    budget: "₹4,000 - ₹12,000",
    attractions: [
      "Golden Temple",
      "Wagah Border",
      "Jallianwala Bagh",
      "Partition Museum",
      "Gobindgarh Fort",
      "Durgiana Temple",
      "Hall Bazaar",
    ],
  },
   {
    id: 13,
    name: "Holi in Mathura/Vrindavan",
    image: vrindawanImg,
    description:
      "Holi in Mathura/Vrindavan is world-famous for its unique, week-long traditional, and intensely devotional celebrations, including unique, localized customs like Lathmar Holi.",
    bestTime: "around March( 4–5 days before the Holi )",
    budget: "₹10,000 - ₹18,000",
    attractions: [
      "Barsana (Lathmar Holi)",
      "Mathura (Dwarkadhish Temple)",
      "Vrindavan (Banke Bihari Temple):",
      "Yamunaji Aarti",
      "Vrindavan(ISKCON Temple)",
      "Nandgaon",
    ],
  },
   {
    id: 14,
    name: "Rann of Kutch",
    image:  rannImg,
    description:
      "The Rann of Kutch is world-renowned for being one of the largest salt deserts, offering breathtaking, vast white landscapes, particularly during full moon nights, coupled with the cultural Rann Utsav festival.",
    bestTime: "November to February",
    budget: "₹25,000 - ₹45,000",
    attractions: [
      "The White Rann (Dhordo)",
      "Rann Utsav",
      "Kala Dungar",
      "Bhuj Heritage",
      "Mandvi Beach",
      "Vijay Vilas Palace",
    ],
  },
];

const DestinationDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const destination = destinations.find(
    (item) => item.id === parseInt(id)
  );

  if (!destination) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        Destination Not Found
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">

      {/* HERO IMAGE */}
      <div className="relative h-[70vh] overflow-hidden">
        <img
          src={destination.image}
          alt={destination.name}
          className="w-full h-full object-cover object-center"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-black/70 to-black/20"></div>

        <div className="absolute bottom-12 left-12 text-white">
          <h1 className="text-5xl font-bold drop-shadow-lg">
            {destination.name}
          </h1>
        </div>
      </div>

      {/* CONTENT SECTION */}
      <div className="max-w-5xl mx-auto px-6 py-10">

        {/* Overview */}
        <h2 className="text-2xl font-semibold text-blue-600 mb-3">
          Overview
        </h2>
        <p className="text-gray-700 mb-6">
          {destination.description}
        </p>

        {/* Best Time */}
        <h2 className="text-2xl font-semibold text-blue-600 mb-3">
          Best Time to Visit
        </h2>
        <p className="text-gray-700 mb-6">
          {destination.bestTime}
        </p>

        {/* Budget */}
        <h2 className="text-2xl font-semibold text-blue-600 mb-3">
          Estimated Budget
        </h2>
        <p className="text-gray-700 mb-6">
          {destination.budget}
        </p>

        {/* Attractions */}
        <h2 className="text-2xl font-semibold text-blue-600 mb-3">
          Top Attractions
        </h2>
        <ul className="list-disc list-inside text-gray-700 space-y-2 mb-8">
          {destination.attractions.map((place, index) => (
            <li key={index}>{place}</li>
          ))}
        </ul>

        {/* Buttons */}
        <div className="flex gap-4">
          <button
            onClick={() => navigate("/planner")}
            className="bg-gradient-to-r from-blue-500 to-purple-500 text-white px-6 py-3 rounded-lg"
          >
            Add to Planner
          </button>

          <button
            onClick={() => navigate("/explore")}
            className="border border-gray-400 px-6 py-3 rounded-lg"
          >
            Back to Explore
          </button>
        </div>

      </div>
    </div>
  );
};

export default DestinationDetails;