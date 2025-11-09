import axios from "axios";

const API_BASE = "http://localhost:8080/api/movies";

const moviesService = {
    saveMovie: async (tmdbId) => {
        try {
            const response = await axios.post(`${API_BASE}/${tmdbId}`);
            return response.data;
        } catch (error) {
            throw new Error("Failed to add movie");
        }
    },

    getAllMovies: async (page = 0, size = 10, search = "") => {
        try {
            const response = await axios.get(`${API_BASE}`, {
                params: {
                    page,
                    size,
                    search: search
                }
            });
            return response.data;
        } catch (error) {
            throw new Error('Failed to fetch movies');
        }
    },

    getMovieById: async (movieId) => {
        try {
            const response = await axios.get(`${API_BASE}/${movieId}`);
            return response.data;
        } catch (error) {
            throw new Error("Failed to fetch tracked movie");
        }
    },

    deleteMovie: async (movieId) => {
        try {
            await axios.delete(`${API_BASE}/${movieId}`);
        } catch (error) {
            throw new Error('Failed to delete movie');
        }
    },

    searchMovies: async (query) => {
        try {
            const response = await axios.get(`${API_BASE}/search?query=${encodeURIComponent(query)}`);
            return response.data;
        } catch (error) {
            throw new Error("Failed to search movies");
        }
    },

    syncMovies: async () => {
        try {
            const response = await axios.patch(`http://localhost:8080/api/sync/movies`);
            return response.data;
        } catch (error) {
            throw new Error("Failed to sync library");
        }
    },

    syncMovieById: async (movieId) => {
        try {
            const response = await axios.patch(`http://localhost:8080/api/sync/movies/${movieId}`);
            return response.data;
        } catch (error) {
            throw new Error("Failed to sync library");
        }
    },
};

export default moviesService;