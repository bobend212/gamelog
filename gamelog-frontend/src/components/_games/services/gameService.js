import axios from "axios";

const API_BASE_URL = process.env.REACT_APP_API_URL + "/api/games";

const gameService = {

  saveGame: async (rawgId, gameStatus) => {
    try {
      const response = await axios.post(
        `${API_BASE_URL}/${rawgId}`,
        null,
        {
          params: { gameStatus },
        }
      );
      return response.data;
    } catch (error) {
      const status = error.response?.status || 0;
      const msg = error.response?.data?.message || '';
      throw { message: msg, status };
    }
  },

  getGames: async (
    page = 0,
    size = 8,
    status = "ALL",
    searchTerm = ""
  ) => {
    try {
      const response = await axios.get(`${API_BASE_URL}`, {
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

  getWishlistGames: async (page = 0, size = 8, search = "") => {
    const response = await axios.get(`${API_BASE_URL}/wishlist`, {
      params: { page, size, search },
    });
    return response.data;
  },

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
      const response = await axios.patch(
        `${API_BASE_URL}/${gameId}`,
        requestBody
      );
      return response.data;
    } catch (error) {
      throw new Error("Failed to update game");
    }
  },

  deleteGame: async (gameId) => {
    try {
      await axios.delete(`${API_BASE_URL}/${gameId}`);
    } catch (error) {
      throw new Error("Failed to delete game");
    }
  },

  searchGames: async (query) => {
    try {
      const response = await axios.get(
        `${API_BASE_URL}/search?query=${encodeURIComponent(query)}`
      );
      return response.data;
    } catch (error) {
      throw new Error("Failed to search games");
    }
  },

  syncLibraryGames: async (status) => {
    try {
      const response = await axios.patch(
        `http://localhost:8080/api/sync/games`,
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
