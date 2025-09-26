import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
    Box,
    Typography,
    Button,
    Paper,
    Chip,
    Stack,
    Avatar,
    CircularProgress,
    Divider,
    useTheme
} from "@mui/material";

import moviesService from "../services/moviesService";
import MoviesNavbar from "../Navbar/MoviesNavbar";
import { POSTER_PATH_BASE_W200, VOD_PROVIDER_PATH_BASE_W45 } from "../../_tv-series/utils/tvSeriesUtil";
import { toast } from 'react-toastify';
import CloudSyncIcon from '@mui/icons-material/CloudSync';

const MovieDetails = () => {
    const { id } = useParams();
    const [movie, setMovie] = useState(null);
    const [loading, setLoading] = useState(true);
    const theme = useTheme();
    const [syncSummary, setSyncSummary] = useState(null);

    useEffect(() => {
        fetchMovie(id);
    }, [id]);

    const fetchMovie = async (id) => {
        setLoading(true);
        try {
            const data = await moviesService.getMovieById(id);
            setMovie(data);
        } catch (err) {
            console.error("Failed to load movie details", err);
        } finally {
            setLoading(false);
        }
    }

    if (loading)
        return (
            <Box sx={{ display: "flex", justifyContent: "center", mt: 8 }}>
                <CircularProgress color="inherit" />
            </Box>
        );

    if (!movie)
        return (
            <Typography variant="h6" color={theme.palette.grey[600]} align="center" mt={6}>
                Movie not found
            </Typography>
        );

    const formatRuntime = (totalMinutes) => {
        const hours = Math.floor(totalMinutes / 60);
        const minutes = totalMinutes % 60;
        return `${hours}h ${minutes}m`;
    }

    const formatIsoToDateTime = (isoString) => {
        const date = new Date(isoString);
        if (isNaN(date)) return "";

        const pad = (num) => num.toString().padStart(2, "0");

        const year = date.getFullYear();
        const month = pad(date.getMonth() + 1);
        const day = pad(date.getDate());

        const hours = pad(date.getHours());
        const minutes = pad(date.getMinutes());

        return `${year}-${month}-${day} ${hours}:${minutes}`;
    }

    const handleSync = async (movieId) => {
        try {
            const result = await moviesService.syncMovies(movieId);
            setSyncSummary(result);

            await fetchMovie(movieId);

            if (result?.changes?.length > 0) {
                const allChangedFields = result.changes.flatMap(change =>
                    change.fieldChanges.map(field => field.fieldName)
                );

                const uniqueFields = [...new Set(allChangedFields)];
                const fieldsString = uniqueFields.join(', ');

                toast.success(`Sync completed: Updated fields - ${fieldsString}`, { autoClose: 6000 });
            } else {
                toast.success('Sync completed: Up to date', { autoClose: 2000 });
            }
        } catch (error) {
            console.error("Sync failed", error);
            toast.error('Sync failed.');
        }
    };

    return (
        <>
            <MoviesNavbar />
            <Paper
                sx={{
                    p: 3,
                    maxWidth: 800,
                    mx: "auto",
                    mt: 3,
                    bgcolor: theme.palette.grey[400],
                    borderRadius: 2,
                    boxShadow: theme.shadows[3],
                    color: theme.palette.grey[800],
                }}
                elevation={3}
            >
                <Stack direction={{ xs: "column", sm: "row" }} spacing={4}>
                    {/* Poster */}
                    <Box sx={{ flexGrow: 1 }}>
                        <Avatar
                            variant="rounded"
                            src={POSTER_PATH_BASE_W200 + movie.poster}
                            alt={movie.title}
                            sx={{
                                width: 200,
                                height: 300,
                                borderRadius: 3,
                                boxShadow: `0 4px 8px ${theme.palette.grey[400]}`,
                                mb: { xs: 3, sm: 0 },
                                mx: "auto"
                            }}
                        />
                        {movie.createdAt && (
                            <Typography variant="body2" gutterBottom sx={{ color: theme.palette.grey[600], mt: 1, ml: 1 }}>
                                Wishlisted: {formatIsoToDateTime(movie.createdAt)}
                            </Typography>
                        )}
                    </Box>
                    {/* Movie Info */}
                    <Box sx={{ flexGrow: 1 }}>
                        <Typography
                            variant="h4"
                            fontWeight="bold"
                            gutterBottom
                            sx={{ color: theme.palette.grey[900], mb: 0 }}
                        >
                            {movie.title}
                        </Typography>

                        {movie.title !== movie.originalTitle &&
                            <Typography
                                variant="h6"
                                fontWeight="bold"
                                gutterBottom
                                sx={{ color: theme.palette.grey[900] }}
                            >
                                {movie.originalTitle}
                            </Typography>
                        }

                        <Divider sx={{ my: 2, borderColor: theme.palette.grey[300] }} />

                        <Typography
                            variant="body2"
                            gutterBottom
                            sx={{ color: theme.palette.grey[800] }}
                        >
                            Release Date (PL): {movie.releaseDatePL}
                        </Typography>

                        <Typography
                            variant="body2"
                            gutterBottom
                            sx={{ color: theme.palette.grey[800] }}
                        >
                            Release Date (World): {movie.releaseDate}
                        </Typography>
                        <Typography
                            variant="body2"
                            gutterBottom
                            sx={{ color: theme.palette.grey[800] }}
                        >
                            Runtime: {formatRuntime(movie.runtime)}
                        </Typography>
                        <Typography
                            variant="body2"
                            gutterBottom
                            sx={{ color: theme.palette.grey[800] }}
                        >
                            Status: {movie.status}
                        </Typography>

                        <Divider sx={{ my: 2, borderColor: theme.palette.grey[300] }} />

                        <Typography
                            variant="body1"
                            sx={{ color: theme.palette.grey[800], mb: 3, whiteSpace: 'pre-line' }}
                        >
                            {movie.overview}
                        </Typography>

                        {/* Genres */}
                        <Stack direction="row" spacing={1} flexWrap="wrap" mb={3}>
                            {movie.genres.map((genre) => (
                                <Chip
                                    key={genre}
                                    label={genre}
                                    size="small"
                                    sx={{
                                        bgcolor: theme.palette.grey[300],
                                        color: theme.palette.grey[900],
                                        fontWeight: 600,
                                    }}
                                />
                            ))}
                        </Stack>

                        {/* VOD Providers */}
                        <Box sx={{
                            display: 'flex',
                            alignItems: 'end',
                            justifyContent: 'space-between'
                        }}>
                            {movie.vodProviders?.length !== 0 ? (
                                <>
                                    <Stack direction="row" spacing={1} flexWrap="wrap">
                                        {movie.vodProviders?.map((provider) => (
                                            <Avatar
                                                key={provider}
                                                alt={provider}
                                                src={`${VOD_PROVIDER_PATH_BASE_W45}${provider.split(';')[0]}`}
                                                sx={{ width: 45, height: 45 }}
                                                variant="circular"
                                            />
                                        ))}
                                    </Stack>
                                </>
                            ) : (
                                <Typography variant="body1" sx={{ color: theme.palette.grey[900], fontWeight: 600, fontStyle: "italic" }}>
                                    Unavailable on VOD
                                </Typography>
                            )
                            }
                            <Button
                                variant="contained"
                                onClick={() => handleSync(movie.id)}
                                sx={{ textTransform: "none" }}
                                color="success"
                                endIcon={<CloudSyncIcon />}
                            >
                                SYNC
                            </Button>
                        </Box>
                    </Box>
                </Stack>
            </Paper>
        </>
    );

};

export default MovieDetails;
