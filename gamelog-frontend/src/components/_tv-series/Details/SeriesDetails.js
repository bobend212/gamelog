import { useEffect, useState } from "react";
import {
    Box,
    Typography,
    Chip,
    Button,
    Stack,
    CardMedia,
    Paper,
    Divider
} from "@mui/material";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import { useNavigate, useParams } from "react-router-dom";
import tvSeriesService from "../../_tv-series/services/tvSeriesService";
import StatusDialog from "../Common/StatusDialog";
import Navbar from "../Navigation/Navbar";
import { TRACKING_TYPES } from "../utils/constants";

const FALLBACK_POSTER =
    "https://image.tmdb.org/t/p/original/ipNCnwKaRqyddXRukTslsl3hiop.jpg";
const MAIN_PATH_POSTER = "https://image.tmdb.org/t/p/w200";

const SeriesDetails = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [series, setSeries] = useState(null);
    const [loading, setLoading] = useState(true);
    const [dialogOpen, setDialogOpen] = useState(false);

    useEffect(() => {
        loadDetails(id);
    }, [id]);

    const loadDetails = async (id) => {
        setLoading(true);
        try {
            const series = await tvSeriesService.getSeriesById(id);
            setSeries(series);
        } finally {
            setLoading(false);
        }
    };

    const handleIncrement = async (seriesId, seasonId) => {
        await tvSeriesService.incrementWatched(seasonId);
        await loadDetails(seriesId);
    };

    const handleAllWatched = async (seriesId, seasonId, count) => {
        await tvSeriesService.incrementWatchedCount(seasonId, count);
        await loadDetails(seriesId);
    };

    const handleSaveStatus = async (newStatus) => {
        if (series) {
            await tvSeriesService.updateTrackingType(series.id, newStatus);
            setDialogOpen(false);
            await loadDetails(series.id);
        }
    };

    if (loading) return <Typography align="center" sx={{ mt: 6 }}>Loading...</Typography>;
    if (!series) return <Typography align="center" sx={{ mt: 6 }}>Series not found.</Typography>;

    const info = TRACKING_TYPES[series.trackingType];
    const imgUrl = series.poster_path ? MAIN_PATH_POSTER + series.poster_path : FALLBACK_POSTER;

    return (
        <>
            <Navbar />
            <Box
                sx={{
                    maxWidth: 800,
                    mx: "auto",
                    mt: 4,
                    p: 3,
                    bgcolor: "#2d313bff",
                    minHeight: "100vh",
                    color: "#e0e0e0"
                }}
            >
                <Button
                    variant="outlined"
                    startIcon={<ArrowBackIcon />}
                    sx={{ mb: 2 }}
                    onClick={() => navigate(-1)}
                >
                    Back to Dashboard
                </Button>
                <Stack direction={{ xs: "column", sm: "row" }} spacing={3} alignItems="flex-start">

                    {/* Left Side - Poster + new fields below it */}
                    <Box sx={{ minWidth: 200 }}>
                        <CardMedia
                            component="img"
                            image={imgUrl}
                            alt={series.name}
                            sx={{ height: 280, width: 200, objectFit: "cover", borderRadius: 2, boxShadow: 3, mb: 2 }}
                        />

                        {/* New fields directly under poster */}
                        <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 0.5 }}>
                            Release Date
                        </Typography>
                        <Typography variant="body2" color="#cfd8dc" sx={{ mb: 2 }}>
                            {series.first_air_date}
                        </Typography>
                        <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 0.5 }}>
                            Status
                        </Typography>
                        <Typography variant="body2" color="#cfd8dc" sx={{ mb: 2 }}>
                            {series.status}
                        </Typography>

                    </Box>

                    {/* Right Side - Title, status, seasons */}
                    <Box flex={1}>
                        <Typography variant="h5" fontWeight="bold" sx={{ mb: 1 }}>
                            {series.name}
                        </Typography>
                        <Stack direction="row" alignItems="center" spacing={1} sx={{ mb: 2 }}>
                            <Chip
                                icon={info.icon}
                                label={info.label}
                                color={info.color}
                                size="medium"
                                sx={{ fontWeight: 700, fontSize: 15, px: 2 }}
                            />
                            <Button
                                variant="outlined"
                                onClick={() => setDialogOpen(true)}
                                sx={{ textTransform: "none", fontWeight: 600 }}
                            >
                                Edit
                            </Button>
                        </Stack>

                        <Divider sx={{ my: 2, bgcolor: "#334" }} />
                        <Typography variant="h6" sx={{ mb: 1 }}>
                            Seasons
                        </Typography>
                        <Stack spacing={3}>
                            {series.seasons.map((season, idx) => (
                                <Paper key={season.id} elevation={2} sx={{ p: 2, bgcolor: "#313649ff" }}>
                                    <Typography sx={{ fontWeight: 600, mb: 0, color: "white" }}>
                                        {season.name || `Season ${season.seasonNumber}`} - {season.air_date}
                                    </Typography>
                                    <Typography variant="body2" color="#aad6ff">
                                        {season.watchedCount} / {season.episode_count} episodes watched
                                    </Typography>
                                    {season.watchedCount < season.episode_count && (
                                        <Stack mt={1} direction="row" spacing={1}>
                                            <Button
                                                variant="outlined"
                                                color="primary"
                                                size="small"
                                                onClick={() => handleAllWatched(series.id, season.id, season.episode_count)}
                                            >
                                                All Watched
                                            </Button>
                                            <Button
                                                variant="contained"
                                                color="success"
                                                size="small"
                                                onClick={() => handleIncrement(series.id, season.id)}
                                            >
                                                +1 Watched
                                            </Button>
                                        </Stack>
                                    )}
                                </Paper>
                            ))}
                        </Stack>
                    </Box>
                </Stack>

                <StatusDialog
                    open={dialogOpen}
                    onClose={() => setDialogOpen(false)}
                    onSave={handleSaveStatus}
                    currentStatus={series.trackingType}
                />
            </Box>
        </>
    );
};

export default SeriesDetails;
