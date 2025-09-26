import { useEffect, useState } from "react";
import {
    Box, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Paper, IconButton, Avatar, TextField, Chip, Stack, Tooltip
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import DeleteForeverIcon from "@mui/icons-material/DeleteForever";
import TablePagination from '@mui/material/TablePagination';
import moviesService from "../services/moviesService";
import { POSTER_PATH_BASE_W92, VOD_PROVIDER_PATH_BASE_W45 } from "../../_tv-series/utils/tvSeriesUtil";
import MoviesNavbar from "../Navbar/MoviesNavbar";
import { useNavigate } from "react-router-dom";
import { toast } from 'react-toastify';

const MoviesDashboard = () => {
    const [moviesList, setMoviesList] = useState([]);
    const [allMovies, setAllMovies] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [searchQuery, setSearchQuery] = useState("");
    const navigate = useNavigate();
    const [moviesPage, setMoviesPage] = useState({ content: [], totalElements: 0, number: 0, size: 10 });

    useEffect(() => {
        loadMovies(page, rowsPerPage);
    }, []);

    const loadMovies = async (pageParam, sizeParam) => {
        setLoading(true);
        try {
            const data = await moviesService.getAllMoviesWithPagination(pageParam, sizeParam);
            setMoviesList(data.content);
            setMoviesPage(data);

            const allMovies = await moviesService.getAllMovies();
            setAllMovies(allMovies);
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (movieId) => {
        if (window.confirm("Are you sure you want to delete this movie?")) {
            try {
                await moviesService.deleteMovie(movieId);
                await loadMovies(page, rowsPerPage);
                toast.success('Movie deleted.', { autoClose: 2000 });
            } catch (error) {
                alert("Failed to delete the movie.");
                console.error(error);
            }
        }
    };

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
        if (!searchQuery) {
            loadMovies(newPage, rowsPerPage);
        }
    };

    const handleChangeRowsPerPage = (event) => {
        const newSize = parseInt(event.target.value, 10);
        setRowsPerPage(newSize);
        setPage(0);
        if (!searchQuery) {
            loadMovies(0, newSize);
        }
    };

    const sourceData = searchQuery ? allMovies : moviesList;
    const filteredMovies = sourceData.filter(movie => {
        if (!searchQuery) return true;

        const q = searchQuery.toLowerCase();
        const titleMatch = movie.title.toLowerCase().includes(q);
        const originalTitleMatch = movie.originalTitle.toLowerCase().includes(q);
        const vodMatch = movie.vodProviders?.some(provider =>
            provider.split(';')[1].toLowerCase().includes(q)
        ) || false;
        const genreMatch = movie.genres?.some(provider =>
            provider.toLowerCase().includes(q)
        ) || false;
        return titleMatch || originalTitleMatch || vodMatch || genreMatch;
    });

    const displayedMovies = searchQuery
        ? filteredMovies.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
        : filteredMovies;

    return (
        <>
            <MoviesNavbar />
            <Box sx={{ maxWidth: 1500, mx: "auto", mt: 2, color: "#e0e0e0" }}>
                <Box display="flex" alignItems="center" mb={2} gap={1}>
                    <TextField
                        fullWidth
                        variant="outlined"
                        label="Search..."
                        size="small"
                        value={searchQuery}
                        onChange={(e) => {
                            setSearchQuery(e.target.value);
                            setPage(0);
                        }}
                        sx={{ backgroundColor: "#374579ff", borderRadius: 2 }}
                        slotProps={{ inputLabel: { style: { color: "white" } }, input: { style: { color: "white" } } }}
                    />
                    {searchQuery && (
                        <IconButton
                            aria-label="clear search"
                            onClick={() => setSearchQuery('')}
                            size="small"
                            sx={{
                                ml: 1,
                                bgcolor: "#27417c",
                                "&:hover": { bgcolor: "#34518f" },
                                color: "white",
                            }}
                        >
                            <CloseIcon fontSize="small" />
                        </IconButton>
                    )}
                </Box>

                <TableContainer component={Paper} sx={{ bgcolor: "#232634" }}>
                    <Table size="small">
                        <TableHead>
                            <TableRow>
                                <TableCell sx={{ color: "#aad6ff", fontWeight: "bold", width: 70 }}></TableCell>
                                <TableCell sx={{ color: "#aad6ff", fontWeight: "bold" }}>Title</TableCell>
                                <TableCell align="center" sx={{ color: "#aad6ff", fontWeight: "bold" }}>Genres</TableCell>
                                <TableCell align="center" sx={{ color: "#aad6ff", fontWeight: "bold" }}>VOD</TableCell>
                                <TableCell></TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {loading ? (
                                <TableRow>
                                    <TableCell colSpan={4} align="center" sx={{ color: "#fff" }}>Loading...</TableCell>
                                </TableRow>
                            ) : moviesList.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={6} align="center" sx={{ color: "#fff" }}>
                                        No movies to watch yet.
                                    </TableCell>
                                </TableRow>
                            ) : (
                                displayedMovies
                                    .map(movie => {
                                        return (
                                            <TableRow
                                                hover
                                                key={movie.id}
                                                sx={{
                                                    cursor: "pointer",
                                                    bgcolor: "#232634",
                                                    '& td': { color: "#fff" }
                                                }}
                                                onClick={() => navigate(`/movies/${movie.id}`)}
                                            >
                                                <TableCell>
                                                    <Avatar
                                                        variant="rounded"
                                                        src={POSTER_PATH_BASE_W92 + movie.poster}
                                                        alt={movie.title}
                                                        sx={{ width: 45, height: 65, bgcolor: "#222" }}
                                                    />
                                                </TableCell>
                                                <TableCell sx={{ fontWeight: 600, verticalAlign: 'center' }}>
                                                    <Box display="flex" flexDirection={"column"} gap={0.5}>
                                                        <Typography variant="body1" component="div" sx={{ fontWeight: 600 }}>
                                                            {movie.title} {movie.releaseDate && `(${movie.releaseDate.split("-")[0]})`}
                                                            {/* <Chip label={movie.status} size="small" sx={{ bgcolor: 'primary.light', ml: 1 }} /> */}
                                                        </Typography>
                                                        {movie.title !== movie.originalTitle &&
                                                            <Typography variant="subtitle2" component="div" sx={{ fontWeight: 600 }}>
                                                                {movie.originalTitle}
                                                            </Typography>
                                                        }
                                                    </Box>
                                                </TableCell>
                                                <TableCell align="center">
                                                    {movie.genres?.map((provider, index) => (
                                                        <Chip
                                                            key={provider + index}
                                                            label={provider}
                                                            size="small"
                                                            sx={{ mr: 1, bgcolor: 'secondary.light' }}
                                                        />
                                                    ))}
                                                </TableCell>
                                                <TableCell align="center" sx={{ whiteSpace: 'pre-line' }}>
                                                    <Stack direction="row" justifyContent="center" spacing={1} flexWrap="wrap">
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
                                                </TableCell>
                                                <TableCell align="right" onClick={e => e.stopPropagation()}>
                                                    <Tooltip title="Delete">
                                                        <IconButton onClick={() => handleDelete(movie.id)} size="small">
                                                            <DeleteForeverIcon sx={{ color: "red" }} />
                                                        </IconButton>
                                                    </Tooltip>
                                                </TableCell>
                                            </TableRow>
                                        );
                                    })
                            )}
                        </TableBody>
                    </Table>
                    <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', width: '100%' }}>
                        <TablePagination
                            component="div"
                            count={searchQuery ? filteredMovies.length : moviesPage.totalElements}
                            page={page}
                            onPageChange={handleChangePage}
                            rowsPerPage={rowsPerPage}
                            rowsPerPageOptions={[10, 20, 50, 100]}
                            onRowsPerPageChange={handleChangeRowsPerPage}
                            sx={{
                                color: 'white',
                                '& .MuiTablePagination-selectLabel, & .MuiTablePagination-displayedRows': {
                                    color: 'white',
                                },
                                '& .MuiTablePagination-select, & .MuiInputBase-root': {
                                    color: 'white',
                                },
                                '& .MuiSvgIcon-root': {
                                    color: 'white',
                                },
                                '& .Mui-disabled': {
                                    color: '#666',
                                    opacity: 0.2
                                }
                            }}
                        />
                    </Box>
                </TableContainer>
            </Box>
        </>
    );
};

export default MoviesDashboard;
