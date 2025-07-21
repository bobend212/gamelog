import React, { useState } from 'react';
import gameService from '../../services/gameService';
import { toast } from 'react-toastify';
import EditGameModal from '../Library/EditGameModal';

const SearchResultCard = ({ game, onGameAdded }) => {
  const [isAdding, setIsAdding] = useState(false);
  const [addedStatus, setAddedStatus] = useState(null);
  const [showEditModal, setShowEditModal] = useState(false);
  const [gameData, setGameData] = useState(null);

  const openEditModalFor = (game) => {
    setGameData(game);
    setShowEditModal(true);
  };

  const handleCloseModal = () => {
    setShowEditModal(false);
    setGameData(null);
  };

  const handleAddToLibrary = async () => {
    try {
      const result = await gameService.addToLibrary(game.rawgId);
      if (result.alreadyExists) {
        handleGameAlreadyExists(result.game);
      } else {
        setIsAdding(true);
        setAddedStatus('library');
        openEditModalFor(result);
      }
    } catch (error) {
      console.error('Failed to add to library:', error);
      alert('Failed to add game to library');
    } finally {
      setIsAdding(false);
    }
  };

  const handleAddToWishlist = async () => {
    try {
      const result = await gameService.addToWishlist(game.rawgId);
      if (result.alreadyExists) {
        handleGameAlreadyExists(result.game);
      } else {
        setIsAdding(true);
        setAddedStatus('wishlist');
        onGameAdded();
        toast.success(`"${game.title}" added to Wishlist! 🟣`);
      }
    } catch (error) {
      console.error('Failed to add to wishlist:', error);
      alert('Failed to add game to wishlist');
    } finally {
      setIsAdding(false);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'TBA';
    try {
      const dateObj = new Date(dateString);
      const options = { day: '2-digit', month: 'long', year: 'numeric' };
      return dateObj.toLocaleDateString('en-GB', options);
    } catch (error) {
      return 'TBA';
    }
  };

  const handleEditSave = async (updatedGame) => {
    try {
      await gameService.updateGame(gameData.game.id, updatedGame);
      setShowEditModal(false);
      toast.success(`"${game.title}" added to Library! 🟢`);
      onGameAdded();
    } catch (error) {
      console.error('Failed to update game:', error);
      alert('Failed to update game. Please try again.');
    }
  };

  const handleGameAlreadyExists = (game) => {
    toast.warning(
      `"${game.title}" is already in the database!`,
      {
        icon: "⚠️",
        autoClose: 3000,
        position: "bottom-right",
        theme: "dark"
      }
    );
  };


  return (
    <>
      <div className="search-result-card">
        <div className="game-image">
          {game.imageUrl ? (
            <img src={game.imageUrl} alt={game.title} />
          ) : (
            <div className="no-image">🎮</div>
          )}

          {/* Status Badge */}
          {addedStatus && (
            <div className={`status-badge-search ${addedStatus}`}>
              {addedStatus === 'library' ? '📚 In Library' : '★ In Wishlist'}
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
                isAdding ? 'Adding...' : 'Log'}
            </button>

            <button
              onClick={handleAddToWishlist}
              disabled={isAdding || addedStatus === 'wishlist'}
              className="btn btn-secondary btn-sm"
            >
              {addedStatus === 'wishlist' ? 'In Wishlist' :
                isAdding ? 'Adding...' : 'Wishlist'}
            </button>
          </div>
        </div>
      </div>
      {showEditModal && gameData.game && (
        <EditGameModal
          game={game}
          onSave={handleEditSave}
          onCancel={handleCloseModal}
        />
      )}
    </>
  );
};

export default SearchResultCard;
