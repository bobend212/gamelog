import React, { useState } from "react";
import {
    Box,
    IconButton,
    Typography,
    Card,
    CardContent,
    CardMedia
} from "@mui/material";
import ArrowBackIosNewIcon from "@mui/icons-material/ArrowBackIosNew";
import ArrowForwardIosIcon from "@mui/icons-material/ArrowForwardIos";
import { useNavigate } from "react-router-dom";

const cardWidth = 170;
const cardsAtOnce = 6;
const gapSize = 12;

export default function GameCardCarousel({ games, header }) {
    const navigate = useNavigate();
    const [startIdx, setStartIdx] = useState(0);

    const canGoBack = startIdx > 0;
    const canGoForward = startIdx + cardsAtOnce < games.length;

    const handlePrev = () => setStartIdx(Math.max(0, startIdx - cardsAtOnce));
    const handleNext = () =>
        setStartIdx(Math.min(games.length - cardsAtOnce, startIdx + cardsAtOnce));

    const handleNavigate = (id) => {
        navigate(`/games/details/${id}`);
    };

    const formatDateToCustomString = (dateString) => {
        const date = new Date(dateString);
        const monthNames = [
            "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
            "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
        ];
        return `${monthNames[date.getMonth()]} ${date.getDate()}, ${date.getFullYear()} `;
    };

    const isToday = (dateString) => {
        if (!dateString) return false;

        const today = new Date();
        const date = new Date(dateString);

        return (
            today.getFullYear() === date.getFullYear() &&
            today.getMonth() === date.getMonth() &&
            today.getDate() === date.getDate()
        );
    };

    return (
        <Box
            sx={{
                position: "relative",
                py: 1,
                pr: 4,
                pl: 2,
                background: "#191919",
                borderRadius: 3,
                overflow: "visible"
            }}
        >
            {header && (
                <Typography variant="h5" sx={{ mb: 2, color: "#fff", ml: 2 }}>
                    {header}
                </Typography>
            )}

            <Box sx={{ display: "flex", alignItems: "center" }}>

                <IconButton
                    onClick={handlePrev}
                    disabled={!canGoBack}
                    sx={{
                        color: "#fff",
                        bgcolor: "#222",
                        mr: 2,
                        opacity: canGoBack ? 1 : 0.4,
                        "&:hover": { bgcolor: "#333" }
                    }}
                >
                    <ArrowBackIosNewIcon fontSize="small" />
                </IconButton>

                <Box
                    sx={{
                        display: "flex",
                        gap: 1.2,
                        overflow: "visible",
                        width: `calc(${cardWidth}px * ${cardsAtOnce} + ${gapSize}px * ${cardsAtOnce - 1})`
                    }}
                >
                    {games
                        .slice(startIdx, startIdx + cardsAtOnce)
                        .map((game) => (
                            <Card
                                key={game.id}
                                onClick={() => handleNavigate(game.id)}
                                sx={{
                                    width: cardWidth,
                                    minWidth: cardWidth,
                                    height: 200,
                                    borderRadius: 3,
                                    overflow: "hiddven",
                                    position: "relative",
                                    cursor: "pointer",
                                    bgcolor: "#111",
                                    transition: "all 0.25s ease",

                                    "&:hover": {
                                        transform: "translateY(-5px) scale(1.02)",
                                        boxShadow: "0 12px 35px rgba(0,0,0,0.7)"
                                    },

                                    "&:hover .image": {
                                        transform: "scale(1.08)"
                                    }
                                }}
                            >
                                {/* IMAGE */}
                                <Box
                                    className="image"
                                    sx={{
                                        position: "absolute",
                                        inset: 0,
                                        backgroundImage: `url(${game.imageUrl})`,
                                        backgroundSize: "cover",
                                        backgroundPosition: "center",
                                        transition: "transform 0.4s ease"
                                    }}
                                />

                                {/* GRADIENT */}
                                <Box
                                    sx={{
                                        position: "absolute",
                                        inset: 0,
                                        background: `
                linear-gradient(to top, rgba(0,0,0,0.95) 25%, transparent 70%),
                linear-gradient(to right, rgba(0,0,0,0.4), transparent)
            `
                                    }}
                                />

                                {/* UPCOMING BADGE */}
                                {(game.daysToRelease !== undefined && game.daysToRelease !== null) || isToday(game.releaseDate) ? (
                                    <Box
                                        sx={{
                                            position: "absolute",
                                            top: 8,
                                            left: 8,
                                            px: 1,
                                            py: 0.3,
                                            borderRadius: 2,
                                            fontSize: "0.6rem",
                                            fontWeight: 700,
                                            backdropFilter: "blur(6px)",
                                            color: "#fff",

                                            background:
                                                isToday(game.releaseDate)
                                                    ? "rgba(34,197,94,0.95)"
                                                    : "rgba(59,130,246,0.9)"
                                        }}
                                    >
                                        {isToday(game.releaseDate)
                                            ? "Today!"
                                            : `${game.daysToRelease} days`}
                                    </Box>
                                ) : null}

                                {/* CONTENT */}
                                <Box
                                    sx={{
                                        position: "absolute",
                                        bottom: 0,
                                        width: "100%",
                                        p: 1,
                                        display: "flex",
                                        flexDirection: "column",
                                        gap: 0.4
                                    }}
                                >
                                    <Typography
                                        variant="body2"
                                        sx={{
                                            fontWeight: 700,
                                            fontSize: "0.8rem",
                                            lineHeight: 1.2,
                                            display: "-webkit-box",
                                            WebkitLineClamp: 2,
                                            WebkitBoxOrient: "vertical",
                                            overflow: "hidden",
                                            color: "#fff"
                                        }}
                                    >
                                        {game.title}
                                    </Typography>

                                    {/* RELEASE DATE */}
                                    {game.releaseDate && (
                                        <Typography
                                            variant="caption"
                                            sx={{ color: "#bbb", fontSize: "0.7rem" }}
                                        >
                                            {formatDateToCustomString(game.releaseDate)}
                                        </Typography>
                                    )}

                                    {/* UPCOMING TEXT (ważne 🔥) */}
                                    {/* {game.daysToRelease && (
                                        <Typography
                                            variant="caption"
                                            sx={{
                                                fontSize: "0.7rem",
                                                color: "#60a5fa",
                                                fontWeight: 600
                                            }}
                                        >
                                            Coming soon
                                        </Typography>
                                    )} */}

                                    {/* STATUS */}
                                    {game.status && !game.daysToRelease && (
                                        <Typography
                                            variant="caption"
                                            sx={{
                                                fontWeight: 700,
                                                fontSize: "0.7rem",
                                                color: "#4ade80"
                                            }}
                                        >
                                            {game.status}
                                        </Typography>
                                    )}
                                </Box>
                            </Card>
                        ))}
                </Box>

                <IconButton
                    onClick={handleNext}
                    disabled={!canGoForward}
                    sx={{
                        color: "#fff",
                        bgcolor: "#222",
                        ml: 2,
                        opacity: canGoForward ? 1 : 0.4,
                        "&:hover": { bgcolor: "#333" }
                    }}
                >
                    <ArrowForwardIosIcon fontSize="small" />
                </IconButton>

            </Box>
        </Box>
    );
}