import { Link, useNavigate } from "react-router-dom"
import { useState, useEffect } from "react"

const Navbar = () => {

  const navigate = useNavigate()

  const [user, setUser] = useState(null)
  const [menuOpen, setMenuOpen] = useState(false)

  useEffect(() => {

  const loadUser = () => {
    try {
      const storedUser = localStorage.getItem("user");

      if (storedUser && storedUser !== "undefined") {
        setUser(JSON.parse(storedUser));
      } else {
        setUser(null);
      }

    } catch (error) {
      console.error("Invalid user in localStorage", error);
      setUser(null);
    }
  };

  loadUser();

  window.addEventListener("storage", loadUser);

  return () => {
    window.removeEventListener("storage", loadUser);
  };

}, []);

  const handleLogout = () => {
    localStorage.removeItem("token")
    localStorage.removeItem("user")
    window.dispatchEvent(new Event("storage"));
    setUser(null)
    navigate("/login")
  }

  return (
    <nav className="fixed top-0 left-0 w-full bg-white shadow-md z-50">

      <div className="max-w-7xl mx-auto px-8 py-5 flex justify-between items-center">

        {/* Logo */}
        <h1 
          className="text-2xl font-bold text-sky-600 cursor-pointer"
          onClick={() => navigate("/")}
        >
          TravelMateAI
        </h1>

        {/* Center Menu */}
        <div className="hidden md:flex gap-8 font-medium text-gray-700">

          <Link to="/" className="hover:text-sky-600 transition">
            Home
          </Link>

          <Link to="/explore" className="hover:text-sky-600 transition">
            Explore
          </Link>

          <Link to="/trips" className="hover:text-sky-600 transition">
            Trips
          </Link>

          {user && (
            <Link to="/my-bookings" className="hover:text-sky-600 transition">
              My Bookings
            </Link>
          )}

          {user?.role === "ADMIN" && (
            <Link to="/admin" className="hover:text-sky-600 transition">
              Admin Dashboard
            </Link>
          )}

        </div>

        {/* Right Side */}
        <div className="flex items-center gap-4">

          {/* Guest */}
          {!user && (
            <>
              <Link
                to="/login"
                className="px-4 py-2 text-sky-600 font-medium hover:text-sky-700"
              >
                Login
              </Link>

              <Link
                to="/register"
                className="px-5 py-2 bg-sky-600 text-white rounded-full hover:bg-sky-700 transition"
              >
                Register
              </Link>
            </>
          )}

          {/* Logged In User */}
          {user && (
            <div className="relative">

              <button
                onClick={() => setMenuOpen(!menuOpen)}
                className="flex items-center gap-2 bg-sky-100 px-4 py-2 rounded-full"
              >
                👤 {user.name}
              </button>

              {menuOpen && (
                <div className="absolute right-0 mt-3 bg-white shadow-lg rounded-xl w-40 py-2">

                  <button
                    onClick={() => navigate("/my-bookings")}
                    className="block w-full text-left px-4 py-2 hover:bg-gray-100"
                  >
                    My Bookings
                  </button>

                  {user.role === "ADMIN" && (
                    <button
                      onClick={() => navigate("/admin")}
                      className="block w-full text-left px-4 py-2 hover:bg-gray-100"
                    >
                      Admin
                    </button>
                  )}

                  <button
                    onClick={handleLogout}
                    className="block w-full text-left px-4 py-2 text-red-500 hover:bg-gray-100"
                  >
                    Logout
                  </button>

                </div>
              )}

            </div>
          )}

        </div>

      </div>

    </nav>
  )
}

export default Navbar