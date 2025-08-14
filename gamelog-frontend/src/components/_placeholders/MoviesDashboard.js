// MoviesDashboard.js
import React from 'react';
import { Typography, Box, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';

const MoviesDashboard = () => {
    const navigate = useNavigate();

    return (
        <Box sx={{ p: 2 }}>
            <Button onClick={() => navigate('/')} variant="outlined" sx={{ mb: 2 }}>
                Back to Homepage
            </Button>
            <Typography variant="h5">Movies - Coming Soon!</Typography>
        </Box>
    );
};

export default MoviesDashboard;
