import React, { useEffect, useState } from "react";
import {
    Box,
    Typography,
    Chip,
    Button,
    Stack,
    CardMedia,
    Paper,
    Divider,
    Avatar,
    LinearProgress,
    Rating
} from "@mui/material";
import { useParams } from "react-router-dom";
import tvShowService from "../services/tvShowService";
import UpdateTrackingTypeDialog from "../Common/UpdateTrackingTypeDialog";
import Navbar from "../Navigation/Navbar";
import EditIcon from '@mui/icons-material/Edit';
import { toast } from 'react-toastify';
import { parseDate, VOD_PROVIDER_PATH_BASE_W45, POSTER_PATH_BASE_W200, TRACKING_TYPES } from '../utils/TVShowUtil';

// --- Sub-components ---

const TVShowSidebar = ({ tvShow }) => (
    <Box sx={{ minWidth: 200 }}>
        <CardMedia
            component="img"
            image={POSTER_PATH_BASE_W200 + tvShow.posterPath}
            alt={tvShow.name}
            sx={{ height: 280, width: 200, objectFit: "cover", borderRadius: 2, boxShadow: 3, mb: 2 }}
        />

        <DetailSection title="Release Date" content={`✦ ${tvShow.firstAirDate}`} />
        <DetailSection title="Status" content={`✦ ${tvShow.status}`} />
        <DetailSection title="Last Episode Air Date" content={`✦ ${parseDate(tvShow.lastAirDate)}`} />

        {tvShow.ratingOverall !== null && tvShow.ratingOverall !== 0 && (
            <>
                <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 0.5 }}>
                    Overall Rating
                </Typography>
                <Typography component="div" variant="body2" color="#cfd8dc" sx={{ mb: 2 }}>
                    <Box sx={{ mr: 1, color: "white", fontWeight: 700 }}>
                        ✦ {tvShow.ratingOverall}
                    </Box>
                </Typography>
            </>
        )}

        {tvShow.nextEpisode !== null && (
            <DetailSection title="Next Episode" content={`✦ ${tvShow.nextEpisode}`} />
        )}

        <Typography variant="caption" sx={{ textAlign: 'left' }}>
            <p>Last Modified</p>
            <p>{parseDate(tvShow.updatedAt)}</p>
        </Typography>
    </Box>
);

const DetailSection = ({ title, content }) => (
    <>
        <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 0.5 }}>
            {title}
        </Typography>
        <Typography variant="body2" color="#cfd8dc" sx={{ mb: 2 }}>
            {content}
        </Typography>
    </>
);

const SeasonCard = ({ season, tvShowId, onSetWatched, onAllWatched, onIncrement, onRate }) => (
    <Paper elevation={2} sx={{ p: 2, bgcolor: "#313649ff" }}>
        <Box sx={{ width: 200, display: 'flex', alignItems: 'center', mb: 0.5 }}>
            {season.rating != null && (
                <Box
                    sx={{
                        mr: 1, px: 1.2, py: 0.3, bgcolor: 'goldenrod', borderRadius: 2,
                        color: 'black', fontWeight: 600, textAlign: 'center', userSelect: 'none', fontSize: '0.9rem'
                    }}
                    aria-label={`Current rating: ${season.rating}`}
                >
                    {season.rating}
                </Box>
            )}
            <Rating
                name={`season-rating-${season.id}`}
                value={season.rating || null}
                max={10}
                precision={1}
                onChange={(event, newValue) => onRate(season.id, newValue)}
                onChangeActive={(event, newHover) => {
                    if (newHover === 0) onRate(season.id, null);
                }}
                sx={{ flexGrow: 1 }}
                size="medium"
            />
        </Box>

        <Typography sx={{ fontWeight: 600, mb: 0, color: "white" }}>
            {season.name || `Season ${season.seasonNumber}`} | {parseDate(season.airDate)}
        </Typography>
        <Typography variant="body2" color="#aad6ff">
            {season.watchedCount} / {season.episodeCount} episodes watched
        </Typography>

        <Stack mt={1} direction="row" spacing={1}>
            <Button
                size="small"
                variant="outlined"
                onClick={() => onSetWatched(tvShowId, season.id)}
                sx={{ mr: 1 }}
            >
                Set Watched
            </Button>
            {season.watchedCount < season.episodeCount && (
                <>
                    <Button
                        variant="outlined"
                        color="primary"
                        size="small"
                        onClick={() => onAllWatched(tvShowId, season.id, season.episodeCount)}
                    >
                        All
                    </Button>
                    <Button
                        variant="contained"
                        color="success"
                        size="small"
                        onClick={() => onIncrement(tvShowId, season.id, season.watchedCount)}
                    >
                        +1
                    </Button>
                </>
            )}
        </Stack>
    </Paper>
);

// --- Main Component ---

