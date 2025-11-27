import React from "react";
import { useNavigate } from "react-router-dom";
import { Box, Typography, Paper } from "@mui/material";
import Grid from '@mui/material/Grid';

const Homepage = () => {
    const navigate = useNavigate();

    const options = [
        { label: "TV Shows", path: "/tv-shows/dashboard" },
        { label: "Movies (to watch)", path: "/movies/dashboard" },
        { label: "Games", path: "/games/dashboard" },
    ];

    const handleSelection = (path) => {
        navigate(path);
    };

    return (
        <Box
            sx={{
                minHeight: "100vh",
                background: "linear-gradient(135deg, #1a1a1a 0%, #5a6152ff 100%)"
                ,
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                justifyContent: "center",
                p: 4,
                color: "white",
                textAlign: "center",
            }}
        >
            <Typography variant="h2" sx={{ fontWeight: "bold", mb: 3 }}>
                The Library
            </Typography>

            <Grid container spacing={4} justifyContent="center" sx={{ maxWidth: 700, mb: 4 }}>
                {options.map(({ label, path }) => (
                    <Grid size={{ xs: 12, sm: 6, md: 6 }} key={label}>
                        <Paper
                            elevation={8}
                            sx={{
                                p: 5,
                                cursor: "pointer",
                                backgroundColor: "rgba(255, 255, 255, 0.1)",
                                transition: "all 0.3s ease",
                                borderRadius: 3,
                                "&:hover": {
                                    backgroundColor: "rgba(255, 255, 255, 0.25)",
                                    transform: "scale(1.05)",
                                },
                                userSelect: "none",
                            }}
                            onClick={() => handleSelection(path)}
                        >
                            <Typography variant="h5" sx={{ fontWeight: "medium", color: "white" }}>
                                {label}
                            </Typography>
                        </Paper>
                    </Grid>
                ))}
            </Grid>

            <Typography variant="body1" sx={{ maxWidth: 600, opacity: 0.85, fontSize: "1.1rem" }}>
                Game data provided by&nbsp;
                <a
                    href="https://rawg.io/apidocs"
                    target="_blank"
                    rel="noopener noreferrer"
                    style={{ color: "#ffdd57", textDecoration: "underline" }}
                >
                    RAWG API
                </a>
                <br /> Movie and TV Show data provided by&nbsp;
                <a
                    href="https://www.themoviedb.org/documentation/api"
                    target="_blank"
                    rel="noopener noreferrer"
                    style={{ color: "#ffdd57", textDecoration: "underline" }}
                >
                    TMDB API
                </a>
            </Typography>
        </Box>
    );
};

export default Homepage;
