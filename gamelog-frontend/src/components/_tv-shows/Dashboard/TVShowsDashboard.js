import { useEffect, useState } from "react";
import {
    Box, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Paper, IconButton, Tooltip, Avatar, Stack, TextField, Chip
} from "@mui/material";
import DeleteForeverIcon from '@mui/icons-material/DeleteForever';
import { useNavigate } from "react-router-dom";
import Navbar from "../Navigation/Navbar";
import tvShowService from "../services/tvShowService";
import { Badge } from "@mui/material";
import { Select, MenuItem, InputLabel, FormControl } from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import { parseDate, POSTER_PATH_BASE_W92, VOD_PROVIDER_PATH_BASE_W45, TRACKING_TYPES } from '../utils/TVShowUtil';
import TablePagination from '@mui/material/TablePagination';

const TVShowsDashboard = () => {
    const [tvShowList, setTVShowList] = useState([]);
    const [trackingTypeFilter, setTrackingTypeFilter] = useState(TRACKING_TYPES.WATCHING.value);
    const [tvShowCount, setTVShowCount] = useState([])
    const [searchQuery, setSearchQuery] = useState("");
    const navigate = useNavigate();

    const [debouncedSearch, setDebouncedSearch] = useState('');
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(12);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const handler = setTimeout(() => {
            setDebouncedSearch(searchQuery);
            setPage(0);
        }, 300);

        return () => {
            clearTimeout(handler);
        };
    }, [searchQuery]);

    useEffect(() => {
        loadTVShows(page, rowsPerPage, debouncedSearch, trackingTypeFilter);
    }, [page, rowsPerPage, debouncedSearch, trackingTypeFilter]);

    const setFilter = async (value) => {
        setTrackingTypeFilter(value);
        setPage(0);
    };

    const loadTVShows = async (pageParam, sizeParam, search, trackingTypeFilter) => {
        setLoading(true);
        try {
            const data = await tvShowService.getAllTVShows(pageParam, sizeParam, search, trackingTypeFilter);
            setTVShowList(data.content);
            setTVShowCount(data)
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (tvShowId) => {
        if (window.confirm("Are you sure you want to delete this TV Show?")) {
            try {
                await tvShowService.deleteTVShow(tvShowId);
                await loadTVShows(page, rowsPerPage, debouncedSearch, trackingTypeFilter);
            } catch (error) {
                alert("Failed to delete TV Show.");
                console.error(error);
            }
        }
    };

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    return (
        <>
            <Navbar />
            <Box sx={{ maxWidth: 1500, mx: "auto", mt: 2, color: "#e0e0e0" }}>
                <Box display="flex" alignItems="center" mb={2} gap={1}>
                    <TextField
                        fullWidth
                        variant="outlined"
                        label="Search..."
                        size="small"
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
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

                <Box sx={{ display: "flex", alignItems: "center", mt: 1, mb: 2 }}>

                    <FormControl size="small" sx={{ minWidth: 160, flexGrow: 1 }}>
                        <InputLabel
                            id="tracking-filter-label"
                            sx={{ color: "#cfd8dc" }}
                        >
                            Tracking Type
                        </InputLabel>
                        <Select
                            labelId="tracking-filter-label"
                            label="Tracking Type"
                            value={trackingTypeFilter}
                            onChange={(e) => setFilter(e.target.value)}
                            sx={{
                                bgcolor: "#374579ff",
                                color: "white",
                                borderRadius: 2
                            }}
                        >
                            {Object.values(TRACKING_TYPES).map((t) => (
                                <MenuItem
                                    key={t.value}
                                    value={t.value}
                                    sx={{ color: "black", fontWeight: "bold" }}
                                >
                                    {t.label}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>

                    <IconButton
                        aria-label="clear filter"
                        onClick={() => setFilter(TRACKING_TYPES.WATCHING.value)}
                        sx={{
                            ml: 1,
                            bgcolor: "#27417c",
                            "&:hover": { bgcolor: "#34518f" },
                            color: "white",
                        }}
                        size="small"
                    >
                        <CloseIcon fontSize="small" />
                    </IconButton>
                </Box>

                <TableContainer component={Paper} sx={{ bgcolor: "#232634" }}>
                    <Table size="small">
                        <TableHead>
                            <TableRow>
                                <TableCell sx={{ color: "#aad6ff", fontWeight: "bold", width: 70 }}></TableCell>
                                <TableCell sx={{ color: "#aad6ff", fontWeight: "bold" }}>Name</TableCell>
                                <TableCell align="left" sx={{ color: "#aad6ff", fontWeight: "bold" }}>Progress</TableCell>
                                <TableCell align="left" sx={{ color: "#aad6ff", fontWeight: "bold" }}>Last Ep. Air</TableCell>
                                <TableCell align="left" sx={{ color: "#aad6ff", fontWeight: "bold" }}>Next Ep.</TableCell>
                                <TableCell align="right" sx={{ color: "#aad6ff", fontWeight: "bold" }}>VOD</TableCell>
                                <TableCell></TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {loading ? (
                                <TableRow>
                                    <TableCell colSpan={4} align="center" sx={{ color: "#fff" }}>Loading...</TableCell>
                                </TableRow>
                            ) : tvShowList.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={6} align="center" sx={{ color: "#fff" }}>
                                        No TV Shows tracked yet.
                                    </TableCell>
                                </TableRow>
                            ) : (
                                tvShowList
                                    // .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                    .map(tvShow => {
                                        const trackingType = TRACKING_TYPES[tvShow.trackingType];
                                        return (
                                            <TableRow
                                                hover
                                                key={tvShow.id}
                                                sx={{
                                                    cursor: "pointer",
                                                    bgcolor: "#232634",
                                                    '& td': { color: "#fff" }
                                                }}
                                                onClick={() => navigate(`/tv-shows/${tvShow.id}`)}
                                            >
                                                <TableCell>
                                                    <Avatar
                                                        variant="rounded"
                                                        src={POSTER_PATH_BASE_W92 + tvShow.posterPath}
                                                        alt={tvShow.name}
                                                        sx={{ width: 40, height: 60, bgcolor: "#222" }}
                                                    />
                                                </TableCell>
                                                <TableCell sx={{ fontWeight: 600, verticalAlign: 'center' }}>
                                                    <Box display="flex">
                                                        <Box display="flex" flexDirection={"column"} mr={1}>
                                                            <Typography variant="body1" component="div" sx={{ fontWeight: 600 }}>
                                                                {tvShow.name}
                                                            </Typography>
                                                            <Badge
                                                                color={trackingType.color}
                                                                variant="standard"
                                                                overlap="circular"
                                                                badgeContent={null}
                                                            >
                                                                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                                                    {trackingType.icon}
                                                                    <Typography
                                                                        component="span"
                                                                        variant="body2"
                                                                        sx={{
                                                                            ml: 0.5,
                                                                            fontWeight: 600,
                                                                            color: (theme) => theme.palette[trackingType.color]?.main || 'inherit',
                                                                            display: 'flex',
                                                                            alignItems: 'center'
                                                                        }}
                                                                    >
                                                                        {trackingType.label}
                                                                        <Chip label={tvShow.status} size="small" sx={{ ml: 1, bgcolor: 'primary.dark' }} />
                                                                    </Typography>
                                                                </Box>
                                                            </Badge>

                                                        </Box>
                                                    </Box>
                                                </TableCell>
                                                <TableCell align="left">
                                                    {tvShow.totalWatchedEpisodes} / {tvShow.numberOfEpisodes} ({tvShow.percentageProgress}%)
                                                </TableCell>
                                                <TableCell align="left">{parseDate(tvShow.lastAirDate)}</TableCell>
                                                <TableCell align="left">{tvShow.nextEpisode}</TableCell>

                                                <TableCell align="right" sx={{ whiteSpace: 'pre-line' }}>
                                                    <Stack direction="row" justifyContent="right" spacing={1} flexWrap="wrap" mb={1}>
                                                        {tvShow.vodProviders?.map((provider) => (
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
                                                        <IconButton onClick={() => handleDelete(tvShow.id)} size="small">
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
                            count={tvShowCount.totalElements}
                            page={page}
                            onPageChange={handleChangePage}
                            rowsPerPage={rowsPerPage}
                            rowsPerPageOptions={[5, 12, 25, 50, 100]}
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

export default TVShowsDashboard;
