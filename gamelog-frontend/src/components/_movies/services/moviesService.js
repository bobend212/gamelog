import axios from "axios";

const API_BASE = "http://localhost:8080/api/movies";

const moviesService = {
    saveMovie: async (tmdbId) => {
        try {
            const response = await axios.post(`${API_BASE}/save/${tmdbId}`);
            return response.data;
        } catch (error) {
            throw new Error("Failed to add movie");
        }
    },

    getAllMovies: async () => {
        try {
            const response = await axios.get(API_BASE);
            return response.data;
        } catch (error) {
            throw new Error("Failed to fetch tracked movies");
        }
    },

    getAllMoviesWithPagination: async (page = 0, size = 10) => {
        try {
            const response = await axios.get(`${API_BASE}/pageable`, {
                params: {
                    page,
                    size
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
};

export default moviesService;