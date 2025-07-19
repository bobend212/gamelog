import React, { useState, useEffect } from 'react';
import gameService from '../../services/gameService';
import GameCard from '../Library/GameCard';
import LoadingSpinner from '../Common/LoadingSpinner';
import ErrorMessage from '../Common/ErrorMessage';
import './Wishlist.css';
import { NO_RESULTS_MESSAGES } from '../../utils/constants';
import Pagination from '../Common/Pagination';

const Wishlist = () => {
  const [wishlistGames, setWishlistGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  // pagination consts
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalGames, setTotalGames] = useState(0);
  const [pageSize] = useState(8); // Games per page

  useEffect(() => {
    fetchWishlistGames(currentPage);
  }, [currentPage]);

  const fetchWishlistGames = async (page) => {
    setLoading(true);
    try {
      const response = await gameService.getWishlistGames(page, pageSize);
      setWishlistGames(response.content);
      setTotalPages(response.totalPages);
      setTotalGames(response.totalElements);
    } catch (error) {
      console.error('Failed to fetch wishlist games:', error);
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleGameUpdate = () => {
    fetchWishlistGames();
  };

  const filteredGames = wishlistGames.filter(game =>
    game.title.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorMessage message={error}/>;

  return (
    <>
      <div className="wishlist">
        <div className="container">
          <div className="wishlist-header">
            <h1>Wishlist ({totalGames})</h1>
            {totalPages > 1 && (
              <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={handlePageChange}
              />
            )}
            <div className="wishlist-controls">
              <input
                type="text"
                placeholder="Search wishlist..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="search-input"
              />
            </div>
          </div>

          {filteredGames.length > 0 ? (
            <div className="games-grid">
              {filteredGames.map(game => (
                <div key={game.id} className="wishlist-game-card">
                  <GameCard
                    game={game}
                    onUpdate={handleGameUpdate}
                    showStatus={false}
                  />
                </div>
              ))}
            </div>
          ) : (
            <div className="no-games">
              {wishlistGames.length === 0 ? (
                <p>Wishlist is empty. Add some games you want to play!</p>
              ) : (
                <p>{NO_RESULTS_MESSAGES.SEARCH.title}</p>
              )}
            </div>
          )}
        </div>
      </div>
    </>
  );
};

export default Wishlist;
