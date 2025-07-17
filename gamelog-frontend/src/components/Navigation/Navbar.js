import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import './Navbar.css';

const Navbar = () => {
  const location = useLocation();

  const isActive = (path) => {
    return location.pathname === path ? 'nav-link active' : 'nav-link';
  };

  return (
    <nav className="navbar">
      <div className="container">
        <div className="nav-brand">
          <h2>🎮 Game Library</h2>
        </div>
        <div className="nav-links">
          <Link to="/" className={isActive('/')}>
            Dashboard
          </Link>
          <Link to="/library" className={isActive('/library')}>
            Library
          </Link>
          <Link to="/wishlist" className={isActive('/wishlist')}>
            Wishlist
          </Link>
          <Link to="/search" className={isActive('/search')}>
            Search Games
          </Link>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
