import { useEffect,useState } from "react";
import { apiCall } from "../services/api";

export default function AdminTrips(){

  const [trips,setTrips] = useState([]);

  useEffect(()=>{

    const fetchTrips = async () => {
      try {
        const res = await apiCall("/trips?page=0&size=10");

        if (res?.data?.content) {
          setTrips(res.data.content);
        } else {
          setTrips([]);
        }
 
      } catch (error) {
        console.error("Failed to load trips", error);
          setTrips([]);
      }
    };

    fetchTrips();

  },[])

  return(

    <div className="p-10">

      <h1 className="text-3xl font-bold mb-6">
        Manage Trips
      </h1>

      <table className="w-full">

        <thead>

          <tr className="border-b">
            <th>Title</th>
            <th>Destination</th>
            <th>Price</th>
          </tr>

        </thead>

        <tbody>

          {trips.map(t=>(
            <tr key={t.id} className="border-b">

              <td>{t.title}</td>

              <td>{t.destination}</td>

              <td>₹{t.price}</td>

            </tr>
          ))}

        </tbody>

      </table>

    </div>

  )

}