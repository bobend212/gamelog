import React, { useState, useEffect, useMemo } from "react";
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    MenuItem,
    Select,
    FormControl,
    Stack,
    Box
} from "@mui/material";
import { TRACKING_TYPES } from '../utils/TVShowUtil';

const UpdateTrackingTypeDialog = ({ open, currentStatus, onSave, onClose }) => {
    const [status, setStatus] = useState(currentStatus || "WATCHING");

    useEffect(() => {
        if (open) {
            setStatus(currentStatus || "WATCHING");
        }
    }, [open, currentStatus]);

    const trackingOptions = useMemo(() => Object.values(TRACKING_TYPES), []);

    const handleSave = () => {
        onSave(status);
    };

    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="xs">
            <DialogTitle>Update Tracking Type</DialogTitle>

            <DialogContent sx={{ mt: 1 }}>
                <FormControl fullWidth>
                    <Select
                        value={status}
                        onChange={(e) => setStatus(e.target.value)}
                        sx={{ fontWeight: 600 }}
                    >
                        {trackingOptions.map((opt) => (
                            <MenuItem key={opt.value} value={opt.value}>
                                <Stack direction="row" alignItems="center" spacing={1}>
                                    {opt.icon}
                                    <Box component="span" fontWeight={600}>
                                        {opt.label}
                                    </Box>
                                </Stack>
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
            </DialogContent>

            <DialogActions sx={{ px: 3, pb: 2 }}>
                <Button onClick={onClose} color="inherit" variant="outlined">
                    Cancel
                </Button>
                <Button
                    onClick={handleSave}
                    color="primary"
                    variant="contained"
                >
                    Update
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default UpdateTrackingTypeDialog;
