import React, { useState, useEffect } from "react";
import {
    Dialog, DialogTitle, DialogContent, DialogActions,
    Button, MenuItem, Select, FormControl, Stack, Chip, Box
} from "@mui/material";
import { TRACKING_TYPES } from '../utils/tvSeriesUtil';

const StatusDialog = ({ open, currentStatus, onSave, onClose }) => {
    const [status, setStatus] = useState(currentStatus || "WATCHING");

    useEffect(() => {
        if (open) setStatus(currentStatus || "WATCHING");
    }, [open, currentStatus]);

    const selected = Object.values(TRACKING_TYPES).find(opt => opt.value === status);

    return (
        <Dialog open={open} onClose={onClose}>
            <DialogTitle>Change Status</DialogTitle>
            <DialogContent sx={{ minWidth: 320, mt: 2 }}>
                <FormControl fullWidth>
                    <Select
                        value={status}
                        onChange={e => setStatus(e.target.value)}
                        sx={{ fontWeight: 600 }}
                    >
                        {Object.values(TRACKING_TYPES).map(opt =>
                            <MenuItem key={opt.value} value={opt.value}>
                                <Stack direction="row" alignItems="center" spacing={1}>
                                    {opt.icon}
                                    <Box component="span" fontWeight={600}>{opt.label}</Box>
                                </Stack>
                            </MenuItem>
                        )}
                    </Select>
                </FormControl>

                <Box mt={3} textAlign="center">
                    <Chip
                        icon={selected.icon}
                        label={selected.label}
                        color={selected.color}
                        size="medium"
                        sx={{ fontWeight: 700, fontSize: 16, px: 1.8, minWidth: 120 }}
                    />
                </Box>
            </DialogContent>
            <DialogActions sx={{ px: 3, pb: 2 }}>
                <Button onClick={onClose} color="inherit" variant="outlined">Cancel</Button>
                <Button
                    onClick={() => onSave(status)}
                    color="primary"
                    variant="contained"
                >
                    Save
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default StatusDialog;
