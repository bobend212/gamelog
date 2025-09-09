import React, { useState } from 'react';
import { Button, Typography, Box, Snackbar, Alert } from '@mui/material';
import Navbar from '../Navigation/Navbar';
import tvSeriesService from '../services/tvSeriesService';

const SyncSeries = ({ onSyncComplete }) => {
    const [syncLoading, setSyncLoading] = useState(false);
    const [syncSuccess, setSyncSuccess] = useState(false);
    const [syncError, setSyncError] = useState(null);
    const [syncSummary, setSyncSummary] = useState(null);

    const handleSyncClick = async () => {
        setSyncLoading(true);
        setSyncError(null);
        try {
            const result = await tvSeriesService.syncSeries(0);
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
                <Typography sx={{ textAlign: "center" }} variant="h5" gutterBottom>
                    Sync TV Series
                </Typography>

                <Typography sx={{ textAlign: "center" }} variant="body2" gutterBottom>
                    Excluded: COMPLETED, ON HOLD, DROPPED, WISHLIST
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
                        <Typography>Total Checked: {syncSummary.totalChecked}</Typography>
                        <Typography>Series Updated: {syncSummary.updatedCount}</Typography>
                        {syncSummary.changes.length > 0 &&
                            <div>
                                <h3>Updated TV Series Details</h3>
                                <ul>
                                    {syncSummary.changes.map(change => (
                                        <li key={change.mediaId}>
                                            <strong>{change.mediaName}</strong>
                                            <ul>
                                                {change.fieldChanges.map((field, index) => (
                                                    <li style={{ listStyle: 'none' }} key={index}>
                                                        {field.fieldName}: <del>{field.oldValue ?? 'null'}</del> → <ins>{field.newValue ?? 'null'}</ins>
                                                    </li>
                                                ))}
                                            </ul>
                                        </li>
                                    ))}
                                </ul>
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

export default SyncSeries;
