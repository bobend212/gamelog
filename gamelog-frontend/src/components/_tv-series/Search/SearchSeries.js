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

const SearchSeries = ({ onSeriesAdded }) => {
    const [query, setQuery] = useState("");
    const [results, setResults] = useState([]);
    const [loading, setLoading] = useState(false);

    const handleSearch = async () => {
        if (!query.trim()) return;
        setLoading(true);
        try {
            const data = await tvSeriesService.searchSeries(query); // API must return poster_path!
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
            await tvSeriesService.saveSeries(tmdbId);
            if (onSeriesAdded) {
                onSeriesAdded();
            }
            toast.success(`"${name}" added to Library! 🟢`);
        } catch (err) {
            console.error(err);
        }
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
                <Typography variant="h5" gutterBottom>
                    Search TV Series
                </Typography>

                <Stack direction="row" spacing={2} sx={{ mb: 3 }}>
                    <TextField
                        fullWidth
                        label="Enter series title"
                        variant="outlined"
                        size="small"
                        value={query}
                        onChange={(e) => setQuery(e.target.value)}
                        onKeyDown={(e) => {
                            if (e.key === "Enter") handleSearch();
                        }}
                        sx={{ backgroundColor: "#1a1d29" }}
                        InputProps={{ style: { color: "#eaf1fd" } }}
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
                                        primaryTypographyProps={{ color: "#eaf1fd" }}
                                        secondaryTypographyProps={{ color: "#a0aec0" }}
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
        </>
    );
};

export default SearchSeries;
