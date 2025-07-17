import React, { useState } from 'react';
import gameService from '../../services/gameService';

const SearchResultCard = ({ game, onGameAdded }) => {
  const [isAdding, setIsAdding] = useState(false);
  const [addedStatus, setAddedStatus] = useState(null);

  const handleAddToLibrary = async () => {
    try {
      setIsAdding(true);
      await gameService.addToLibrary(game.rawgId);
      setAddedStatus('library');
      onGameAdded();
    } catch (error) {
      console.error('Failed to add to library:', error);
      alert('Failed to add game to library');
    } finally {
      setIsAdding(false);
    }
  };

  const handleAddToWishlist = async () => {
    try {
      setIsAdding(true);
      await gameService.addToWishlist(game.rawgId);
      setAddedStatus('wishlist');
      onGameAdded();
    } catch (error) {
      console.error('Failed to add to wishlist:', error);
      alert('Failed to add game to wishlist');
    } finally {
      setIsAdding(false);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'Unknown';
    try {
      const dateObj = new Date(dateString);
      const options = { day: '2-digit', month: 'long', year: 'numeric' };
      return dateObj.toLocaleDateString('en-GB', options);
    } catch (error) {
      return 'Unknown';
    }
  };

  return (
    <div className="search-result-card">
      <div className="game-image">
        {game.imageUrl ? (
          <img src={game.imageUrl} alt={game.title} />
        ) : (
          <div className="no-image">🎮</div>
        )}

        {/* Status Badge */}
        {addedStatus && (
          <div className={`status-badge ${addedStatus}`}>
            {addedStatus === 'library' ? '📚 In Library' : '⭐ In Wishlist'}
          </div>
        )}
      </div>

      <div className="game-content">
        <h3 className="game-title">{game.title}</h3>

        <div className="game-meta">
          <p className="release-date">Released: {formatDate(game.releaseDate)}</p>
          {game.rating && (
            <div className="rating">
              ⭐ {game.rating.toFixed(1)}
            </div>
          )}
        </div>

        <div className="action-buttons">
          <button
            onClick={handleAddToLibrary}
            disabled={isAdding || addedStatus === 'library'}
            className="btn btn-primary btn-sm"
          >
            {addedStatus === 'library' ? 'In Library' :
              isAdding ? 'Adding...' : 'Add to Library'}
          </button>

          <button
            onClick={handleAddToWishlist}
            disabled={isAdding || addedStatus === 'wishlist'}
            className="btn btn-secondary btn-sm"
          >
            {addedStatus === 'wishlist' ? 'In Wishlist' :
              isAdding ? 'Adding...' : 'Add to Wishlist'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default SearchResultCard;
