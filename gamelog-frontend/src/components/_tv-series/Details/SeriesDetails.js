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
import { LinearProgress } from "@mui/material";
import CloudSyncIcon from '@mui/icons-material/CloudSync';
import EditIcon from '@mui/icons-material/Edit';
import { toast } from 'react-toastify';
import { Rating } from '@mui/material';

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

    const handleSetWatchedCount = async (seriesId, seasonId) => {
        const input = window.prompt("Enter number of watched episodes:");
        if (input !== null) {
            const count = parseInt(input, 10);
            if (!isNaN(count) && count >= 0) {
                await tvSeriesService.incrementWatchedCount(seasonId, count);
                await loadDetails(seriesId);
            } else {
                alert("Please enter a valid non-negative number.");
            }
        }
    };

    const handleSync = async (seriesId) => {
        await tvSeriesService.syncSeries(seriesId);
        await loadDetails(seriesId);
        toast.success(`Sync completed`);
    };

    const handleSaveStatus = async (newStatus) => {
        if (series) {
            await tvSeriesService.updateTrackingType(series.id, newStatus);
            setDialogOpen(false);
            await loadDetails(series.id);
        }
    };

    const handleRatingChange = async (seasonId, newValue) => {
        try {
            await tvSeriesService.rateSeason(seasonId, newValue);
            toast.success('Rating updated');
            await loadDetails(series.id);
        } catch (error) {
            toast.error('Failed to update rating');
        }
    };


    if (loading) return <Typography align="center" sx={{ mt: 6 }}>Loading...</Typography>;
    if (!series) return <Typography align="center" sx={{ mt: 6 }}>Series not found.</Typography>;

    const info = TRACKING_TYPES[series.trackingType];
    const imgUrl = "https://image.tmdb.org/t/p/w200" + series.poster_path;

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

                    <Box sx={{ minWidth: 200 }}>
                        <CardMedia
                            component="img"
                            image={imgUrl}
                            alt={series.name}
                            sx={{ height: 280, width: 200, objectFit: "cover", borderRadius: 2, boxShadow: 3, mb: 2 }}
                        />

                        <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 0.5 }}>
                            Release Date
                        </Typography>
                        <Typography variant="body2" color="#cfd8dc" sx={{ mb: 2 }}>
                            ✦ {series.first_air_date}
                        </Typography>
                        <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 0.5 }}>
                            Status
                        </Typography>
                        <Typography variant="body2" color="#cfd8dc" sx={{ mb: 2 }}>
                            ✦ {series.status}
                        </Typography>
                        <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 0.5 }}>
                            Last Episode Air Date
                        </Typography>
                        <Typography variant="body2" color="#cfd8dc" sx={{ mb: 2 }}>
                            ✦ {series.last_air_date}
                        </Typography>

                        {series.ratingOverall !== null && series.ratingOverall !== 0 && (
                            <>
                                <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 0.5 }}>
                                    Overall Rating
                                </Typography>
                                <Typography component="div" variant="body2" color="#cfd8dc" sx={{ mb: 2 }}>
                                    <Box sx={{ mr: 1, color: "white", fontWeight: 700 }}>
                                        ✦ {series.ratingOverall}
                                    </Box>
                                </Typography>
                            </>
                        )}
                        
                    </Box>

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
                                sx={{ textTransform: "none" }}
                                endIcon={<EditIcon />}
                            >
                                EDIT
                            </Button>
                            <Button
                                variant="contained"
                                onClick={() => handleSync(series.id)}
                                sx={{ textTransform: "none" }}
                                color="success"
                                endIcon={<CloudSyncIcon />}
                            >
                                SYNC
                            </Button>
                        </Stack>

                        <Box sx={{ mb: 0, mt: 1, p: 1, bgcolor: '#374579ff', borderRadius: 1 }}>
                            <Typography variant="h6" sx={{ mb: 1 }}>
                                Progress: {series.totalWatchedEpisodes} / {series.number_of_episodes} episodes
                                ({series.percentageProgress}%)
                            </Typography>
                            <LinearProgress
                                variant="determinate"
                                value={series.percentageProgress}
                                sx={{
                                    height: 12,
                                    borderRadius: 2,
                                    backgroundColor: '#eee',
                                    '& .MuiLinearProgress-bar': {
                                        borderRadius: 2,
                                        backgroundColor: series.percentageProgress === 100 ? 'green' : '#689B8A',
                                        // transition: 'width 0.5s ease-in-out',
                                    }
                                }}
                            />
                        </Box>

                        <Divider sx={{ my: 1, bgcolor: "#334" }} />
                        <Typography variant="h6" sx={{ mb: 1 }}>
                            Seasons ({series.number_of_seasons})
                        </Typography>
                        <Stack spacing={3}>
                            {series.seasons.map((season, idx) => (
                                <Paper key={season.id} elevation={2} sx={{ p: 2, bgcolor: "#313649ff" }}>
                                    <Box sx={{ width: 200, display: 'flex', alignItems: 'center', mb: 0.5 }}>
                                        {season.rating && (<Box sx={{ mr: 1, color: "white", fontWeight: 700 }}>{season.rating}</Box>)}
                                        <Rating
                                            name={`season-rating-${season.id}`}
                                            value={season.rating || null}
                                            max={10}
                                            precision={1}
                                            onChange={(event, newValue) => {
                                                handleRatingChange(season.id, newValue);
                                            }}
                                            onChangeActive={(event, newHover) => {
                                                if (newHover === 0) {
                                                    handleRatingChange(season.id, null);
                                                }
                                            }}
                                        />
                                    </Box>
                                    <Typography sx={{ fontWeight: 600, mb: 0, color: "white" }}>
                                        {season.name || `Season ${season.seasonNumber}`} - {season.air_date}
                                    </Typography>
                                    <Typography variant="body2" color="#aad6ff">
                                        {season.watchedCount} / {season.episode_count} episodes watched
                                    </Typography>
                                    <Stack mt={1} direction="row" spacing={1}>
                                        <Button
                                            size="small"
                                            variant="outlined"
                                            onClick={() => handleSetWatchedCount(series.id, season.id)}
                                            sx={{ mr: 1 }}
                                        >
                                            Set Watched
                                        </Button>
                                        {season.watchedCount < season.episode_count && (
                                            <>
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
                                            </>
                                        )}
                                    </Stack>
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
