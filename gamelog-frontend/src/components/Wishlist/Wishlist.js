import React, { useState, useEffect, useRef } from 'react';
import gameService from '../../services/gameService';
import GameCard from '../Library/GameCard';
import LoadingSpinner from '../Common/LoadingSpinner';
import ErrorMessage from '../Common/ErrorMessage';
import './Wishlist.css';
import { NO_RESULTS_MESSAGES } from '../../utils/constants';
import Pagination from '../Common/Pagination';

const Wishlist = () => {
  const [wishlistGames, setWishlistGames] = useState([]); // paged + filtered
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState(''); // raw input
  const [debouncedSearchTerm, setDebouncedSearchTerm] = useState(''); // debounced search
  const [pageSize] = useState(8);
  const [totalWishlistCount, setTotalWishlistCount] = useState(0); // ✅ Track total games without filters

  // pagination
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalGames, setTotalGames] = useState(0); // total filtered

  const searchInputRef = useRef(null);

  const fetchWishlistGames = async (page) => {
    setLoading(true);
    try {
      const response = await gameService.getWishlistGames(page, pageSize, debouncedSearchTerm);
      setWishlistGames(response.content || []);
      setTotalPages(response.totalPages || 0);
      setTotalGames(response.totalElements || 0);

      const totalWishlistCount = await gameService.getAllWishlistGames();
      setTotalWishlistCount(totalWishlistCount.length || 0); // Total count
    } catch (error) {
      console.error('Failed to fetch wishlist games:', error);
      setError('Failed to fetch wishlist games');
    } finally {
      setLoading(false);
    }
  };

  // Debounce searchTerm
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearchTerm(searchTerm.trim());
      setCurrentPage(0); // ✅ Reset page on new search
    }, 400);
    return () => clearTimeout(timer);
  }, [searchTerm]);

  useEffect(() => {
    fetchWishlistGames(currentPage);
  }, [currentPage, debouncedSearchTerm]);

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleSearchChange = (e) => {
    setSearchTerm(e.target.value);
  };

  const clearSearch = () => {
    setSearchTerm('');
    searchInputRef.current?.focus();
  };

  const hasActiveFilters = searchTerm.length > 0;
  const getWishlistPageTitle = () => {
    if (hasActiveFilters) {
      return `Wishlist (${totalGames} of ${totalWishlistCount})`;
    }
    return `Wishlist (${totalWishlistCount})`;
  };

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorMessage message={error} />;

  return (
    <div className="wishlist">
      <div className="container">
        <div className="wishlist-header">
          <div className="wishlist-header-top">
            <h1>{getWishlistPageTitle()}</h1>
            {searchTerm && (
              <p className="filter-count-note">
                Showing results for: <strong>"{debouncedSearchTerm}"</strong> {totalGames === 0 && '– No matches'}
              </p>
            )}
          </div>

          {totalPages > 1 && (
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={handlePageChange}
            />
          )}

          <div className="wishlist-controls">
            <input
              ref={searchInputRef}
              type="text"
              placeholder="Search wishlist..."
              value={searchTerm}
              onChange={handleSearchChange}
              className="search-input"
            />
            {searchTerm && (
              <button className="clear-filters-btn" onClick={clearSearch}>
                ✕
              </button>
            )}
          </div>
        </div>

        {wishlistGames.length > 0 ? (
          <div className="games-grid">
            {wishlistGames.map((game) => (
              <div key={game.id} className="wishlist-game-card">
                <GameCard game={game} onUpdate={() => fetchWishlistGames(currentPage)} showStatus={false} />
              </div>
            ))}
          </div>
        ) : (
          <div className="no-games">
            <p>{NO_RESULTS_MESSAGES.SEARCH.description}</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Wishlist;
