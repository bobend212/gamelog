import React, { useState } from 'react';
import gameService from '../../services/gameService';
import EditGameModal from './EditGameModal';
import './GameCard.css';
import { toast } from 'react-toastify';

const GameCard = ({ game, onUpdate, showStatus = true }) => {
  const [isUpdating, setIsUpdating] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);

  const handleDelete = async () => {
    // if (window.confirm(`Are you want to remove "${game.title}" from the Library?`)) {
    try {
      setIsUpdating(true);
      await gameService.deleteGame(game.id);
      onUpdate();
      toast.info(`"${game.title}" removed from database! 🟣`);
    } catch (error) {
      console.error('Failed to delete game:', error);
      alert('Failed to delete game. Please try again.');
    } finally {
      setIsUpdating(false);
    }
    // }
  };

  const handleEdit = () => {
    setShowEditModal(true);
  };

  const handleEditSave = async (updatedGame) => {
    try {
      setIsUpdating(true);
      await gameService.updateGame(game.id, updatedGame);
      setShowEditModal(false);
      onUpdate();
    } catch (error) {
      console.error('Failed to update game:', error);
      alert('Failed to update game. Please try again.');
    } finally {
      setIsUpdating(false);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'PLAYING': return '#8b5cf6';
      case 'COMPLETED': return '#10b981';
      case 'BACKLOG': return '#f59e0b';
      case 'WISHLIST': return '#ef4444';
      case 'DROPPED': return '#6b7280';
      default: return '#6b7280';
    }
  };

  const getStatusLabel = (status) => {
    switch (status) {
      case 'PLAYING': return 'Playing';
      case 'COMPLETED': return 'Completed';
      case 'BACKLOG': return 'Backlog';
      case 'WISHLIST': return 'Wishlist';
      case 'DROPPED': return 'Dropped';
      default: return 'Unknown';
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

  const getRatingColor = (rating) => {
    if (rating >= 4.5) return '#10b981'; // Green for excellent
    if (rating >= 4.0) return '#3b82f6'; // Blue for very good
    if (rating >= 3.5) return '#f59e0b'; // Yellow for good
    if (rating >= 3.0) return '#ef4444'; // Red for average
    return '#6b7280'; // Gray for below average
  };

  return (
    <>
      <div className="game-card">
        <div className="game-image">
          {game.imageUrl ? (
            <img src={game.imageUrl} alt={game.title} />
          ) : (
            <div className="no-image">🎮</div>
          )}

          {game.rating && (
            <div
              className="rating-badge"
              style={{ backgroundColor: getRatingColor(game.rating) }}
            >
              <span className="rating-icon">★</span>
              <span className="rating-text">{game.rating.toFixed(1)}</span>
            </div>
          )}

          {showStatus && (
            <div
              className="status-badge"
              style={{ backgroundColor: getStatusColor(game.status) }}
            >
              {getStatusLabel(game.status)}
            </div>
          )}
        </div>

        <div className="game-content">
          <h3 className="game-title">{game.title}</h3>

          <div className="game-meta">
            <p className="game-release">Release: {formatDate(game.releaseDate)}</p>
            <br />
            {game.completedAt && (
              <p className="game-release">
                Completed: {formatDate(game.completedAt)}
                {game.playedOn && ` • ${game.playedOn}`}
              </p>
            )}
            {game.playedOn && !game.completedAt && (
              <p className="game-release">Played on: {game.playedOn}</p>
            )}
          </div>

          <div className="game-details">
            {game.notes && (
              <div className="game-meta">
                <p className="game-notes">{game.notes}</p>
              </div>
            )}
          </div>

          <div className="game-actions">
            <button
              onClick={handleEdit}
              disabled={isUpdating}
              className="btn btn-compact btn-edit"
              title="Edit game details"
            >
              <span className="icon">✏️</span>
            </button>

            <button
              onClick={handleDelete}
              disabled={isUpdating}
              className="btn btn-compact btn-delete"
              title="Remove from library"
            >
              <span className="icon">🗑️</span>
            </button>
          </div>
        </div>
      </div>

      {showEditModal && (
        <EditGameModal
          game={game}
          onSave={handleEditSave}
          onCancel={() => setShowEditModal(false)}
        />
      )}
    </>
  );
}

export default GameCard;
