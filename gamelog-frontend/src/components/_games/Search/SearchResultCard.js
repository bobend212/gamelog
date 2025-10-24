import React, { useState } from "react";
import gameService from "../services/gameService";
import { toast } from "react-toastify";
import EditGameModal from "../Library/EditGameModal";

const SearchResultCard = ({ game, onGameAdded }) => {
  const [isAdding, setIsAdding] = useState(false);
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

  const handleSaveGame = async (gameStatus) => {
    try {
      const result = await gameService.saveGame(game.rawgId, gameStatus);
      setIsAdding(true);
      if (gameStatus === "WISHLIST") {
        toast.success(`"${game.title}" saved to Wishlist! 🟣`);
      } else {
        toast.success(`"${game.title}" saved to Backlog! 🟢`);
        openEditModalFor(result);
      }
    } catch (error) {
      if (error.status === 409) {
        handleGameAlreadyExists(error.message);
      } else {
        alert("Game save failed");
      }
    } finally {
      setIsAdding(false);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return "TBA";
    try {
      const dateObj = new Date(dateString);
      const options = { day: "2-digit", month: "long", year: "numeric" };
      return dateObj.toLocaleDateString("en-GB", options);
    } catch (error) {
      return "TBA";
    }
  };

  const handleEditSave = async (updatedGame) => {
    try {
      await gameService.updateGame(gameData.id, updatedGame);
      setShowEditModal(false);
      toast.success(`"${game.title}" saved to Library! 🟢`);
      onGameAdded();
    } catch (error) {
      console.error("Failed to update game:", error);
      alert("Failed to update game. Please try again.");
    }
  };

  const handleGameAlreadyExists = (msg) => {
    toast.warning(`${msg}`, {
      icon: "⚠️",
      autoClose: 3000,
      position: "bottom-right",
      theme: "dark",
    });
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
        </div>

        <div className="game-content">
          <h3 className="game-title">{game.title}</h3>

          <div className="game-meta">
            <p className="release-date">{formatDate(game.releaseDate)}</p>
            {game.rating && (
              <div className="rating">⭐ {game.rating.toFixed(1)}</div>
            )}
          </div>

          <div className="action-buttons">
            <button
              onClick={() => handleSaveGame("BACKLOG")}
              disabled={isAdding}
              className="btn btn-primary btn-sm"
            >
              {"Library"}
            </button>

            <button
              onClick={() => handleSaveGame("WISHLIST")}
              disabled={isAdding}
              className="btn btn-secondary btn-sm"
            >
              {"Wishlist"}
            </button>
          </div>
        </div>
      </div>
      {showEditModal && gameData && (
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
