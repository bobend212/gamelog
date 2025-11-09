import React, { useState } from 'react';
import { Button, Typography, Box, Snackbar, Alert } from '@mui/material';
import moviesService from '../services/moviesService';
import MoviesNavbar from '../Navbar/MoviesNavbar';

const SyncMovies = ({ onSyncComplete }) => {
    const [syncLoading, setSyncLoading] = useState(false);
    const [syncSuccess, setSyncSuccess] = useState(false);
    const [syncError, setSyncError] = useState(null);
    const [syncSummary, setSyncSummary] = useState(null);

    const handleSyncClick = async () => {
        setSyncLoading(true);
        setSyncError(null);
        try {
            const result = await moviesService.syncMovies(0);
            setSyncSummary(result);
            setSyncSuccess(true);
            if (onSyncComplete) onSyncComplete();
        } catch (error) {
            setSyncError(error.message || 'Sync failed');
            setSyncSuccess(false);
        } finally {
            setSyncLoading(false);
        }
    };

    return (
        <>
            <MoviesNavbar />
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
                <Typography sx={{ textAlign: "center" }} variant="h5" gutterBottom>
                    Sync Movies
                </Typography>

                <Button
                    variant="contained"
                    color="primary"
                    onClick={handleSyncClick}
                    disabled={syncLoading}
                    fullWidth
                >
                    {syncLoading ? 'Syncing...' : 'Sync'}
                </Button>

                {syncSummary && (
                    <Box sx={{ color: '#9ca3af', textAlign: 'center' }}>
                        <Typography>Movies Processed: {syncSummary.itemsProcessed}</Typography>
                        <Typography>Movies Updated: {syncSummary.itemsUpdated}</Typography>
                        {syncSummary.fieldChanges.length > 0 &&
                            <div>
                                <h3>Updated Movies Details</h3>
                                {/* <ul> */}
                                {syncSummary.fieldChanges.map((field, index) => (
                                    <ul>
                                        <li key={field.id}><strong>{field.title}</strong></li>
                                        <li key={field.id}>
                                            {field.fieldName}: <del>{field.oldValue ?? 'null'}</del> → <ins>{field.newValue ?? 'null'}</ins>
                                        </li>
                                    </ul>
                                ))}
                                {/* </ul> */}
                            </div>
                        }
                    </Box>
                )}

                <Snackbar
                    open={syncSuccess}
                    autoHideDuration={3000}
                    onClose={() => setSyncSuccess(false)}
                >
                    <Alert severity="success" onClose={() => setSyncSuccess(false)}>
                        Library synced successfully!
                    </Alert>
                </Snackbar>

                <Snackbar
                    open={Boolean(syncError)}
                    autoHideDuration={4000}
                    onClose={() => setSyncError(null)}
                >
                    <Alert severity="error" onClose={() => setSyncError(null)}>
                        {syncError}
                    </Alert>
                </Snackbar>
            </Box>
        </>
    );
};

export default SyncMovies;
