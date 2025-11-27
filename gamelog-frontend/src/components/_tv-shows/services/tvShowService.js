import axios from "axios";

const API_BASE = "http://localhost:8080/api/tv-shows";

const tvShowService = {
    saveTVShow: async (tmdbId, trackingType) => {
        try {
            const response = await axios.post(`${API_BASE}/${tmdbId}?trackingType=${trackingType}`);
            return response.data;
        } catch (error) {
            throw new Error("Failed to add TV Show");
        }
    },

    updateTrackingType: async (tvShowId, trackingType) => {
        try {
            await axios.patch(`${API_BASE}/${tvShowId}?trackingType=${trackingType}`);
        } catch (error) {
            throw new Error("Failed to update TV Show tracking type");
        }
    },

    rateSeason: async (seasonId, rating) => {
        try {
            let url = `${API_BASE}/season/${seasonId}/rate`;
            if (rating !== null && rating !== undefined) {
                url += `?rating=${rating}`;
            }
            await axios.patch(url);
        } catch (error) {
            throw new Error("Failed to set rating for season");
        }
    },

    setWatchedCount: async (seasonId, count) => {
        try {
            let url = `${API_BASE}/season/${seasonId}/watched`;
            if (count !== null && count !== undefined) {
                url += `?count=${count}`;
            }
            await axios.patch(url);
        } catch (error) {
            throw new Error("Failed to increment watched count");
        }
    },

    getAllTVShows: async (page = 0, size = 10, search = "", trackingType = "WATCHING") => {
        try {
            const response = await axios.get(`${API_BASE}`, {
                params: {
                    page,
                    size,
                    search: search,
                    trackingType: trackingType
                }
            });
            return response.data;
        } catch (error) {
            throw new Error('Failed to fetch movies');
        }
    },

    getTVShowById: async (tvShowId) => {
        try {
            const response = await axios.get(`${API_BASE}/${tvShowId}`);
            return response.data;
        } catch (error) {
            throw new Error("Failed to fetch tracked TV Show");
        }
    },

    searchTVShow: async (query) => {
        try {
            const response = await axios.get(`${API_BASE}/search?query=${encodeURIComponent(query)}`);
            return response.data;
        } catch (error) {
            throw new Error("Failed to search TV Show");
        }
    },

    deleteTVShow: async (tvShowId) => {
        try {
            await axios.delete(`${API_BASE}/${tvShowId}`);
        } catch (error) {
            throw new Error('Failed to delete TV Show');
        }
    },

    syncTVShows: async (trackingType) => {
        try {
            const response = await axios.patch(
                `http://localhost:8080/api/sync/tv-shows`,
                null,
                {
                    params: { trackingType: trackingType },
                }
            );
            return response.data;
        } catch (error) {
            throw new Error("Failed to sync library");
        }
    }
};

export default tvShowService;
