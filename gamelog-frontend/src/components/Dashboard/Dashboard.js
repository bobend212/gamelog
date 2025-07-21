import React, { useState, useEffect } from 'react';
import gameService from '../../services/gameService';
import StatCard from './StatCard';
import GameCard from '../Library/GameCard';
import LoadingSpinner from '../Common/LoadingSpinner';
import ErrorMessage from '../Common/ErrorMessage';
import './Dashboard.css';

const Dashboard = () => {
  const [stats, setStats] = useState({
    totalLibrary: 0,
    totalWishlist: 0,
    completedGames: 0,
    currentlyPlaying: 0,
    backloggedGames: 0,
    droppedGames: 0
  });
  const [lastEditedGames, setLastEditedGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      const [libraryGames, wishlistGames] = await Promise.all([
        gameService.getAllLibraryGames(),
        gameService.getAllWishlistGames()
      ]);

      // Calculate statistics
      const completedCount = libraryGames.filter(game => game.status === 'COMPLETED').length;
      const playingCount = libraryGames.filter(game => game.status === 'PLAYING').length;
      const backlogCount = libraryGames.filter(game => game.status === 'BACKLOG').length;
      const droppedCount = libraryGames.filter(game => game.status === 'DROPPED').length;

      setStats({
        totalLibrary: libraryGames.length,
        totalWishlist: wishlistGames.length,
        completedGames: completedCount,
        currentlyPlaying: playingCount,
        backloggedGames: backlogCount,
        droppedGames: droppedCount
      });

      // Get last edited games (most recent 5)
      const allGames = [...libraryGames, ...wishlistGames];
      const sortedGames = allGames.sort((a, b) =>
        new Date(b.updatedAt) - new Date(a.updatedAt)
      ).slice(0, 8);

      setLastEditedGames(sortedGames);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorMessage message={error} />;

  return (
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
            title="Backlog"
            value={stats.backloggedGames}
            icon="📝"
            color="#f59e0b"
          />
          <StatCard
            title="Dropped"
            value={stats.droppedGames}
            icon="👎"
            color="#6b7280"
          />
        </div>

        {/* Last Edited Games Section */}
        <div className="last-edited-section">
          <h2>Recently Updated</h2>
          {lastEditedGames.length > 0 ? (
            <div className="games-grid">
              {lastEditedGames.map(game => (
                <GameCard
                  key={game.id}
                  game={game}
                  onUpdate={loadDashboardData}
                  showStatus={true}
                />
              ))}
            </div>
          ) : (
            <p className="no-games">No games found. Start by adding some games to your library!</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
