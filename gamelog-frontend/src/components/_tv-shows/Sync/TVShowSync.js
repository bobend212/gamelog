import React, { useState } from 'react';
import {
    Button,
    Typography,
    Box,
    Snackbar,
    Alert,
    FormControl,
    InputLabel,
    Select,
    MenuItem
} from '@mui/material';
import Navbar from '../Navigation/Navbar';
import tvShowService from '../services/tvShowService';
import { TRACKING_TYPES } from '../utils/TVShowUtil';

const TVShowSync = ({ onSyncComplete }) => {
    const [selectedTrackingType, setSelectedTrackingType] = useState(TRACKING_TYPES.WATCHING.value);
    const [syncLoading, setSyncLoading] = useState(false);
    const [syncSuccess, setSyncSuccess] = useState(false);
    const [syncError, setSyncError] = useState(null);
    const [syncSummary, setSyncSummary] = useState(null);

    const handleSync = async () => {
        setSyncLoading(true);
        setSyncError(null);
        setSyncSummary(null);

        try {
            const result = await tvShowService.syncTVShows(selectedTrackingType);
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
                    Sync TV Shows
                </Typography>

                <FormControl fullWidth size="small" sx={{ mb: 3, mt: 1 }}>
                    <InputLabel
                        id="sync-status-label"
                        sx={{ color: "#cfd8dc", '&.Mui-focused': { color: "#90caf9" } }}
                    >
                        Sync by Tracking Type
                    </InputLabel>
                    <Select
                        labelId="sync-status-label"
                        value={selectedTrackingType}
                        label="Sync Target"
                        onChange={(e) => setSelectedTrackingType(e.target.value)}
                        sx={{
                            bgcolor: "#374579ff",
                            color: "white",
                            borderRadius: 2,
                            '.MuiSvgIcon-root': { color: "white" },
                            '& .MuiOutlinedInput-notchedOutline': { borderColor: '#5c6bc0' },
                            '&:hover .MuiOutlinedInput-notchedOutline': { borderColor: '#90caf9' },
                        }}
                        MenuProps={{
                            PaperProps: {
                                sx: {
                                    bgcolor: "#232634",
                                    color: "white",
                                    '& .MuiMenuItem-root': {
                                        '&:hover': { bgcolor: "#374579ff" },
                                        '&.Mui-selected': { bgcolor: "#3f51b5", '&:hover': { bgcolor: "#303f9f" } }
                                    }
                                }
                            }
                        }}
                    >

                        {Object.values(TRACKING_TYPES).map((option) => (
                            <MenuItem key={option.value} value={option.value}>
                                {option.label}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>

                <Button
                    variant="contained"
                    color="primary"
                    onClick={handleSync}
                    disabled={syncLoading}
                    fullWidth
                    sx={{
                        py: 1.2,
                        fontWeight: 'bold',
                        textTransform: 'none',
                        fontSize: '1rem'
                    }}
                >
                    {syncLoading ? 'Syncing...' : 'Start Sync'}
                </Button>

                {syncSummary && (
                    <Box sx={{ mt: 3, color: '#9ca3af', textAlign: 'left', bgcolor: '#1b1e2b', p: 2, borderRadius: 1 }}>
                        <Typography variant="subtitle2" sx={{ color: '#eaf1fd', mb: 1, textAlign: 'center' }}>
                            Sync Results
                        </Typography>
                        <Typography variant="body2">Items processed: {syncSummary.itemsProcessed}</Typography>
                        <Typography variant="body2">Items Updated: {syncSummary.itemsUpdated}</Typography>

                        {syncSummary.fieldChanges.length > 0 && (
                            <Box sx={{ mt: 2 }}>
                                <Typography variant="subtitle2" sx={{ color: '#90caf9' }}>
                                    Details:
                                </Typography>
                                <ul style={{ paddingLeft: 20, marginTop: 4 }}>
                                    {syncSummary.fieldChanges.map((change, i) => (
                                        <li style={{ marginBottom: 8 }}>
                                            <strong style={{ color: '#eaf1fd' }}>{change.title}</strong>
                                            <ul style={{ paddingLeft: 15, marginTop: 2 }}>
                                                <li key={i} style={{ listStyle: 'none', fontSize: '0.85rem' }}>
                                                    <span style={{ color: '#b0bec5' }}>{change.fieldName}:</span>{' '}
                                                    <span style={{ color: '#ff8a80', textDecoration: 'line-through' }}>
                                                        {String(change.oldValue ?? 'null')}
                                                    </span>
                                                    {' → '}
                                                    <span style={{ color: '#69f0ae' }}>
                                                        {String(change.newValue ?? 'null')}
                                                    </span>
                                                </li>
                                            </ul>
                                        </li>
                                    ))}
                                </ul>
                            </Box>
                        )}
                    </Box>
                )}

                <Snackbar
                    open={syncSuccess}
                    autoHideDuration={3000}
                    onClose={() => setSyncSuccess(false)}
                    anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
                >
                    <Alert severity="success" variant="filled" onClose={() => setSyncSuccess(false)}>
                        Library synced successfully!
                    </Alert>
                </Snackbar>

                <Snackbar
                    open={Boolean(syncError)}
                    autoHideDuration={4000}
                    onClose={() => setSyncError(null)}
                    anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
                >
                    <Alert severity="error" variant="filled" onClose={() => setSyncError(null)}>
                        {syncError}
                    </Alert>
                </Snackbar>
            </Box>
        </>
    );
};

export default TVShowSync;
