import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import './MoviesNavbar.css';

const MoviesNavbar = () => {
    const location = useLocation();

    const isActive = (path) => {
        return location.pathname === path ? 'nav-link active' : 'nav-link';
    };

    return (
        <nav className="navbar">
            <div className="container">
                <div className="nav-brand">
                    <h2>🎬 Movies to Watch</h2>
                </div>
                <div className="nav-links">
                    <Link to="/" className={isActive('/')}>
                        ⾕ Home
                    </Link>
                    <hr />
                    <Link to="/movies/dashboard" className={isActive('/movies/dashboard')}>
                        Dashboard
                    </Link>
                    <Link to="/movies/search" className={isActive('/movies/search')}>
                        Search
                    </Link>
                </div>
            </div>
        </nav>
    );
};

export default MoviesNavbar;
