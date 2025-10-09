import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api";

const gameService = {
  // [RAWG API] Add game to library by rawgId and status
  saveGame: async (rawgId, gameStatus) => {
    try {
      const response = await axios.post(
        `${API_BASE_URL}/games/add/${rawgId}`,
        null,
        {
          params: { gameStatus },
        }
      );
      return response.data;
    } catch (error) {
      throw new Error("Failed to add game to library");
    }
  },

  // Get all library games count
  getAllLibraryGames: async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/games/library`, {
        params: { page: 0, size: 1000 },
      });
      return response.data.content || [];
    } catch (error) {
      throw new Error("Failed to fetch all library games");
    }
  },

  // Get all library games
  getLibraryGames: async (
    page = 0,
    size = 8,
    status = "ALL",
    searchTerm = ""
  ) => {
    try {
      const response = await axios.get(`${API_BASE_URL}/games/library`, {
        params: {
          page,
          size,
          status: status !== "ALL" ? status : "ALL",
          search: searchTerm,
        },
      });
      return response.data;
    } catch (error) {
      throw new Error("Failed to fetch library games");
    }
  },

  // Get all wishlist games count
  getAllWishlistGames: async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/games/wishlist`, {
        params: { page: 0, size: 1000 },
      });
      return response.data.content || [];
    } catch (error) {
      throw new Error("Failed to fetch all wishlist games");
    }
  },

  // Get all wishlist games
  getWishlistGames: async (page = 0, size = 8, search = "") => {
    const response = await axios.get(`${API_BASE_URL}/games/wishlist`, {
      params: { page, size, search },
    });
    return response.data;
  },

  getWishlistGamesDashboard: async (page, size, sort, release = "ALL") => {
    const response = await axios.get(
      `${API_BASE_URL}/games/wishlist/dashboard`,
      {
        params: { page, size, sort, release },
      }
    );
    return response.data;
  },

  // Update game
  updateGame: async (gameId, updateData) => {
    try {
      const requestBody = {
        platform: updateData.platform || null,
        status: updateData.status,
        rating: updateData.rating || null,
        notes: updateData.notes || null,
        completedAt: updateData.completedAt || null,
        favourite: updateData.favourite || false,
      };
      const response = await axios.put(
        `${API_BASE_URL}/games/${gameId}`,
        requestBody
      );
      return response.data;
    } catch (error) {
      throw new Error("Failed to update game");
    }
  },

  // Delete game from Library
  deleteGame: async (gameId) => {
    try {
      await axios.delete(`${API_BASE_URL}/games/${gameId}`);
    } catch (error) {
      throw new Error("Failed to delete game");
    }
  },

  // [RAWG API] Search games
  searchGames: async (query) => {
    try {
      const response = await axios.get(
        `${API_BASE_URL}/games/search?query=${encodeURIComponent(query)}`
      );
      return response.data;
    } catch (error) {
      throw new Error("Failed to search games");
    }
  },

  // [RAWG API] Sync library
  syncLibraryGames: async (status) => {
    try {
      const response = await axios.patch(
        `${API_BASE_URL}/games/sync-library`,
        null,
        {
          params: { status },
        }
      );
      return response.data; // return full response containing change details
    } catch (error) {
      throw new Error("Failed to sync library");
    }
  },
};

export default gameService;
