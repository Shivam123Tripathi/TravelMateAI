import { useEffect } from "react"
import { useNavigate } from "react-router-dom"

const Home = () => {

  const navigate = useNavigate()

  useEffect(() => {
    window.scrollTo(0, 0)
  }, [])

  return (
    <div className="overflow-x-hidden">

      {/* ================= HERO ================= */}

      <section className="relative h-screen flex items-center justify-center text-white">

        <video
          autoPlay
          loop
          muted
          playsInline
          className="absolute w-full h-full object-cover"
        >
          <source src="/travel-video.mp4" type="video/mp4" />
        </video>

        <div className="absolute inset-0 bg-black/55"></div>

        <div className="relative z-10 text-center px-6 max-w-3xl">

          <h1 className="text-5xl md:text-6xl font-bold mb-6 leading-tight">
            Plan your next adventure
             <br />
              without the stress.
          </h1>

          <p className="text-lg text-gray-200 mb-8">
            From mountains to beaches, tell us your travel style and we’ll help you build a
            simple, personalized travel plan in seconds.
          </p>

          <div className="flex justify-center gap-6 flex-wrap">

            <button
              onClick={() => navigate("/planner")}
              className="px-8 py-3 bg-gradient-to-r from-sky-500 to-blue-600 rounded-full font-semibold hover:scale-105 hover:shadow-2xl transition"
            >
              Plan My Trip ✈️
            </button>

            <button
              onClick={() => navigate("/explore")}
              className="px-8 py-3 border border-white rounded-full hover:bg-white hover:text-black transition"
            >
              Explore Destinations 🌍
            </button>

          </div>

        </div>
      </section>

      {/* ================= WHY TRAVELERS LIKE IT ================= */}

      <section className="py-24 bg-gradient-to-b from-sky-100 to-white">

        <div className="max-w-6xl mx-auto px-6">

          <h2 className="text-4xl font-bold text-center text-sky-700 mb-16">
            Why travelers enjoy using it
          </h2>

          <div className="grid md:grid-cols-3 gap-10">

            <div className="bg-white p-8 rounded-3xl shadow-md hover:shadow-xl transition">
              <h3 className="text-xl font-semibold mb-3 text-sky-700">
                ⚡ Quick trip plans
              </h3>
              <p className="text-gray-600">
                No more opening 20 tabs. Generate a complete itinerary in seconds.
              </p>
            </div>

            <div className="bg-white p-8 rounded-3xl shadow-md hover:shadow-xl transition">
              <h3 className="text-xl font-semibold mb-3 text-sky-700">
                💰 Budget friendly
              </h3>
              <p className="text-gray-600">
                Get estimated costs so you know what to expect before you go.
              </p>
            </div>

            <div className="bg-white p-8 rounded-3xl shadow-md hover:shadow-xl transition">
              <h3 className="text-xl font-semibold mb-3 text-sky-700">
                🌍 Personalized suggestions
              </h3>
              <p className="text-gray-600">
                Every itinerary adapts to your travel style and interests.
              </p>
            </div>

          </div>
        </div>

      </section>

      {/* ================= DISCOVER SECTION ================= */}

      <section className="py-24 bg-sky-50">

        <div className="max-w-5xl mx-auto px-6 text-center">

          <h2 className="text-4xl font-bold text-sky-700 mb-6">
            Discover places you didn’t know existed
          </h2>

          <p className="text-gray-600 mb-10 max-w-2xl mx-auto">
            From quiet mountain towns to hidden beaches,
            explore destinations that most travel guides miss.
          </p>

          <button
            onClick={() => navigate("/explore")}
            className="px-8 py-3 bg-gradient-to-r from-sky-500 to-blue-600 text-white rounded-full hover:scale-105 hover:shadow-lg transition"
          >
            Explore Destinations
          </button>

        </div>

      </section>

      {/* ================= HOW IT WORKS ================= */}

      <section className="py-24 bg-white">

        <div className="max-w-6xl mx-auto px-6">

          <h2 className="text-4xl font-bold text-center text-sky-700 mb-16">
            How it works
          </h2>

          <div className="grid md:grid-cols-3 gap-12 text-center">

            <div>
              <div className="text-3xl font-bold text-sky-600 mb-4">1</div>
              <p className="text-gray-600">
                Choose your destination and travel preferences.
              </p>
            </div>

            <div>
              <div className="text-3xl font-bold text-sky-600 mb-4">2</div>
              <p className="text-gray-600">
                Our planner builds a travel itinerary for you.
              </p>
            </div>

            <div>
              <div className="text-3xl font-bold text-sky-600 mb-4">3</div>
              <p className="text-gray-600">
                Save it, edit it, and start exploring.
              </p>
            </div>

          </div>

        </div>

      </section>

      {/* ================= POPULAR DESTINATIONS ================= */}

<section className="py-24 bg-sky-50">

  <div className="max-w-6xl mx-auto px-6">

    <h2 className="text-4xl font-bold text-center text-sky-700 mb-16">
      Popular Destinations
    </h2>

    <div className="grid md:grid-cols-3 gap-10">

      {[
        {
          name: "Kashmir",
          image: "/kashmir.jpeg"
        },
        {
          name: "Agra",
          image: "/agra.jpeg"
        },
        {
          name: "Goa",
          image: "/goa.jpeg"
        }
      ].map((place, index) => (

        <div
          key={index}
          className="group rounded-3xl overflow-hidden shadow-lg hover:shadow-2xl transition"
        >

          <div className="relative">

            <img
              src={place.image}
              className="w-full h-72 object-cover group-hover:scale-110 transition duration-500"
            />

            <div className="absolute inset-0 bg-black/30"></div>

            <h3 className="absolute bottom-4 left-4 text-white text-xl font-semibold">
              {place.name}
            </h3>

          </div>

        </div>

      ))}

    </div>

  </div>

</section>


{/* ================= TESTIMONIALS ================= */}

<section className="py-24 bg-white">

  <div className="max-w-5xl mx-auto px-6 text-center">

    <h2 className="text-4xl font-bold text-sky-700 mb-16">
      What Travelers Say
    </h2>

    <div className="grid md:grid-cols-2 gap-10">

      <div className="bg-sky-50 p-8 rounded-3xl shadow-md">
        <p className="text-gray-600 mb-4">
          "Planning our trip to Manali became so easy. The itinerary was
          perfectly organized and saved us hours of research."
        </p>
        <h4 className="font-semibold text-sky-700">
          — Aditi Sharma
        </h4>
      </div>

      <div className="bg-sky-50 p-8 rounded-3xl shadow-md">
        <p className="text-gray-600 mb-4">
          "This travel planner helped me discover places I didn't even
          know existed. Super useful and simple."
        </p>
        <h4 className="font-semibold text-sky-700">
          — Rahul Verma
        </h4>
      </div>

    </div>

  </div>

</section>

{/* ================= TRAVEL GALLERY ================= */}

<section className="py-24 bg-sky-50">

  <div className="max-w-6xl mx-auto px-6">

    <h2 className="text-4xl font-bold text-center text-sky-700 mb-16">
      Travel Inspiration
    </h2>

    <div className="grid grid-cols-2 md:grid-cols-4 gap-6">

      {[
        "/kerala.jpeg",
        "/travel1.jpeg",
        "/travel2.jpeg",
        "/travel3.jpeg"
      ].map((img, index) => (

        <img
          key={index}
          src={img}
          className="rounded-2xl object-cover h-60 w-full hover:scale-105 transition duration-300"
        />

      ))}

    </div>

  </div>

</section>

      {/* ================= STATS ================= */}

      <section className="py-20 bg-gradient-to-r from-sky-600 to-blue-700 text-white">

        <div className="max-w-5xl mx-auto grid md:grid-cols-3 gap-8 text-center">

          <div>
            <h3 className="text-4xl font-bold">10,000+</h3>
            <p className="opacity-90">Trips planned</p>
          </div>

          <div>
            <h3 className="text-4xl font-bold">5,000+</h3>
            <p className="opacity-90">Travelers helped</p>
          </div>

          <div>
            <h3 className="text-4xl font-bold">120+</h3>
            <p className="opacity-90">Destinations</p>
          </div>

        </div>

      </section>

      {/* ================= CTA ================= */}

      <section className="py-24 bg-white text-center">

        <h2 className="text-4xl font-bold text-sky-700 mb-6">
          Ready for your next adventure?
        </h2>

        <p className="text-gray-600 mb-10">
          Build your personalized travel plan in less than a minute.
        </p>

        <button
          onClick={() => navigate("/planner")}
          className="px-10 py-4 bg-gradient-to-r from-sky-500 to-blue-600 text-white rounded-full text-lg font-semibold hover:scale-105 hover:shadow-xl transition"
        >
          Start Planning
        </button>

      </section>

    </div>
  )
}

export default Home