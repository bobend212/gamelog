// src/Common/StatusDialog.jsx
import React, { useState, useEffect } from "react";
import {
    Dialog, DialogTitle, DialogContent, DialogActions,
    Button, MenuItem, Select, InputLabel, FormControl, Stack, Chip, Box
} from "@mui/material";
import MovieFilterIcon from "@mui/icons-material/MovieFilter";
import PlaylistAddCheckIcon from "@mui/icons-material/PlaylistAddCheck";
import CancelIcon from "@mui/icons-material/Cancel";
import StarOutlineIcon from "@mui/icons-material/StarOutline";
import { TRACKING_TYPES } from "../utils/constants";

// All status options
const statusOptions = [
    {
        value: "WATCHING",
        label: "Watching",
        color: "info",
        icon: <MovieFilterIcon fontSize="small" />
    },
    {
        value: "COMPLETED",
        label: "Completed",
        color: "success",
        icon: <PlaylistAddCheckIcon fontSize="small" />
    },
    {
        value: "DROPPED",
        label: "Dropped",
        color: "error",
        icon: <CancelIcon fontSize="small" />
    },
    {
        value: "WISHLIST",
        label: "Wishlist",
        color: "warning",
        icon: <StarOutlineIcon fontSize="small" />
    }
];

const StatusDialog = ({ open, currentStatus, onSave, onClose }) => {
    const [status, setStatus] = useState(currentStatus || "WATCHING");

    useEffect(() => {
        if (open) setStatus(currentStatus || "WATCHING");
    }, [open, currentStatus]);

    const selected = Object.values(TRACKING_TYPES).find(opt => opt.value === status);

    return (
        <Dialog open={open} onClose={onClose}>
            <DialogTitle>Edit Tracking Type</DialogTitle>
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
