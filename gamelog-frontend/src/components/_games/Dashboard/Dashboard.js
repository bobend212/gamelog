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
  const [gamesTBA, setGamesTBA] = useState([]);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      const [libraryGames, wishlistGames] = await Promise.all([
        gameService.getGames(0, 2000),
        gameService.getWishlistGames(),
      ]);

      // Stats calculations
      setStats({
        totalLibrary: libraryGames.totalElements,
        totalWishlist: libraryGames.content.filter(g => g.status === 'WISHLIST').length,
        completedGames: libraryGames.content.filter(g => g.status === 'COMPLETED').length,
        currentlyPlaying: libraryGames.content.filter(g => g.status === 'PLAYING').length,
        backloggedGames: libraryGames.content.filter(g => g.status === 'BACKLOG').length,
        droppedGames: libraryGames.content.filter(g => g.status === 'DROPPED').length,
        onlineGames: libraryGames.content.filter(g => g.status === 'ONLINE').length,
      });

      // Recently updated games carousel
      const allGames = [...libraryGames.content, ...wishlistGames.content];
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

  const populateWishlistCarousels = async () => {
    try {
      const wishlistGames = await gameService.getWishlistGames(0, 100);
      const today = new Date();
      today.setHours(0, 0, 0, 0);

      const upcomingGames = wishlistGames.content.filter(game => {
        const releaseDate = new Date(game.releaseDate);
        return releaseDate >= today && !game.tba;
      }).sort((a, b) => {
        const dateA = new Date(a.releaseDate);
        const dateB = new Date(b.releaseDate);
        return dateA - dateB;
      });;

      setGamesNotReleased(upcomingGames);

      const recentlyReleased = wishlistGames.content
        .filter(game => {
          const releaseDate = new Date(game.releaseDate);
          return (releaseDate < today || releaseDate.toDateString() === today.toDateString()) && !game.tba;
        })
        .sort((a, b) => new Date(b.releaseDate) - new Date(a.releaseDate));

      setGamesReleased(recentlyReleased);

      const gamesTBA = wishlistGames.content.filter(game => game.tba);
      setGamesTBA(gamesTBA);

    } catch (err) {
      console.error('Failed to fetch wishlist games:', err);
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
            <GameCardCarousel games={lastEditedGames} header="Last Updated" />
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