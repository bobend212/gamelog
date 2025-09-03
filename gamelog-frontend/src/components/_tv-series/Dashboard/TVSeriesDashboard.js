import { useEffect, useState } from "react";
import {
    Box, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Paper, IconButton, Tooltip, Avatar, Stack, TextField, Chip
} from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import DeleteForeverIcon from '@mui/icons-material/DeleteForever';
import { useNavigate } from "react-router-dom";
import Navbar from "../Navigation/Navbar";
import StatusDialog from "../Common/StatusDialog";
import tvSeriesService from "../../_tv-series/services/tvSeriesService";
import { Badge } from "@mui/material";
import { Select, MenuItem, InputLabel, FormControl } from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import { parseDate, POSTER_PATH_BASE_W92, VOD_PROVIDER_PATH_BASE_W45, TRACKING_TYPES } from '../utils/tvSeriesUtil';
import TablePagination from '@mui/material/TablePagination';

const TVSeriesDashboard = () => {
    const [seriesList, setSeriesList] = useState([]);
    const [loading, setLoading] = useState(true);
    const [dialogOpen, setDialogOpen] = useState(false);
    const [selectedSeries, setSelectedSeries] = useState(null);
    const [trackingTypeFilter, setTrackingTypeFilter] = useState(TRACKING_TYPES.WATCHING.value);
    const [seriesCount, setSeriesCount] = useState([])
    const [totalSeriesCount, setTotalSeriesCount] = useState([])
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(12);
    const [searchQuery, setSearchQuery] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        loadSeries(trackingTypeFilter);
    }, [trackingTypeFilter]);

    const setFilter = async (value) => {
        setTrackingTypeFilter(value);
        setPage(0);
    };

    const loadSeries = async (filter = "ALL TV SERIES") => {
        setLoading(true);
        try {
            const data =
                filter && filter !== "ALL TV SERIES"
                    ? await tvSeriesService.getAllSeriesByTrackingType(filter)
                    : await tvSeriesService.getAllSeries();
            setSeriesList(data);
            setSeriesCount(data.length)

            const totalSeriesCount = await tvSeriesService.getAllSeries();
            setTotalSeriesCount(totalSeriesCount.length);
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (seriesId) => {
        if (window.confirm("Are you sure you want to delete this series?")) {
            try {
                await tvSeriesService.deleteSeries(seriesId);
                await loadSeries(trackingTypeFilter);
            } catch (error) {
                alert("Failed to delete the series.");
                console.error(error);
            }
        }
    };

    const openStatusDialog = (series) => {
        setSelectedSeries(series);
        setDialogOpen(true);
    };

    const handleSaveStatus = async (newStatus) => {
        if (selectedSeries) {
            await tvSeriesService.updateTrackingType(selectedSeries.id, newStatus);
            setDialogOpen(false);
            setSelectedSeries(null);
            await loadSeries(trackingTypeFilter);
        }
    };

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);  // reset to first page when page size changes
    };

    const filteredSeries = seriesList.filter(series => {
        const q = searchQuery.toLowerCase();

        // Check if name matches
        const nameMatch = series.name.toLowerCase().includes(q);

        // Check if any VOD provider matches the query
        const vodMatch = series.vodProviders?.some(provider =>
            provider.split(';')[1].toLowerCase().includes(q)
        ) || false;

        return nameMatch || vodMatch;
    });



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
                            Status
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
                            <MenuItem value="ALL TV SERIES" sx={{ color: "black", fontWeight: "bold" }}>
                                ALL TV SERIES
                            </MenuItem>
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
                            ) : seriesList.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={6} align="center" sx={{ color: "#fff" }}>
                                        No series tracked yet.
                                    </TableCell>
                                </TableRow>
                            ) : (
                                filteredSeries
                                    .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                    .map(series => {
                                        const trackingType = TRACKING_TYPES[series.trackingType];
                                        return (
                                            <TableRow
                                                hover
                                                key={series.id}
                                                sx={{
                                                    cursor: "pointer",
                                                    bgcolor: "#232634",
                                                    '& td': { color: "#fff" }
                                                }}
                                                onClick={() => navigate(`/tv-series/${series.id}`)}
                                            >
                                                <TableCell>
                                                    <Avatar
                                                        variant="rounded"
                                                        src={POSTER_PATH_BASE_W92 + series.poster_path}
                                                        alt={series.name}
                                                        sx={{ width: 40, height: 60, bgcolor: "#222" }}
                                                    />
                                                </TableCell>
                                                <TableCell sx={{ fontWeight: 600, verticalAlign: 'center' }}>
                                                    <Box display="flex" gap={0.5}>
                                                        <Box display="flex" flexDirection={"column"} gap={0.5} mr={1}>
                                                            <Typography variant="body1" component="div" sx={{ fontWeight: 600 }}>
                                                                {series.name}
                                                            </Typography>
                                                            <Badge
                                                                color={trackingType.color}
                                                                variant="standard"
                                                                overlap="circular"
                                                                badgeContent={null}
                                                            >
                                                                {trackingType.icon}
                                                                <Typography variant="body2" sx={{ ml: 0.5, fontWeight: 600, color: (theme) => theme.palette[trackingType.color]?.main || 'inherit' }}>
                                                                    {trackingType.label}
                                                                    <Chip label={series.status} size="small" sx={{ ml: 1, bgcolor: 'primary.dark' }} />
                                                                </Typography>
                                                            </Badge>
                                                        </Box>
                                                    </Box>
                                                </TableCell>
                                                <TableCell align="left">
                                                    {series.totalWatchedEpisodes} / {series.number_of_episodes} ({series.percentageProgress}%)
                                                </TableCell>
                                                <TableCell align="left">{parseDate(series.last_air_date)}</TableCell>
                                                <TableCell align="left">{series.nextEpisode}</TableCell>

                                                <TableCell align="right" sx={{ whiteSpace: 'pre-line' }}>
                                                    <Stack direction="row" justifyContent="right" spacing={1} flexWrap="wrap" mb={1}>
                                                        {series.vodProviders?.map((provider) => (
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
                                                    <Tooltip title="Change Tacking Type">
                                                        <IconButton onClick={() => openStatusDialog(series)} size="small">
                                                            <EditIcon sx={{ color: "white" }} />
                                                        </IconButton>
                                                    </Tooltip>
                                                    <Tooltip title="Delete">
                                                        <IconButton onClick={() => handleDelete(series.id)} size="small">
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
                            count={filteredSeries.length}
                            page={page}
                            onPageChange={handleChangePage}
                            rowsPerPage={rowsPerPage}
                            onRowsPerPageChange={handleChangeRowsPerPage}
                            rowsPerPageOptions={[5, 12, 25, 50, 100]}
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
                                    color: '#666', // or any "disabled" color you prefer
                                    opacity: 0.2
                                }
                            }}
                        />
                    </Box>
                </TableContainer>
                <StatusDialog
                    open={dialogOpen}
                    onClose={() => setDialogOpen(false)}
                    onSave={handleSaveStatus}
                    currentStatus={selectedSeries?.myStatus}
                />
            </Box>
        </>
    );
};

export default TVSeriesDashboard;
