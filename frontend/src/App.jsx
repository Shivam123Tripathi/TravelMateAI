import { Routes, Route } from "react-router-dom"
import Navbar from "./components/Navbar"
import { ToastContainer } from "react-toastify"
import "react-toastify/dist/ReactToastify.css"
import Home from "./pages/Home"
import Planner from "./pages/Planner"
import Explore from "./pages/Explore"
import Footer from "./components/Footer"
import SavedTrips from "./pages/SavedTrips"
import DestinationDetails from "./pages/DestinationDetails";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Trips from "./pages/Trips";
import TripDetails from "./pages/TripDetails";
import MyBookings from "./pages/MyBookings";
import ProtectedRoutes from "./components/ProtectedRoutes";
import AdminDashboard from "./pages/AdminDashboard";
import Recommendations from "./pages/Recommendations" ;
import Profile from "./pages/Profile";
import AdminTrips from "./pages/AdminTrips";
import AddTrip from "./pages/AddTrip";
function App() {
  return (
    <>
  
      <Navbar />
      <div className="pt-20">
        
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/planner" element={<Planner />} />
          <Route path="/explore" element={<Explore />} />
          <Route path="/saved" element={<SavedTrips />} />
          <Route path="/destination/:id" element={<DestinationDetails />} />
          <Route path="/trips" element={<ProtectedRoutes><Trips /></ProtectedRoutes>} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/trip/:id" element={<ProtectedRoutes><TripDetails /></ProtectedRoutes>} />
          <Route 
            path="/recommendations" 
            element={
              <ProtectedRoutes>
                <Recommendations/>
             </ProtectedRoutes>
            }
          />
          <Route
          path="/my-bookings"
          element={
            <ProtectedRoutes>
              <MyBookings />
            </ProtectedRoutes>
          }
          />

          <Route
          path="/admin"
          element={
            <ProtectedRoutes roleRequired="ADMIN">
             <AdminDashboard />
            </ProtectedRoutes>
          }
         />

         <Route path="/profile" element={
            <ProtectedRoutes>
              <Profile/>
            </ProtectedRoutes>
          }/>

          <Route path="/admin/trips" element={
            <ProtectedRoutes roleRequired="ADMIN">
              <AdminTrips/>
            </ProtectedRoutes>
          }/>

          <Route path="/admin/add-trip" element={
            <ProtectedRoutes roleRequired="ADMIN">
              <AddTrip/>
            </ProtectedRoutes>
          }/>
          
        </Routes>
        
      </div>

      <ToastContainer 
        position="top-right"
        autoClose={2000}
        theme="colored"
      />
      
       <Footer />
   </>
  
  )
}

export default App