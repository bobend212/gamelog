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
        color: "primary",
        label: "WATCHING",
        icon: <PlayCircleFilledWhiteIcon fontSize="small" />
    },
    UP_TO_DATE: {
        value: "UP_TO_DATE",
        color: "success",
        label: "UP TO DATE",
        icon: <CheckCircleOutlineIcon fontSize="small" />
    },
    COMPLETED: {
        value: "COMPLETED",
        color: "secondary",
        label: "COMPLETED",
        icon: <DoneAllIcon fontSize="small" />
    },
    ON_HOLD: {
        value: "ON_HOLD",
        color: "warning",
        label: "ON HOLD",
        icon: <PauseCircleFilledIcon fontSize="small" />
    },
    DROPPED: {
        value: "DROPPED",
        color: "error",
        label: "DROPPED",
        icon: <CancelIcon fontSize="small" />
    },
    WISHLIST: {
        value: "WISHLIST",
        color: "error",
        label: "WISHLIST",
        icon: <ScheduleIcon fontSize="small" />
    }
};
