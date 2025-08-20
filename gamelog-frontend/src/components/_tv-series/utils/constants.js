import React from "react";
import PlayCircleFilledWhiteIcon from '@mui/icons-material/PlayCircleFilledWhite';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import DoneAllIcon from '@mui/icons-material/DoneAll';
import PauseCircleFilledIcon from '@mui/icons-material/PauseCircleFilled';
import CancelIcon from '@mui/icons-material/Cancel';
import ScheduleIcon from '@mui/icons-material/Schedule';

export const TRACKING_TYPES = {
    WATCHING: {
        value: "WATCHING",
        color: "info",
        label: "Watching",
        icon: <PlayCircleFilledWhiteIcon fontSize="small" />
    },
    UP_TO_DATE: {
        value: "UP_TO_DATE",
        color: "info",
        label: "Up To Date",
        icon: <CheckCircleOutlineIcon fontSize="small" />
    },
    COMPLETED: {
        value: "COMPLETED",
        color: "success",
        label: "Completed",
        icon: <DoneAllIcon fontSize="small" />
    },
    ON_HOLD: {
        value: "ON_HOLD",
        color: "warning",
        label: "On Hold",
        icon: <PauseCircleFilledIcon fontSize="small" />
    },
    DROPPED: {
        value: "DROPPED",
        color: "error",
        label: "Dropped",
        icon: <CancelIcon fontSize="small" />
    },
    WISHLIST: {
        value: "WISHLIST",
        color: "warning",
        label: "Wishlist",
        icon: <ScheduleIcon fontSize="small" />
    }
};
