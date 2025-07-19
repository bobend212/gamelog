import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const gameService = {

  // [RAWG API] Get by rawgId and add game to library
  addToLibrary: async (rawgId) => {
    try {
      const response = await axios.get(`${API_BASE_URL}/games/add-library/${rawgId}`);
      return response.data;
    } catch (error) {
      throw new Error('Failed to add game to library');
    }
  },

  // [RAWG API] Get by rawgId and add game to wishlist
  addToWishlist: async (rawgId) => {
    try {
      const response = await axios.get(`${API_BASE_URL}/games/add-wishlist/${rawgId}`);
      return response.data;
    } catch (error) {
      throw new Error('Failed to add game to wishlist');
    }
  },

  // Get all library games (without wishlist)
  // getLibraryGames: async () => {
  //   try {
  //     const response = await axios.get(`${API_BASE_URL}/games/all-games`);
  //     return response.data;
  //   } catch (error) {
  //     throw new Error('Failed to fetch library games');
  //   }
  // },

  // Get all library games number
  getAllLibraryGames: async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/games/all-games`, {
        params: { page: 0, size: 1000 }
      });
      return response.data.content || [];
    } catch (error) {
      throw new Error('Failed to fetch all library games');
    }
  },

  // getLibraryGames: async (page = 0, size = 6) => {
  //   try {
  //     const response = await axios.get(`${API_BASE_URL}/games/all-games`, {
  //       params: { page, size }
  //     });
  //     return response.data;
  //   } catch (error) {
  //     throw new Error('Failed to fetch library games');
  //   }
  // },

  getLibraryGames: async (page = 0, size = 8, status = 'ALL', searchTerm = '') => {
    try {
      const response = await axios.get(`${API_BASE_URL}/games/all-games`, {
        params: {
          page,
          size,
          status: status !== 'ALL' ? status : 'ALL',
          search: searchTerm
        }
      });
      return response.data;
    } catch (error) {
      throw new Error('Failed to fetch library games');
    }
  },

  // Get all wishlist games number
  getAllWishlistGames: async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/games/wishlist`, {
        params: { page: 0, size: 1000 }
      });
      return response.data.content || [];
    } catch (error) {
      throw new Error('Failed to fetch all wishlist games');
    }
  },

  // Get all wishlist games
  getWishlistGames: async (page = 0, size = 8) => {
    try {
      const response = await axios.get(`${API_BASE_URL}/games/wishlist`, {
        params: { page, size }
      });
      return response.data;
    } catch (error) {
      throw new Error('Failed to fetch wishlist games');
    }
  },

  // Update game
  updateGame: async (gameId, updateData) => {
    try {
      const requestBody = {
        playedOn: updateData.playedOn || null,
        status: updateData.status,
        rating: updateData.rating || null,
        notes: updateData.notes || null,
        completedAt: updateData.completedAt || null
      };

      const response = await axios.put(`${API_BASE_URL}/games/${gameId}`, requestBody);
      return response.data;
    } catch (error) {
      throw new Error('Failed to update game');
    }
  },

  // Delete game from Library
  deleteGame: async (gameId) => {
    try {
      await axios.delete(`${API_BASE_URL}/games/${gameId}`);
    } catch (error) {
      throw new Error('Failed to delete game');
    }
  },

  // [RAWG API] Search games
  searchGames: async (query) => {
    try {
      const response = await axios.get(`${API_BASE_URL}/games/search?query=${encodeURIComponent(query)}`);
      return response.data;
    } catch (error) {
      throw new Error('Failed to search games');
    }
  },

  // [RAWG API] Search games (advanced with pagination)
  searchGamesWithPagination: async (query, page = 1, pageSize = 20) => {
    try {
      const response = await axios.get(`${API_BASE_URL}/games/search`, {
        params: {
          query: query,
          page: page,
          pageSize: pageSize
        }
      });
      return response.data;
    } catch (error) {
      throw new Error('Failed to search games');
    }
  }

};

export default gameService;
