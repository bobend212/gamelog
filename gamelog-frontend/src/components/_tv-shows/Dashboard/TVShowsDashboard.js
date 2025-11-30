import { useEffect, useState, useMemo } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import {
    Box, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Paper, IconButton, Tooltip, Avatar, Stack, TextField, Chip, Badge,
    Select, MenuItem, InputLabel, FormControl
} from "@mui/material";
import DeleteForeverIcon from '@mui/icons-material/DeleteForever';
import CloseIcon from "@mui/icons-material/Close";
import TablePagination from '@mui/material/TablePagination';
import Navbar from "../Navigation/Navbar";
import tvShowService from "../services/tvShowService";
import { parseDate, POSTER_PATH_BASE_W92, VOD_PROVIDER_PATH_BASE_W45, TRACKING_TYPES } from '../utils/TVShowUtil';

const TVShowsDashboard = () => {
    const navigate = useNavigate();
    const [searchParams, setSearchParams] = useSearchParams();

    const pageParam = parseInt(searchParams.get("page") || "0", 10);
    const sizeParam = parseInt(searchParams.get("size") || "12", 10);
    const statusParam = searchParams.get("status") || TRACKING_TYPES.WATCHING.value;
    const searchParam = searchParams.get("search") || "";

    const [tvShowList, setTVShowList] = useState([]);
    const [tvShowCount, setTVShowCount] = useState({ totalElements: 0 });
    const [loading, setLoading] = useState(true);
    const [localSearch, setLocalSearch] = useState(searchParam);

    useEffect(() => {
        const handler = setTimeout(() => {
            if (localSearch !== searchParam) {
                setSearchParams({
                    page: 0,
                    size: sizeParam,
                    status: statusParam,
                    search: localSearch
                });
            }
        }, 300);
        return () => clearTimeout(handler);
    }, [localSearch, searchParam, sizeParam, statusParam, setSearchParams]);

    useEffect(() => {
        loadTVShows(pageParam, sizeParam, searchParam, statusParam);
    }, [pageParam, sizeParam, searchParam, statusParam]);

    const loadTVShows = async (p, s, q, t) => {
        setLoading(true);
        try {
            const data = await tvShowService.getAllTVShows(p, s, q, t);
            setTVShowList(data.content);
            setTVShowCount(data);
        } catch (error) {
            console.error("Failed to fetch shows", error);
        } finally {
            setLoading(false);
        }
    };

    const handleChangePage = (event, newPage) => {
        setSearchParams({ page: newPage, size: sizeParam, status: statusParam, search: searchParam });
    };

    const handleChangeRowsPerPage = (event) => {
        const newSize = parseInt(event.target.value, 10);
        setSearchParams({ page: 0, size: newSize, status: statusParam, search: searchParam });
    };

    const handleFilterChange = (newStatus) => {
        setSearchParams({ page: 0, size: sizeParam, status: newStatus, search: searchParam });
    };

    const handleSearchClear = () => {
        setLocalSearch("");
        setSearchParams({ page: 0, size: sizeParam, status: statusParam, search: "" });
    };

    const handleDelete = async (tvShowId) => {
        if (window.confirm("Are you sure you want to delete this TV Show?")) {
            try {
                await tvShowService.deleteTVShow(tvShowId);
                loadTVShows(pageParam, sizeParam, searchParam, statusParam);
            } catch (error) {
                alert("Failed to delete TV Show.");
            }
        }
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
                        value={localSearch}
                        onChange={(e) => setLocalSearch(e.target.value)}
                        sx={{ backgroundColor: "#374579ff", borderRadius: 2 }}
                        slotProps={{ inputLabel: { style: { color: "white" } }, input: { style: { color: "white" } } }}
                    />
                    {localSearch && (
                        <IconButton
                            onClick={handleSearchClear}
                            size="small"
                            sx={{ ml: 1, bgcolor: "#27417c", "&:hover": { bgcolor: "#34518f" }, color: "white" }}
                        >
                            <CloseIcon fontSize="small" />
                        </IconButton>
                    )}
                </Box>

                <Box sx={{ display: "flex", alignItems: "center", mt: 1, mb: 2 }}>
                    <FormControl size="small" sx={{ minWidth: 160, flexGrow: 1 }}>
                        <InputLabel id="tracking-filter-label" sx={{ color: "#cfd8dc" }}>Tracking Type</InputLabel>
                        <Select
                            labelId="tracking-filter-label"
                            label="Tracking Type"
                            value={statusParam}
                            onChange={(e) => handleFilterChange(e.target.value)}
                            sx={{ bgcolor: "#374579ff", color: "white", borderRadius: 2 }}
                        >
                            {Object.values(TRACKING_TYPES).map((t) => (
                                <MenuItem key={t.value} value={t.value} sx={{ color: "black", fontWeight: "bold" }}>
                                    {t.label}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>

                    <IconButton
                        onClick={() => handleFilterChange(TRACKING_TYPES.WATCHING.value)}
                        sx={{ ml: 1, bgcolor: "#27417c", "&:hover": { bgcolor: "#34518f" }, color: "white" }}
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
                                    <TableCell colSpan={7} align="center" sx={{ color: "#fff", py: 3 }}>Loading...</TableCell>
                                </TableRow>
                            ) : tvShowList.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={7} align="center" sx={{ color: "#fff", py: 3 }}>
                                        No TV Shows tracked yet.
                                    </TableCell>
                                </TableRow>
                            ) : (
                                tvShowList.map(tvShow => {
                                    const trackingType = TRACKING_TYPES[tvShow.trackingType] || TRACKING_TYPES.WATCHING;
                                    return (
                                        <TableRow
                                            hover
                                            key={tvShow.id}
                                            sx={{ cursor: "pointer", bgcolor: "#232634", '& td': { color: "#fff" } }}
                                            onClick={() => navigate(`/tv-shows/${tvShow.id}`)}
                                        >
                                            <TableCell>
                                                <Avatar variant="rounded" src={POSTER_PATH_BASE_W92 + tvShow.posterPath} alt={tvShow.name} sx={{ width: 40, height: 60, bgcolor: "#222" }} />
                                            </TableCell>
                                            <TableCell sx={{ fontWeight: 600 }}>
                                                <Box display="flex" flexDirection={"column"}>
                                                    <Typography variant="body1">{tvShow.name}</Typography>
                                                    <Badge color={trackingType.color} variant="standard" overlap="circular" badgeContent={null}>
                                                        <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                                            {trackingType.icon}
                                                            <Typography component="span" variant="body2" sx={{ ml: 0.5, fontWeight: 600, color: (theme) => theme.palette[trackingType.color]?.main, display: 'flex', alignItems: 'center' }}>
                                                                {trackingType.label}
                                                                <Chip label={tvShow.status} size="small" sx={{ ml: 1, bgcolor: 'primary.dark', color: 'wheat' }} />
                                                            </Typography>
                                                        </Box>
                                                    </Badge>
                                                </Box>
                                            </TableCell>
                                            <TableCell align="left">{tvShow.totalWatchedEpisodes} / {tvShow.numberOfEpisodes} ({tvShow.percentageProgress}%)</TableCell>
                                            <TableCell align="left">{parseDate(tvShow.lastAirDate)}</TableCell>
                                            <TableCell align="left">{tvShow.nextEpisode}</TableCell>
                                            <TableCell align="right">
                                                <Stack direction="row" justifyContent="right" spacing={1}>
                                                    {tvShow.vodProviders?.map((p) => (
                                                        <Avatar key={p} src={`${VOD_PROVIDER_PATH_BASE_W45}${p.split(';')[0]}`} sx={{ width: 30, height: 30 }} />
                                                    ))}
                                                </Stack>
                                            </TableCell>
                                            <TableCell align="right" onClick={e => e.stopPropagation()}>
                                                <IconButton onClick={() => handleDelete(tvShow.id)} size="small">
                                                    <DeleteForeverIcon sx={{ color: "red" }} />
                                                </IconButton>
                                            </TableCell>
                                        </TableRow>
                                    );
                                })
                            )}
                        </TableBody>
                    </Table>

                    <Box sx={{ display: 'flex', justifyContent: 'center', width: '100%' }}>
                        <TablePagination
                            component="div"
                            count={tvShowCount.totalElements || 0}
                            page={pageParam}
                            onPageChange={handleChangePage}
                            rowsPerPage={sizeParam}
                            rowsPerPageOptions={[5, 12, 25, 50, 100]}
                            onRowsPerPageChange={handleChangeRowsPerPage}
                            sx={{ color: 'white' }}
                        />
                    </Box>
                </TableContainer>
            </Box>
        </>
    );
};

export default TVShowsDashboard;