const TVShowDetails = () => {
    const { id } = useParams();
    const [tvShow, setTVShow] = useState(null);
    const [loading, setLoading] = useState(true);
    const [dialogOpen, setDialogOpen] = useState(false);

    useEffect(() => {
        loadDetails(id);
    }, [id]);

    const loadDetails = async (tvShowId) => {
        setLoading(true);
        try {
            const tvShow = await tvShowService.getTVShowById(tvShowId);
            setTVShow(tvShow);
        } finally {
            setLoading(false);
        }
    };

    const handleIncrement = async (tvShowId, seasonId, count) => {
        await tvShowService.setWatchedCount(seasonId, count + 1);
        await loadDetails(tvShowId);
    };

    const handleAllWatched = async (tvShowId, seasonId, count) => {
        await tvShowService.setWatchedCount(seasonId, count);
        await loadDetails(tvShowId);
    };

    const handleSetWatchedCount = async (tvShowId, seasonId) => {
        const input = window.prompt("Enter number of watched episodes:");
        if (input !== null) {
            const count = parseInt(input, 10);
            if (!isNaN(count) && count >= 0) {
                await tvShowService.setWatchedCount(seasonId, count);
                await loadDetails(tvShowId);
            } else {
                alert("Please enter a valid non-negative number.");
            }
        }
    };

    const handleUpdateTrackingType = async (trackingType) => {
        if (tvShow) {
            await tvShowService.updateTrackingType(tvShow.id, trackingType);
            setDialogOpen(false);
            await loadDetails(tvShow.id);
        }
    };

    const handleRateSeason = async (seasonId, newValue) => {
        try {
            await tvShowService.rateSeason(seasonId, newValue);
            toast.success('Rating updated');
            await loadDetails(tvShow.id);
        } catch (error) {
            toast.error('Failed to update rating');
        }
    };

    if (loading) return <Typography align="center" sx={{ mt: 6 }}>Loading...</Typography>;
    if (!tvShow) return <Typography align="center" sx={{ mt: 6 }}>TV Show not found.</Typography>;

    const trackingType = TRACKING_TYPES[tvShow.trackingType];

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
                <Stack direction={{ xs: "column", sm: "row" }} spacing={3} alignItems="flex-start">

                    <TVShowSidebar tvShow={tvShow} />

                    <Box flex={1}>
                        <Typography variant="h5" fontWeight="bold" sx={{ mb: 1 }}>
                            {tvShow.name}
                        </Typography>

                        <Stack direction="row" spacing={1} justifyContent="left" flexWrap="wrap" mb={1} >
                            {tvShow.vodProviders.map((provider) => (
                                <Avatar
                                    key={provider}
                                    alt={provider}
                                    src={`${VOD_PROVIDER_PATH_BASE_W45}${provider.split(';')[0]}`}
                                    sx={{ width: 45, height: 45 }}
                                    variant="circular"
                                />
                            ))}
                        </Stack>

                        <Stack direction="row" alignItems="center" spacing={1} sx={{ mb: 2 }}>
                            <Chip
                                icon={trackingType.icon}
                                label={trackingType.label}
                                color={trackingType.color}
                                size="medium"
                                deleteIcon={<EditIcon />}
                                onDelete={() => setDialogOpen(true)}
                                onClick={() => setDialogOpen(true)}
                                sx={{ fontWeight: 700, fontSize: 15, px: 2 }}
                            />
                        </Stack>

                        <Box sx={{ mb: 0, mt: 1, p: 1, bgcolor: '#374579ff', borderRadius: 1 }}>
                            <Typography variant="h6" sx={{ mb: 1 }}>
                                Progress: {tvShow.totalWatchedEpisodes} / {tvShow.numberOfEpisodes} episodes
                                ({tvShow.percentageProgress}%)
                            </Typography>
                            <LinearProgress
                                variant="determinate"
                                value={tvShow.percentageProgress}
                                sx={{
                                    height: 12,
                                    borderRadius: 2,
                                    backgroundColor: '#eee',
                                    '& .MuiLinearProgress-bar': {
                                        borderRadius: 2,
                                        backgroundColor: tvShow.percentageProgress === 100 ? 'green' : '#689B8A'
                                    }
                                }}
                            />
                        </Box>

                        <Divider sx={{ my: 1, bgcolor: "#334" }} />

                        <Typography variant="h6" sx={{ mb: 1 }}>
                            Seasons ({tvShow.seasons.length})
                        </Typography>
                        <Stack spacing={3}>
                            {tvShow.seasons.map((season) => (
                                <SeasonCard
                                    key={season.id}
                                    season={season}
                                    tvShowId={tvShow.id}
                                    onSetWatched={handleSetWatchedCount}
                                    onAllWatched={handleAllWatched}
                                    onIncrement={handleIncrement}
                                    onRate={handleRateSeason}
                                />
                            ))}
                        </Stack>
                    </Box>
                </Stack>

                <UpdateTrackingTypeDialog
                    open={dialogOpen}
                    onClose={() => setDialogOpen(false)}
                    onSave={handleUpdateTrackingType}
                    currentStatus={tvShow.trackingType}
                />
            </Box>
        </>
    );
};

export default TVShowDetails;
