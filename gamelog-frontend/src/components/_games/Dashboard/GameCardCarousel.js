import React, { useState } from "react";
import { Box, IconButton, Typography, Card, CardContent, CardMedia } from "@mui/material";
import ArrowBackIosNewIcon from "@mui/icons-material/ArrowBackIosNew";
import ArrowForwardIosIcon from "@mui/icons-material/ArrowForwardIos";

const cardWidth = 200;
const cardsAtOnce = 5;
const gapSize = 16; // gap=2 in MUI spacing = 8 * 2

export default function GameCardCarousel({ games, header }) {
    const [startIdx, setStartIdx] = useState(0);

    const canGoBack = startIdx > 0;
    const canGoForward = startIdx + cardsAtOnce < games.length;

    const handlePrev = () => setStartIdx(Math.max(0, startIdx - cardsAtOnce));
    const handleNext = () => setStartIdx(Math.min(games.length - cardsAtOnce, startIdx + cardsAtOnce));

    const formatDateToCustomString = (dateString) => {
        const date = new Date(dateString);
        const monthNames = ["JAN", "FEB", "MAR", "APR", "MAY", "JUN",
            "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"];
        const month = monthNames[date.getMonth()];
        const day = date.getDate();
        const year = date.getFullYear();
        return `${month} ${day}, ${year}`;
    }
    return (
        <Box
            sx={{
                position: "relative",
                px: 0,
                py: 0.5,
                background: "#191919",
                borderRadius: 3,
                overflow: "hidden"
            }}
        >
            {header && (
                <Typography variant="h5" sx={{ mb: 2, color: "#fff", ml: 2, textAlign: "left" }}>
                    {header}
                </Typography>
            )}
            <Box sx={{ display: "flex", alignItems: "center" }}>
                <IconButton
                    aria-label="previous"
                    onClick={handlePrev}
                    disabled={!canGoBack}
                    sx={{
                        color: "#fff",
                        bgcolor: "#222",
                        mr: 2,
                        opacity: canGoBack ? 1 : 0.4,
                        "&:hover": { bgcolor: "#333" },
                    }}
                >
                    <ArrowBackIosNewIcon />
                </IconButton>
                <Box
                    sx={{
                        display: "flex",
                        gap: 1.5,
                        overflow: "hidden",
                        flexWrap: "nowrap",
                        width: `calc(${cardWidth}px * ${cardsAtOnce} + ${gapSize}px * ${cardsAtOnce - 1})`
                        // paddingBottom: 0
                        // minHeight: 270,
                    }}
                >
                    {games.slice(startIdx, startIdx + cardsAtOnce).map((game) => (
                        <Card
                            key={game.id}
                            sx={{
                                width: cardWidth,
                                minWidth: cardWidth,
                                borderRadius: 2,
                                bgcolor: "#232323",
                                color: "#fff",
                                boxShadow: 3,
                                display: "flex",
                                flexDirection: "column"
                            }}
                        >
                            <CardMedia
                                image={game.imageUrl}
                                title={game.title}
                                sx={{ height: 112, backgroundSize: "cover" }}
                            />
                            <CardContent sx={{ textAlign: "center" }} style={{ padding: 8 }}>
                                <Typography
                                    variant="body2"
                                    sx={{
                                        fontWeight: 600,
                                        whiteSpace: 'normal',
                                        wordBreak: 'break-word',
                                    }}
                                    gutterBottom
                                >
                                    {game.title}
                                </Typography>

                                <Typography variant="body2" sx={{ color: "#aaa" }}>
                                    {game.releaseDate ? `${formatDateToCustomString(game.releaseDate)}` : "TBA"}
                                </Typography>

                                {game.daysToRelease && (
                                    <Typography variant="body2" sx={{ color: "#aaa", fontWeight: 600 }}>
                                        {game.daysToRelease} days left
                                    </Typography>
                                )}

                                {game.status && (
                                    <Box sx={{ fontWeight: 600 }}>
                                        {game.status}
                                    </Box>
                                )}
                            </CardContent>
                        </Card>
                    ))}
                </Box>
                <IconButton
                    aria-label="next"
                    onClick={handleNext}
                    disabled={!canGoForward}
                    sx={{
                        color: "#fff",
                        bgcolor: "#222",
                        ml: 2,
                        opacity: canGoForward ? 1 : 0.4,
                        "&:hover": { bgcolor: "#333" },
                    }}
                >
                    <ArrowForwardIosIcon />
                </IconButton>
            </Box>
        </Box>
    );
}
