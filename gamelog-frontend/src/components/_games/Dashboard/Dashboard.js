import React, { useState, useEffect } from 'react';
import gameService from '../services/gameService';
import StatCard from './StatCard';
import Navbar from '../Navigation/Navbar';
import LoadingSpinner from '../Common/LoadingSpinner';
import ErrorMessage from '../Common/ErrorMessage';
import './Dashboard.css';
import GameCardCarousel from './GameCardCarousel';

const Dashboard = () => {
  const [stats, setStats] = useState({
    totalLibrary: 0,
    totalWishlist: 0,
    completedGames: 0,
    currentlyPlaying: 0,
    backloggedGames: 0,
    droppedGames: 0,
    onlineGames: 0
  });
  const [lastEditedGames, setLastEditedGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [gamesNotReleased, setGamesNotReleased] = useState([]);
  const [gamesReleased, setGamesReleased] = useState([]);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      const [libraryGames, wishlistGames] = await Promise.all([
        gameService.getAllLibraryGames(),
        gameService.getAllWishlistGames(),
      ]);

      // Stats calculations
      setStats({
        totalLibrary: libraryGames.length,
        totalWishlist: wishlistGames.length,
        completedGames: libraryGames.filter(g => g.status === 'COMPLETED').length,
        currentlyPlaying: libraryGames.filter(g => g.status === 'PLAYING').length,
        backloggedGames: libraryGames.filter(g => g.status === 'BACKLOG').length,
        droppedGames: libraryGames.filter(g => g.status === 'DROPPED').length,
        onlineGames: libraryGames.filter(g => g.status === 'ONLINE').length,
      });

      // Last edited games slice
      const allGames = [...libraryGames, ...wishlistGames];
      const sortedGames = allGames
        .sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt))
        .slice(0, 20);
      setLastEditedGames(sortedGames);

    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const populateWishlistTables = async (pageNo, rowsPerPage) => {
    try {
      const notReleasedRes = await gameService.getWishlistGamesDashboard(
        pageNo,
        rowsPerPage,
        'releaseDate,asc',
        'NOT_RELEASED_ONLY'
      );
      setGamesNotReleased(notReleasedRes.content);

      const releasedRes = await gameService.getWishlistGamesDashboard(
        pageNo,
        100,
        'releaseDate,desc',
        'RELEASED_ONLY'
      );
      setGamesReleased(releasedRes.content);
    } catch (err) {
      console.error('Failed to fetch wishlist games:', err);
    }
  };

  useEffect(() => {
    loadDashboardData();
    populateWishlistTables(0, 20);
  }, []);

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorMessage message={error} />;

  return (
    <>
      <Navbar />
      <div className="dashboard">
        <div className="container">
          <h1>Dashboard</h1>

          {/* Statistics Section */}
          <div className="stats-grid">
            <StatCard
              title="Logged"
              value={stats.totalLibrary}
              icon="📚"
              color="#3b82f6"
            />
            <StatCard
              title="Wishlisted"
              value={stats.totalWishlist}
              icon="❤️"
              color="#ef4444"
            />
            <StatCard
              title="Playing"
              value={stats.currentlyPlaying}
              icon="🎮"
              color="#8b5cf6"
            />
            <StatCard
              title="Completed"
              value={stats.completedGames}
              icon="✅"
              color="#10b981"
            />
            <StatCard
              title="Dropped"
              value={stats.droppedGames}
              icon="👎"
              color="#6b7280"
            />
            <StatCard
              title="Online / PVP"
              value={stats.onlineGames}
              icon="🌐"
              color="#38bdf8"
            />
          </div>

          <div>
            <GameCardCarousel games={lastEditedGames} header="Recently Updated - Library" />
            <GameCardCarousel games={gamesNotReleased} header="Upcoming Games - Wishlist" />
            <GameCardCarousel games={gamesReleased} header="Recently Released - Wishlist" />
          </div>

        </div>
      </div>
    </>
  );
};

export default Dashboard;