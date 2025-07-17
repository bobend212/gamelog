import React, { useState, useEffect } from 'react';
import gameService from '../../services/gameService';
import GameCard from '../Library/GameCard';
import LoadingSpinner from '../Common/LoadingSpinner';
import ErrorMessage from '../Common/ErrorMessage';
import './Wishlist.css';
import { NO_RESULTS_MESSAGES } from '../../utils/constants';

const Wishlist = () => {
  const [wishlistGames, setWishlistGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    loadWishlistGames();
  }, []);

  const loadWishlistGames = async () => {
    try {
      setLoading(true);
      const games = await gameService.getWishlistGames();
      setWishlistGames(games);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleGameUpdate = () => {
    loadWishlistGames();
  };

  const filteredGames = wishlistGames.filter(game =>
    game.title.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorMessage message={error} />;

  return (
    <div className="wishlist">
      <div className="container">
        <div className="wishlist-header">
          <h1>Wishlist ({wishlistGames.length})</h1>
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
  );
};

export default Wishlist;
