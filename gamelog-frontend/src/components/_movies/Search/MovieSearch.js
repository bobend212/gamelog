import { useState } from "react";
import {
    Box,
    Typography,
    TextField,
    Paper,
    Avatar,
    Grid,
    IconButton,
    CircularProgress,
    Stack,
    useTheme,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import SearchIcon from "@mui/icons-material/Search";
import { toast } from 'react-toastify';
import moviesService from "../services/moviesService";
import { POSTER_PATH_BASE_W200 } from "../../_tv-series/utils/tvSeriesUtil";
import MoviesNavbar from "../Navbar/MoviesNavbar";

const MovieSearch = () => {
    const [query, setQuery] = useState("");
    const [results, setResults] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const theme = useTheme();

    const handleSearch = async () => {
        if (!query.trim()) return;
        setLoading(true);
        setError(null);

        try {
            const data = await moviesService.searchMovies(query.trim());
            setResults(data);
        } catch (err) {
            setError("Failed to fetch movies. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    const handleAddMovie = async (movieTmdbId) => {
        try {
            const result = await moviesService.saveMovie(movieTmdbId);
            if (result.alreadyExists) {
                toast.warning(`Movie is already in the database!`);
            } else {
                toast.success(`Movie added successfully!`);
            }

        } catch (error) {
            alert("Failed to add movie.");
            console.error(error);
        }
    };

    const displayedResults = results.slice(0, 20);

    return (
        <>
            <MoviesNavbar />
            <Box sx={{ maxWidth: 1000, mx: "auto", mt: 3, px: 2 }}>
                {/* Search box */}
                <Paper
                    sx={{
                        p: 3,
                        mb: 3,
                        bgcolor: theme.palette.grey[400],
                        borderRadius: 2,
                        boxShadow: theme.shadows[3],
                        color: theme.palette.grey[800],
                    }}
                    elevation={3}
                >
                    <Stack direction="row" spacing={1} alignItems="center">
                        <TextField
                            fullWidth
                            placeholder="Search movies by title..."
                            value={query}
                            onChange={(e) => setQuery(e.target.value)}
                            size="small"
                            variant="outlined"
                            onKeyDown={(e) => {
                                if (e.key === "Enter") {
                                    handleSearch();
                                }
                            }}
                        />
                        <IconButton
                            onClick={handleSearch}
                            color="primary"
                            aria-label="search"
                            size="large"
                        >
                            <SearchIcon />
                        </IconButton>
                    </Stack>
                </Paper>

                {/* Loading */}
                {loading && (
                    <Box sx={{ display: "flex", justifyContent: "center", mt: 8 }}>
                        <CircularProgress color="inherit" />
                    </Box>
                )}

                {/* Error */}
                {error && (
                    <Typography
                        color="error"
                        variant="body1"
                        align="center"
                        sx={{ mt: 4, mb: 2 }}
                    >
                        {error}
                    </Typography>
                )}

                {/* No results */}
                {!loading && !error && results.length === 0 && query.trim() !== "" && (
                    <Typography
                        variant="body1"
                        color="text.secondary"
                        align="center"
                        sx={{ mt: 4, mb: 2 }}
                    >
                        No movies found for "{query}"
                    </Typography>
                )}

                {/* Movies grid */}
                <Grid container spacing={2}>
                    {displayedResults.map((movie) => (
                        <Grid size={{ xs: 12, sm: 8, md: 2.4 }} key={movie.id}>
                            <Paper
                                elevation={2}
                                sx={{
                                    position: "relative",
                                    textAlign: "center",
                                    p: 1,
                                    cursor: "default",
                                    bgcolor: theme.palette.grey[300],
                                    borderRadius: 2,
                                    boxShadow: theme.shadows[3],
                                    color: theme.palette.grey[800],
                                    height: 300,
                                    width: 180,
                                    display: "flex",
                                    flexDirection: "column",
                                    justifyContent: "space-between",
                                    mx: "auto",
                                }}
                            >
                                {/* Poster */}
                                <Avatar
                                    variant="rounded"
                                    src={POSTER_PATH_BASE_W200 + movie.poster_path}
                                    alt={movie.title}
                                    sx={{
                                        width: "100%",
                                        height: 190,
                                        borderRadius: 2,
                                        mb: 0.5,
                                        objectFit: "cover",
                                    }}
                                />

                                <Box sx={{ flexGrow: 1, mb: 0.5 }}>
                                    {/* Title */}
                                    <Typography
                                        variant="subtitle1"
                                        sx={{
                                            fontWeight: "bold",
                                            overflow: "hidden",
                                            textOverflow: "ellipsis",
                                            whiteSpace: "nowrap",
                                            width: "100%",
                                        }}
                                        title={movie.title}
                                        gutterBottom
                                    >
                                        {movie.title}
                                    </Typography>

                                    {/* Release date */}
                                    <Typography
                                        variant="caption"
                                        sx={{ color: (theme) => theme.palette.grey[600] }}
                                    >
                                        {movie.releaseDate}
                                    </Typography>
                                </Box>

                                {/* Add button */}
                                <IconButton
                                    color="primary"
                                    aria-label="Add movie"
                                    onClick={() => handleAddMovie(movie.id)}
                                    size="small"
                                    sx={{ alignSelf: "center" }}
                                >
                                    <AddIcon />
                                </IconButton>
                            </Paper>
                        </Grid>
                    ))}
                </Grid>
            </Box>
        </>
    );
};

export default MovieSearch;
