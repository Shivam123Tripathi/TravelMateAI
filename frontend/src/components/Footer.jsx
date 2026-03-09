const Footer = () => {
  return (
    <footer className="bg-sky-900 text-white py-10">
      <div className="max-w-6xl mx-auto px-6 grid md:grid-cols-3 gap-8">

        {/* Logo + About */}
        <div>
          <h2 className="text-2xl font-bold mb-4">Travel Mate AI</h2>
          <p className="text-sm text-gray-300">
            Your smart AI-powered travel planner that creates personalized
            itineraries based on your preferences and budget.
          </p>
        </div>

        {/* Quick Links */}
        <div>
          <h3 className="text-lg font-semibold mb-4">Quick Links</h3>
          <ul className="space-y-2 text-sm text-gray-300">
            <li className="hover:text-white cursor-pointer">Home</li>
            <li className="hover:text-white cursor-pointer">Planner</li>
            <li className="hover:text-white cursor-pointer">Explore</li>
          </ul>
        </div>

        {/* Contact */}
        <div>
          <h3 className="text-lg font-semibold mb-4">Contact</h3>
          <p className="text-sm text-gray-300">
            Email: support@travelmateai.com
          </p>
          <p className="text-sm text-gray-300 mt-2">
            Made with ❤️ by Final Year Team
          </p>
        </div>

      </div>

      <div className="text-center text-gray-400 text-sm mt-10 border-t border-gray-700 pt-6">
        © 2026 Travel Mate AI. All rights reserved.
      </div>
    </footer>
  )
}

export default Footer