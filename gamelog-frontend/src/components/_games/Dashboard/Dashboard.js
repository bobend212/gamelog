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
    droppedGames: 0,
    onlineGames: 0
  });
  const [lastEditedGames, setLastEditedGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [gamesNotReleased, setGamesNotReleased] = useState([]);
  const [gamesReleased, setGamesReleased] = useState([]);
  const [gamesTBA, setGamesTBA] = useState([]);


  const loadDashboardData = async () => {
    try {
      setLoading(true);
      // const [libraryGames, wishlistGames] = await Promise.all([
      //   gameService.getGames(0, 2000),
      //   gameService.getWishlistGames(),
      // ]);
      const dashboard = await gameService.getGamesDashboard();

      setStats({
        totalLibrary: dashboard.stats.totalGames,
        totalWishlist: dashboard.stats.wishlisted,
        completedGames: dashboard.stats.completed,
        currentlyPlaying: dashboard.stats.playing,
        droppedGames: dashboard.stats.dropped,
        onlineGames: dashboard.stats.online,
      });

      // Recently updated games carousel
      // const allGames = [...libraryGames.content, ...wishlistGames.content];
      // const sortedGames = allGames
      //   .sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt))
      //   .slice(0, 20);
      setLastEditedGames(dashboard.recentlyUpdated);

    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const populateWishlistCarousels = async () => {
    try {
      const wishlistGames = await gameService.getWishlistGames(0, 100);

      const today = new Date();
      today.setHours(0, 0, 0, 0);

      const upcoming = [];
      const released = [];
      const tba = [];

      for (const game of wishlistGames.content) {
        if (game.tba) {
          tba.push(game);
          continue;
        }

        const releaseDate = new Date(game.releaseDate);

        if (releaseDate >= today) {
          upcoming.push(game);
        } else {
          released.push(game);
        }
      }

      upcoming.sort((a, b) => new Date(a.releaseDate) - new Date(b.releaseDate));
      released.sort((a, b) => new Date(b.releaseDate) - new Date(a.releaseDate));

      setGamesNotReleased(upcoming);
      setGamesReleased(released);
      setGamesTBA(tba);

    } catch (err) {
      console.error("Failed to fetch wishlist games:", err);
    }
  };

  useEffect(() => {
    loadDashboardData();
    populateWishlistCarousels();
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
            <GameCardCarousel games={lastEditedGames} header="Recently Updated" />
            <GameCardCarousel games={gamesNotReleased} header="Upcoming" />
            <GameCardCarousel games={gamesReleased} header="Released" />
            <GameCardCarousel games={gamesTBA} header="TBA" />
          </div>

        </div>
      </div>
    </>
  );
};

export default Dashboard;