import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Button, Typography, Grid, Paper } from '@mui/material';

const Homepage = () => {
    const navigate = useNavigate();

    const handleSelection = (path) => {
        navigate(path);
    };

    return (
        <Box sx={{ p: 4, textAlign: 'center' }}>
            <Typography variant="h4" gutterBottom>Select media type</Typography>
            <Grid container spacing={4} justifyContent="center">
                {[
                    { label: 'TV Shows', path: '/tv-shows' },
                    { label: 'Movies', path: '/movies' },
                    { label: 'Games', path: '/games/dashboard' }
                ].map(({ label, path }) => (
                    <Grid item key={label} xs={12} sm={4} md={3}>
                        <Paper
                            elevation={3}
                            sx={{
                                p: 4,
                                cursor: 'pointer',
                                ':hover': { boxShadow: 6 },
                            }}
                            onClick={() => handleSelection(path)}
                        >
                            <Typography variant="h6">{label}</Typography>
                        </Paper>
                    </Grid>
                ))}
            </Grid>
        </Box>
    );
};

export default Homepage;
