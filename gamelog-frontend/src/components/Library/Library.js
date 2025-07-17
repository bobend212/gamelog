import React, { useState, useEffect } from 'react';
import gameService from '../../services/gameService';
import GameCard from './GameCard';
import LoadingSpinner from '../Common/LoadingSpinner';
import ErrorMessage from '../Common/ErrorMessage';
import './Library.css';

const Library = () => {
  const [games, setGames] = useState([]);
  const [filteredGames, setFilteredGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  useEffect(() => {
    loadLibraryGames();
  }, []);

  useEffect(() => {
    filterGames();
  }, [games, searchTerm, statusFilter]);

  const loadLibraryGames = async () => {
    try {
      setLoading(true);
      const libraryGames = await gameService.getLibraryGames();
      setGames(libraryGames);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const filterGames = () => {
    let filtered = games;

    // Filter by search term
    if (searchTerm) {
      filtered = filtered.filter(game =>
        game.title.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    // Filter by status
    if (statusFilter !== 'ALL') {
      filtered = filtered.filter(game => game.status === statusFilter);
    }

    setFilteredGames(filtered);
  };

  const handleGameUpdate = () => {
    loadLibraryGames();
  };

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorMessage message={error} />;

  return (
    <div className="library">
      <div className="container">
        <div className="library-header">
          <h1>Library ({filteredGames.length})</h1>
          <div className="library-controls">
            <input
              type="text"
              placeholder="Search games..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="search-input"
            />
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="filter-select"
            >
              <option value="ALL">All</option>
              <option value="DROPPED">Dropped</option>
              <option value="PLAYING">Playing</option>
              <option value="COMPLETED">Completed</option>
              <option value="BACKLOG">Backlog</option>
            </select>
          </div>
        </div>

        {filteredGames.length > 0 ? (
          <div className="games-grid">
            {filteredGames.map(game => (
              <GameCard 
                key={game.id} 
                game={game} 
                onUpdate={handleGameUpdate}
                showStatus={true}
              />
            ))}
          </div>
        ) : (
          <div className="no-games">
            {games.length === 0 ? (
              <p>Your library is empty. Add some games to get started!</p>
            ) : (
              <p>No games match your current filters.</p>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default Library;
