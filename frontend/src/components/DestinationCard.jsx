const DestinationCard = ({ image, name, description }) => {
  return (
    <div className="bg-white rounded-2xl shadow-md overflow-hidden hover:shadow-xl transition duration-300">
      
      <div className="overflow-hidden">
        <img
          src={image}
          alt={name}
          className="w-full h-56 object-cover hover:scale-110 transition duration-500"
        />
      </div>

      <div className="p-6">
        <h3 className="text-xl font-semibold mb-2">{name}</h3>
        <p className="text-gray-600 text-sm">{description}</p>
      </div>

    </div>
  )
}

export default DestinationCard