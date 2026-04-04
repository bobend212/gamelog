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

const cardWidth = 200;
const cardsAtOnce = 5;
const gapSize = 16;

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

    return (
        <Box
            sx={{
                position: "relative",
                py: 1,
                background: "#191919",
                borderRadius: 3,
                overflow: "hidden"
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
                        overflow: "hidden",
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
                                    borderRadius: 2,
                                    bgcolor: "#232323",
                                    color: "#fff",
                                    boxShadow: 2,
                                    display: "flex",
                                    flexDirection: "column",
                                    cursor: "pointer",
                                    transition: "all 0.2s ease",
                                    "&:hover": {
                                        transform: "translateY(-3px)",
                                        boxShadow: 4
                                    }
                                }}
                            >
                                <CardMedia
                                    image={game.imageUrl}
                                    title={game.title}
                                    sx={{
                                        height: 120,
                                        backgroundSize: "cover"
                                    }}
                                />

                                <CardContent
                                    sx={{
                                        px: 0.7,
                                        py: 0.4,
                                        "&:last-child": {
                                            pb: 0.4
                                        },
                                        display: "flex",
                                        flexDirection: "column",
                                        alignItems: "center",
                                        textAlign: "center"
                                    }}
                                >
                                    <Typography
                                        variant="body2"
                                        sx={{
                                            fontWeight: 700,
                                            fontSize: "0.9rem",
                                            lineHeight: 1.2,
                                            display: "-webkit-box",
                                            WebkitLineClamp: 2,
                                            WebkitBoxOrient: "vertical",
                                            overflow: "hidden"
                                        }}
                                    >
                                        {game.title}
                                    </Typography>

                                    <Typography
                                        variant="caption"
                                        sx={{
                                            color: "#bbb",
                                            fontSize: "0.72rem"
                                        }}
                                    >
                                        {game.releaseDate
                                            ? formatDateToCustomString(game.releaseDate)
                                            : ""}
                                        {game.daysToRelease && (
                                            <Typography
                                                variant="caption"
                                                sx={{
                                                    color: "#bbb",
                                                    fontSize: "0.72rem"
                                                }}
                                            >
                                                - {game.daysToRelease} days left
                                            </Typography>
                                        )}
                                    </Typography>

                                    {game.status && (
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
                                </CardContent>
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