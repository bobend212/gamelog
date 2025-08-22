// src/components/TVSeriesDashboard.jsx

import { useEffect, useState } from "react";
import {
    Box, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Paper, IconButton, Tooltip, Avatar
} from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import DeleteForeverIcon from '@mui/icons-material/DeleteForever';
import { useNavigate } from "react-router-dom";
import Navbar from "../Navigation/Navbar";
import StatusDialog from "../Common/StatusDialog";
import tvSeriesService from "../../_tv-series/services/tvSeriesService";
import { TRACKING_TYPES } from "../utils/constants";
import { Badge } from "@mui/material";
import { Select, MenuItem, InputLabel, FormControl } from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";

// Helper for TMDB poster:
const getPosterUrl = (poster_path) => `https://image.tmdb.org/t/p/w92${poster_path}`;

const TVSeriesDashboard = () => {
    const [seriesList, setSeriesList] = useState([]);
    const [loading, setLoading] = useState(true);
    const [dialogOpen, setDialogOpen] = useState(false);
    const [selectedSeries, setSelectedSeries] = useState(null);
    const [trackingTypeFilter, setTrackingTypeFilter] = useState(TRACKING_TYPES.WATCHING.value);

    const navigate = useNavigate();

    useEffect(() => {
        loadSeries(trackingTypeFilter);
    }, [trackingTypeFilter]);

    const loadSeries = async (filter = "") => {
        setLoading(true);
        try {
            const data =
                filter && filter !== ""
                    ? await tvSeriesService.getAllSeriesByTrackingType(filter)
                    : await tvSeriesService.getAllSeries();
            setSeriesList(data);
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (seriesId) => {
        // if (window.confirm("Are you sure you want to delete this series?")) {
        try {
            await tvSeriesService.deleteSeries(seriesId);
            await loadSeries(trackingTypeFilter);
        } catch (error) {
            alert("Failed to delete the series.");
            console.error(error);
        }
        // }
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

    return (
        <>
            <Navbar />
            <Box sx={{ maxWidth: 1000, mx: "auto", mt: 4, minHeight: "100vh", color: "#e0e0e0" }}>
                <Box sx={{ display: "flex", alignItems: "center", mt: 1, mb: 2 }}>
                    <FormControl size="small" sx={{ minWidth: 160, flexGrow: 1 }}>
                        <InputLabel
                            id="tracking-filter-label"
                            sx={{ color: "#cfd8dc" }}
                        >
                            Filter
                        </InputLabel>
                        <Select
                            labelId="tracking-filter-label"
                            label="Tracking Type"
                            value={trackingTypeFilter}
                            onChange={(e) => setTrackingTypeFilter(e.target.value)}
                            sx={{
                                bgcolor: "#374579ff",
                                color: "white",
                                borderRadius: 1,
                                "& .MuiSelect-select": {
                                    py: 1,
                                    px: 2,
                                },
                                "& .MuiSvgIcon-root": {
                                    color: "white",
                                },
                                "&:hover": {
                                    bgcolor: "#435a8b",
                                },
                                "& .MuiPaper-root": {
                                    bgcolor: "#2a3b63",
                                    color: "white",
                                },
                                "& .MuiMenuItem-root": {
                                    "&:hover": {
                                        bgcolor: "#4a69ad",
                                    },
                                    "&.Mui-selected": {
                                        bgcolor: "#1f314c",
                                    },
                                },
                                "& .MuiMenuItem-root.Mui-selected": {
                                    backgroundColor: "#1f314c",
                                    color: "red",
                                    "&:hover": {
                                        backgroundColor: "#34518f",
                                    },
                                },
                            }}
                        >
                            {Object.values(TRACKING_TYPES).map((t) => (
                                <MenuItem
                                    key={t.value}
                                    value={t.value}
                                    sx={{ color: "black", bgcolor: "#34518f" }}
                                >
                                    {t.label}
                                </MenuItem>
                            ))}
                            <MenuItem value="" sx={{ color: "black", bgcolor: "#34518f" }}>
                                ALL
                            </MenuItem>
                        </Select>
                    </FormControl>

                    <IconButton
                        aria-label="clear filter"
                        onClick={() => setTrackingTypeFilter(TRACKING_TYPES.WATCHING.value)}
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
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell sx={{ color: "#aad6ff", fontWeight: "bold", width: 70 }}></TableCell>
                                <TableCell sx={{ color: "#aad6ff", fontWeight: "bold" }}>Name</TableCell>
                                <TableCell align="left" sx={{ color: "#aad6ff", fontWeight: "bold" }}>Progress</TableCell>
                                <TableCell sx={{ color: "#aad6ff", fontWeight: "bold" }}>Last Air Date</TableCell>
                                <TableCell sx={{ color: "#aad6ff", fontWeight: "bold" }}>Status</TableCell>
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
                                seriesList.map(series => {
                                    const info = TRACKING_TYPES[series.trackingType];
                                    return (
                                        <TableRow
                                            hover
                                            key={series.id}
                                            sx={{
                                                cursor: "pointer",
                                                bgcolor: "#232634",
                                                '& td': { color: "#fff" }
                                            }}
                                            onClick={() => navigate(`/series/${series.id}`)}
                                        >
                                            <TableCell>
                                                <Avatar
                                                    variant="rounded"
                                                    src={getPosterUrl(series.poster_path)}
                                                    alt={series.name}
                                                    sx={{ width: 40, height: 60, bgcolor: "#222" }}
                                                />
                                            </TableCell>
                                            <TableCell sx={{ fontWeight: 600, verticalAlign: 'top' }}>
                                                <Box display="flex" flexDirection="column" gap={0.5} alignItems="flex-start">
                                                    <Typography variant="body1" component="div" sx={{ fontWeight: 600 }}>
                                                        {series.name}
                                                    </Typography>
                                                    <Box display="flex" alignItems="center" gap={0.5}>
                                                        <Badge
                                                            color={info.color}
                                                            variant="standard"
                                                            overlap="circular"
                                                            badgeContent={null}
                                                        >
                                                            {info.icon}
                                                        </Badge>
                                                        <Typography variant="body2" sx={{ fontWeight: 600, color: (theme) => theme.palette[info.color]?.main || 'inherit' }}>
                                                            {info.label}
                                                        </Typography>
                                                    </Box>
                                                </Box>
                                            </TableCell>
                                            <TableCell align="left">
                                                {series.totalWatchedEpisodes} / {series.number_of_episodes} ({series.percentageProgress}%)
                                            </TableCell>
                                            <TableCell>{series.last_air_date}</TableCell>
                                            <TableCell>{series.status}</TableCell>
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
