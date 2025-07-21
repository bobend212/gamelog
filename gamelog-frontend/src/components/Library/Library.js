import React, { useState, useEffect, useCallback } from 'react';
import gameService from '../../services/gameService';
import GameCard from './GameCard';
import LoadingSpinner from '../Common/LoadingSpinner';
import ErrorMessage from '../Common/ErrorMessage';
import './Library.css';
import Pagination from '../Common/Pagination';

const Library = () => {
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [debouncedSearchTerm, setDebouncedSearchTerm] = useState(''); // ✅ Add debounced state
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [allGamesCount, setAllGamesCount] = useState(0); // ✅ Track total games without filters

  // pagination consts
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalGames, setTotalGames] = useState(0);
  const [pageSize] = useState(8);

  // ✅ Debounce search term
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearchTerm(searchTerm);
    }, 400);

    return () => clearTimeout(timer);
  }, [searchTerm]);

  // ✅ Load data when debounced search, status, or page changes
  useEffect(() => {
    loadLibraryGames(currentPage);
  }, [currentPage, debouncedSearchTerm, statusFilter]);

  // ✅ Reset to page 0 when filters change (but not on every keystroke)
  useEffect(() => {
    if (currentPage !== 0) {
      setCurrentPage(0);
    }
  }, [debouncedSearchTerm, statusFilter]);

  const loadLibraryGames = async (page) => {
    try {
      setLoading(true);
      const response = await gameService.getLibraryGames(
        page,
        pageSize,
        statusFilter,
        debouncedSearchTerm // ✅ Use debounced term for API call
      );

      const totalCountResponse = await gameService.getAllLibraryGames();

      setGames(response.content || []);
      setTotalPages(response.totalPages || 0);
      setTotalGames(response.totalElements || 0);
      setAllGamesCount(totalCountResponse.length || 0); // Total count
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

  // ✅ Handle search input without triggering API calls
  const handleSearchChange = (e) => {
    setSearchTerm(e.target.value); // This won't trigger API calls immediately
  };

  const handleStatusChange = (e) => {
    setStatusFilter(e.target.value);
  };

  const clearFilters = () => {
    setSearchTerm('');
    setDebouncedSearchTerm(''); // ✅ Also clear debounced term
    setStatusFilter('ALL');
    setCurrentPage(0);
  };

  const hasActiveFilters = searchTerm.length > 0 || statusFilter !== 'ALL';

  const getLibraryTitle = () => {
    if (hasActiveFilters) {
      return `Library (${totalGames} of ${allGamesCount})`;
    }
    return `Library (${allGamesCount})`;
  };

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorMessage message={error} />;

  return (
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
                value={searchTerm} // ✅ Uses immediate state for UI responsiveness
                onChange={handleSearchChange}
                className="search-input"
              />
              {/* ✅ Optional: Show loading indicator while searching */}
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
              <option value="PLAYING">Playing</option>
              <option value="COMPLETED">Completed</option>
              <option value="BACKLOG">Backlog</option>
              <option value="DROPPED">Dropped</option>
            </select>
          </div>
        </div>

        {games.length > 0 ? (
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
                : 'Your library is empty. Add some games to get started!'
              }
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Library;
