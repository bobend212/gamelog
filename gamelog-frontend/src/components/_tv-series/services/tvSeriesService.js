import axios from "axios";

const API_BASE = "http://localhost:8080/api/tv-show";

const tvSeriesService = {
    saveSeries: async (tmdbId, status) => {
        try {
            const response = await axios.post(`${API_BASE}/save/${tmdbId}?status=${status}`);
            return response.data;
        } catch (error) {
            throw new Error("Failed to add series");
        }
    },

    updateTrackingType: async (seriesId, trackingType) => {
        try {
            await axios.patch(`${API_BASE}/${seriesId}/trackingType?trackingType=${trackingType}`);
        } catch (error) {
            throw new Error("Failed to update series status");
        }
    },

    rateSeason: async (seasonId, rating) => {
        try {
            let url = `${API_BASE}/${seasonId}/rate`;
            if (rating !== null && rating !== undefined) {
                url += `?rating=${rating}`;
            }
            await axios.patch(url);
        } catch (error) {
            throw new Error("Failed to set rating for season");
        }
    },


    incrementWatchedCount: async (seasonId, count) => {
        try {
            await axios.patch(`${API_BASE}/seasons/${seasonId}/watched?count=${encodeURIComponent(count)}`);
        } catch (error) {
            throw new Error("Failed to increment watched count");
        }
    },

    incrementWatched: async (seasonId) => {
        try {
            await axios.patch(`${API_BASE}/seasons/${seasonId}/watched/increment`);
        } catch (error) {
            throw new Error("Failed to increment watched count");
        }
    },

    getAllSeries: async () => {
        try {
            const response = await axios.get(API_BASE);
            return response.data;
        } catch (error) {
            throw new Error("Failed to fetch tracked series");
        }
    },

    getSeriesById: async (seriesId) => {
        try {
            const response = await axios.get(`${API_BASE}/${seriesId}`);
            return response.data;
        } catch (error) {
            throw new Error("Failed to fetch tracked series");
        }
    },

    searchSeries: async (query) => {
        try {
            const response = await axios.get(`${API_BASE}/search?query=${encodeURIComponent(query)}`);
            return response.data;
        } catch (error) {
            throw new Error("Failed to search series");
        }
    },

    deleteSeries: async (seriesId) => {
        try {
            await axios.delete(`${API_BASE}/${seriesId}`);
        } catch (error) {
            throw new Error('Failed to delete series');
        }
    },

    getAllSeriesByTrackingType: async (trackingType) => {
        try {
            const response = await axios.get(`${API_BASE}/filter?trackingType=${trackingType}`);
            return response.data;
        } catch (error) {
            throw new Error("Failed to fetch tracked series");
        }
    },

    syncSeries: async (seriesId) => {
        try {
            const response = await axios.patch(`${API_BASE}/sync-library/${seriesId}`);
            return response.data;
        } catch (error) {
            throw new Error("Failed to sync library");
        }
    },

};

export default tvSeriesService;
