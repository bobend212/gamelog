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
          <h2>📺 TV Series Library</h2>
        </div>
        <div className="nav-links">
          <Link to="/" className={isActive('/')}>
            Home
          </Link>
          <hr />
          <Link to="/tv-series/dashboard" className={isActive('/tv-series/dashboard')}>
            Dashboard
          </Link>
          <Link to="/games/library" className={isActive('/games/library')}>
            Library
          </Link>
          <Link to="/games/wishlist" className={isActive('/games/wishlist')}>
            Wishlist
          </Link>
          <Link to="/tv-series/search" className={isActive('/tv-series/search')}>
            Search TV Series
          </Link>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
