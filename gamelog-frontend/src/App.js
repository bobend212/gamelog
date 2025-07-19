import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navigation/Navbar';
import Dashboard from './components/Dashboard/Dashboard';
import Library from './components/Library/Library';
import Wishlist from './components/Wishlist/Wishlist';
import GameSearch from './components/Search/GameSearch';
import './App.css';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

function App() {
  return (
    <Router>
      <div className="App">
        <Navbar />
        <main className="main-content">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/library" element={<Library />} />
            <Route path="/wishlist" element={<Wishlist />} />
            <Route path="/search" element={<GameSearch />} />
          </Routes>
        </main>

        <ToastContainer
          position="bottom-right"
          autoClose={3000}
          theme="dark"
          hideProgressBar={true}
          newestOnTop={true}
          closeOnClick
          pauseOnHover
          draggable
        />
      </div>
    </Router>
  );
}

export default App;
