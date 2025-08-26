import React, { useState } from "react";
import {
    Box,
    Button,
    CircularProgress,
    List,
    ListItem,
    ListItemText,
    TextField,
    Typography,
    IconButton,
    Stack,
    Paper,
    Avatar,
    ListItemAvatar,
} from "@mui/material";
import AddIcon from '@mui/icons-material/Add';
import Navbar from "../Navigation/Navbar";
import tvSeriesService from "../../_tv-series/services/tvSeriesService";
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';

const SearchSeries = ({ onSeriesAdded }) => {
    const [query, setQuery] = useState("");
    const [results, setResults] = useState([]);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleSearch = async () => {
        if (!query.trim()) return;
        setLoading(true);
        try {
            const data = await tvSeriesService.searchSeries(query);
            setResults(data);
        } catch (err) {
            console.error(err);
            setResults([]);
        } finally {
            setLoading(false);
        }
    };

    const handleAdd = async (tmdbId, name) => {
        try {
            const result = await tvSeriesService.saveSeries(tmdbId);

            if (result.alreadyExists) {
                handleSeriesAlreadyExists();
            } else {
                if (onSeriesAdded) {
                    onSeriesAdded();
                }
                toast.success(`"${name}" added to Library! 🟢`);

                if (result && result.id) {
                    navigate(`/tv-series/${result.id}`);
                }
            }
        } catch (err) {
            console.error(err);
        }
    };

    const handleSeriesAlreadyExists = () => {
        toast.warning(
            `TV Series is already in the database!`,
            {
                icon: "⚠️",
                autoClose: 3000,
                position: "bottom-right",
                theme: "dark"
            }
        );
    };

    // Helper for TMDB poster:
    const getPosterUrl = (poster_path) => `https://image.tmdb.org/t/p/w92${poster_path}`;

    return (
        <>
            <Navbar />
            <Box
                sx={{
                    maxWidth: 600,
                    mx: "auto",
                    mt: 4,
                    p: 3,
                    bgcolor: "#232634",
                    borderRadius: 2,
                    boxShadow: 3,
                    color: "#eaf1fd"
                }}
            >
                <Typography sx={{ textAlign: "center" }} center variant="h5" gutterBottom>
                    Search TV Series
                </Typography>

                <Stack direction="row" spacing={2} sx={{ mb: 3 }}>
                    <TextField
                        fullWidth
                        label="TV Series Title"
                        variant="outlined"
                        size="small"
                        value={query}
                        onChange={(e) => setQuery(e.target.value)}
                        onKeyDown={(e) => {
                            if (e.key === "Enter") handleSearch();
                        }}
                        sx={{ backgroundColor: "#1a1d29" }}
                        slotProps={{ inputLabel: { style: { color: "#6a6f74ff" } }, input: { style: { color: "white" } } }}
                    />
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={handleSearch}
                        disabled={loading}
                        sx={{ minWidth: "100px" }}
                    >
                        {loading ? <CircularProgress size={24} color="inherit" /> : "Search"}
                    </Button>
                </Stack>

                {!loading && results.length > 0 && (
                    <Paper sx={{ maxHeight: 360, overflow: "auto", bgcolor: "#1a1d29" }}>
                        <List dense>
                            {results.map((res) => (
                                <ListItem
                                    key={res.tmdbId}
                                    secondaryAction={
                                        <IconButton
                                            edge="end"
                                            aria-label="add"
                                            onClick={() => handleAdd(res.tmdbId, res.name)}
                                            size="small"
                                            color="success"
                                        >
                                            <AddIcon />
                                        </IconButton>
                                    }
                                    sx={{
                                        borderBottom: "1px solid #334",
                                        '&:hover': { bgcolor: "#2a2f45" },
                                    }}
                                >
                                    <ListItemAvatar>
                                        <Avatar
                                            variant="rounded"
                                            src={getPosterUrl(res.poster_path)}
                                            alt={res.name}
                                            sx={{
                                                width: 54,
                                                height: 76,
                                                mr: 2,
                                                bgcolor: "#222", // fallback background
                                            }}
                                        />
                                    </ListItemAvatar>
                                    <ListItemText
                                        primary={res.name}
                                        secondary={res.firstAirDate || null}
                                        slotProps={{ primary: { color: "#eaf1fd" }, secondary: { color: "#a0aec0" } }}
                                    />
                                </ListItem>
                            ))}
                        </List>
                    </Paper>
                )}

                {!loading && results.length === 0 && query.trim() !== "" && (
                    <Typography color="grey.500">No results found.</Typography>
                )}
            </Box>
            <br />
            <p className="footer" >metadata by TMDB API</p>
        </>
    );
};

export default SearchSeries;
