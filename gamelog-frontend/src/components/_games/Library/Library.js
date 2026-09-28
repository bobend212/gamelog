import React, { useState, useEffect } from 'react';
import gameService from '../services/gameService';
import GameCard from './GameCard';
import LoadingSpinner from '../Common/LoadingSpinner';
import ErrorMessage from '../Common/ErrorMessage';
import './Library.css';
import Pagination from '../Common/Pagination';
import Navbar from '../Navigation/Navbar';

const Library = () => {
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [debouncedSearchTerm, setDebouncedSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [allGamesCount, setAllGamesCount] = useState(0);

  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalGames, setTotalGames] = useState(0);
  const [pageSize] = useState(12);

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearchTerm(searchTerm);
    }, 400);

    return () => clearTimeout(timer);
  }, [searchTerm]);

  useEffect(() => {
    loadLibraryGames(currentPage);
  }, [currentPage, debouncedSearchTerm, statusFilter]);

  useEffect(() => {
    if (currentPage !== 0) {
      setCurrentPage(0);
    }
  }, [debouncedSearchTerm, statusFilter]);

  const loadLibraryGames = async (page) => {
    try {
      setLoading(true);
      setError(null);

      const response = await gameService.getGames(
        page,
        pageSize,
        statusFilter,
        debouncedSearchTerm
      );

      const totalCountResponse = await gameService.getGames(0, 2000);

      setGames(response.content || []);
      setTotalPages(response.totalPages || 0);
      setTotalGames(response.totalElements || 0);
      setAllGamesCount(totalCountResponse.totalElements || 0);
    } catch (err) {
      setError(err.message);
      setGames([]);
      setTotalPages(0);
      setTotalGames(0);
    } finally {
      setLoading(false);
    }
  };

  const handleGameUpdate = () => {
    loadLibraryGames(currentPage);
  };

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleSearchChange = (e) => {
    setSearchTerm(e.target.value);
  };

  const handleStatusChange = (e) => {
    setStatusFilter(e.target.value);
  };

  const clearFilters = () => {
    setSearchTerm('');
    setDebouncedSearchTerm('');
    setStatusFilter('ALL');
    setCurrentPage(0);
  };

  const hasActiveFilters =
    searchTerm.length > 0 || statusFilter !== 'ALL';

  const getLibraryTitle = () => {
    if (hasActiveFilters) {
      return `Library (${totalGames} of ${allGamesCount})`;
    }

    return `Library (${allGamesCount})`;
  };

  return (
    <>
      <Navbar />

      <div className="library">
        <div className="container">
          <div className="library-header">
            <h1>{getLibraryTitle()}</h1>

            {totalPages > 1 && (
              <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={handlePageChange}
              />
            )}

            <div className="library-controls">
              {hasActiveFilters && (
                <button
                  onClick={clearFilters}
                  className="clear-filters-btn"
                  title="Clear all filters"
                >
                  ✕
                </button>
              )}

              <div className="search-container">
                <input
                  type="text"
                  placeholder="Search games..."
                  value={searchTerm}
                  onChange={handleSearchChange}
                  className="search-input"
                />

                {searchTerm !== debouncedSearchTerm && (
                  <div className="search-loading">🔍</div>
                )}
              </div>

              <select
                value={statusFilter}
                onChange={handleStatusChange}
                className="filter-select"
              >
                <option value="ALL">All</option>
                <option value="WISHLIST">Wishlist</option>
                <option value="PLAYING">Playing</option>
                <option value="COMPLETED">Completed</option>
                <option value="BACKLOG">Backlog</option>
                <option value="DROPPED">Dropped</option>
                <option value="ONLINE">Online</option>
              </select>
            </div>
          </div>

          {error && <ErrorMessage message={error} />}

          <div style={{ position: 'relative' }}>
            {loading && games.length > 0 && (
              <div className="library-loading">
                <LoadingSpinner />
              </div>
            )}

            {loading && games.length === 0 ? (
              <LoadingSpinner />
            ) : games.length > 0 ? (
              <div className="games-grid">
                {games.map(game => (
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
                <p>
                  {debouncedSearchTerm || statusFilter !== 'ALL'
                    ? 'No games match your current filters.'
                    : 'Your library is empty. Add some games to get started!'}
                </p>
              </div>
            )}
          </div>
        </div>
      </div>
    </>
  );
};

export default Library;