// src/components/TVSeriesDashboard.jsx

import { useEffect, useState } from "react";
import {
    Box, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Paper, IconButton, Chip, Tooltip, Avatar
} from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import { useNavigate } from "react-router-dom";
import Navbar from "../Navigation/Navbar";
import StatusDialog from "../Common/StatusDialog";
import tvSeriesService from "../../_tv-series/services/tvSeriesService";
import { TRACKING_TYPES } from "../utils/constants";

// Helper for TMDB thumbnails
const getPosterUrl = (poster_path) =>
    poster_path
        ? `https://image.tmdb.org/t/p/w92${poster_path}`
        : "https://via.placeholder.com/54x76.png?text=No+Poster";

// const trackingType = {
//     WATCHING: { color: "info", label: "Watching", icon: <PlayCircleFilledWhiteIcon fontSize="small" /> },
//     UP_TO_DATE: { color: "info", label: "Up To Date", icon: <CheckCircleOutlineIcon fontSize="small" /> },
//     COMPLETED: { color: "success", label: "Completed", icon: <DoneAllIcon fontSize="small" /> },
//     ON_HOLD: { color: "success", label: "On Hold", icon: <PauseCircleFilledIcon fontSize="small" /> },
//     DROPPED: { color: "error", label: "Dropped", icon: <CancelIcon fontSize="small" /> },
//     WISHLIST: { color: "warning", label: "Wishlist", icon: <ScheduleIcon fontSize="small" /> }
// };

const TVSeriesDashboard = () => {
    const [seriesList, setSeriesList] = useState([]);
    const [loading, setLoading] = useState(true);
    const [dialogOpen, setDialogOpen] = useState(false);
    const [selectedSeries, setSelectedSeries] = useState(null);

    const navigate = useNavigate();

    useEffect(() => { loadSeries(); }, []);

    const loadSeries = async () => {
        setLoading(true);
        try {
            const data = await tvSeriesService.getAllSeries();
            setSeriesList(data);
        } finally {
            setLoading(false);
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
            await loadSeries();
        }
    };

    return (
        <>
            <Navbar />
            <Box sx={{ maxWidth: 1000, mx: "auto", mt: 4, bgcolor: "#181b23", p: 3, minHeight: "100vh", color: "#e0e0e0" }}>
                <Typography variant="h4" fontWeight="bold" sx={{ mb: 3, color: "#aad6ff" }}>
                    Dashboard
                </Typography>
                <TableContainer component={Paper} sx={{ bgcolor: "#232634" }}>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell sx={{ color: "#aad6ff", fontWeight: "bold", width: 70 }}></TableCell>
                                <TableCell sx={{ color: "#aad6ff", fontWeight: "bold" }}>Name</TableCell>
                                <TableCell sx={{ color: "#aad6ff", fontWeight: "bold" }}>Tracking Type</TableCell>
                                <TableCell sx={{ color: "#aad6ff", fontWeight: "bold" }}>Status</TableCell>
                                <TableCell sx={{ color: "#aad6ff", fontWeight: "bold" }}>Actions</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {loading ? (
                                <TableRow>
                                    <TableCell colSpan={4} align="center" sx={{ color: "#fff" }}>Loading...</TableCell>
                                </TableRow>
                            ) : seriesList.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={4} align="center" sx={{ color: "#fff" }}>
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
                                            <TableCell sx={{ fontWeight: 600 }}>{series.name}</TableCell>
                                            <TableCell>
                                                <Chip icon={info.icon} label={info.label} color={info.color} size="small" sx={{ fontWeight: 600 }} />
                                            </TableCell>
                                            <TableCell sx={{ fontWeight: 600 }}>{series.status}</TableCell>
                                            <TableCell onClick={e => e.stopPropagation()}>
                                                <Tooltip title="Change Status">
                                                    <IconButton onClick={() => openStatusDialog(series)} size="small">
                                                        <EditIcon sx={{ color: "white" }} />
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
