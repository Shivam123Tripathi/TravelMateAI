import { useState } from "react";

export default function Profile(){

  const [user] = useState(()=>{
    try{
      const storedUser = localStorage.getItem("user");
      return storedUser ? JSON.parse(storedUser) : null;
    }catch{
      return null;
    }
  });

  if(!user){
    return(
      <div className="text-center mt-20">
        User not found
      </div>
    );
  }

  return(
    <div className="max-w-3xl mx-auto p-10">

      <h1 className="text-3xl font-bold mb-6">
        My Profile
      </h1>

      <div className="border p-6 rounded-lg space-y-4">

        <p>
          <span className="font-semibold">Name:</span> {user.name}
        </p>

        <p>
          <span className="font-semibold">Email:</span> {user.email}
        </p>

        <p>
          <span className="font-semibold">Role:</span> {user.role}
        </p>

        <p>
          <span className="font-semibold">Phone:</span> {user.phone || "Not added"}
        </p>
      </div>

    </div>
  );
}