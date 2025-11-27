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
          <h2>📺 TV Shows Library</h2>
        </div>
        <div className="nav-links">
          <Link to="/" className={isActive('/')}>
            ⾕ Home
          </Link>
          <hr />
          <Link to="/tv-shows/dashboard" className={isActive('/tv-shows/dashboard')}>
            Dashboard
          </Link>
          <Link to="/tv-shows/sync" className={isActive('/tv-shows/sync')}>
            Sync
          </Link>
          <Link to="/tv-shows/search" className={isActive('/tv-shows/search')}>
            Search
          </Link>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
