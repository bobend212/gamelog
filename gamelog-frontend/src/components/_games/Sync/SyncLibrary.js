import React, { useState } from 'react';
import { Button, Select, MenuItem, Typography, Box, Snackbar, Alert } from '@mui/material';
import gameService from '../services/gameService';
import Navbar from '../Navigation/Navbar';

const SyncLibrary = ({ onSyncComplete }) => {
    const [syncStatus, setSyncStatus] = useState('WISHLIST');
    const [syncLoading, setSyncLoading] = useState(false);
    const [syncSuccess, setSyncSuccess] = useState(false);
    const [syncError, setSyncError] = useState(null);
    const [syncSummary, setSyncSummary] = useState(null);

    const handleSyncClick = async () => {
        setSyncLoading(true);
        setSyncError(null);
        try {
            const result = await gameService.syncLibraryGames(syncStatus);
            console.log(result)
            setSyncSummary(result);
            setSyncSuccess(true);
            if (onSyncComplete) onSyncComplete();  // notify parent if needed
        } catch (error) {
            setSyncError(error.message || 'Sync failed');
            setSyncSuccess(false);
        } finally {
            setSyncLoading(false);
        }
    };

    return (
        <>
            <Navbar />
            <Box
                sx={{
                    backgroundColor: '#2d2d2d',
                    padding: 2,
                    borderRadius: 2,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    gap: 2,
                    maxWidth: 400,
                    margin: 'auto',
                    marginTop: 2
                }}
            >
                <Typography variant="h6" sx={{ color: '#ffffff' }}>
                    Sync Game Library
                </Typography>

                <Typography variant="body2" sx={{ color: '#ffffff' }}>
                    Select Game status to sync
                </Typography>

                <Select
                    value={syncStatus}
                    onChange={(e) => setSyncStatus(e.target.value)}
                    variant="outlined"
                    size="small"
                    sx={{ backgroundColor: '#1a1a1a', color: '#fff', width: '100%', textAlign: 'center' }}
                >
                    <MenuItem value="WISHLIST">Wishlist</MenuItem>
                    <MenuItem value="PLAYING">Playing</MenuItem>
                    <MenuItem value="COMPLETED">Completed</MenuItem>
                    <MenuItem value="BACKLOG">Backlog</MenuItem>
                    <MenuItem value="DROPPED">Dropped</MenuItem>
                    <MenuItem value="ONLINE">Online</MenuItem>
                </Select>

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
                        <Typography>Games Processed: {syncSummary.itemsProcessed}</Typography>
                        <Typography>Games Updated: {syncSummary.itemsUpdated}</Typography>
                        {syncSummary.fieldChanges.length > 0 &&
                            <div>
                                <h3>Updated Games Details</h3>
                                {/* <ul> */}
                                {syncSummary.fieldChanges.map((field, index) => (
                                    <ul>
                                        <li key={index}><strong>{field.title}</strong></li>
                                        <li key={index}>
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

export default SyncLibrary;
