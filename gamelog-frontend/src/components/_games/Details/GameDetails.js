import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import gameService from '../services/gameService';
import LoadingSpinner from '../Common/LoadingSpinner';
import ErrorMessage from '../Common/ErrorMessage';
import Navbar from '../Navigation/Navbar';
import './GameDetails.css';

const GameDetails = () => {
    const { gameId } = useParams();

    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        loadGameDetails();
    }, [gameId]);

    const loadGameDetails = async () => {
        try {
            setLoading(true);
            const response = await gameService.getGameDetails(gameId);
            setData(response);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const formatDate = (dateString) => {
        if (!dateString) return 'TBA';
        return new Date(dateString).toLocaleDateString('en-GB', {
            day: '2-digit',
            month: 'long',
            year: 'numeric'
        });
    };

    if (loading) return <LoadingSpinner />;
    if (error) return <ErrorMessage message={error} />;

    const { game, details } = data;

    return (
        <>
            <Navbar />

            <div className="game-details-page">

                <div
                    className="hero"
                    style={{
                        backgroundImage: `url(${details.additionalImageUrl || game.imageUrl})`
                    }}
                >
                    <div className="hero-overlay" />

                    <div className="container hero-content">

                        <img
                            src={game.imageUrl}
                            alt={game.title}
                            className="hero-cover"
                        />

                        <div className="hero-info">
                            <h1>{game.title}</h1>

                            <div className="meta-row">

                                <div className="meta-section">
                                    <span className="meta-label">Release date</span>
                                    <div className="hero-meta">
                                        <span>{formatDate(game.releaseDate)}</span>
                                    </div>
                                </div>

                                <div className="meta-section">
                                    <span className="meta-label">Library Update</span>
                                    <div className="hero-meta">
                                        <span>{formatDate(game.updatedAt)}</span>
                                    </div>
                                </div>

                                {details.igdbLastUpdated && (
                                    <div className="meta-section">
                                        <span className="meta-label">IGDB Update</span>
                                        <div className="hero-meta">
                                            <span>{formatDate(details.igdbLastUpdated)}</span>
                                        </div>
                                    </div>
                                )}

                            </div>

                            <div className="meta-section user">
                                <span className="meta-label">My Progress</span>

                                <div className="hero-meta">
                                    <span className={`status ${game.status?.toLowerCase()}`}>
                                        {game.status}
                                    </span>

                                    {game.completedAt && (
                                        <span>{formatDate(game.completedAt)}</span>
                                    )}

                                    {game.platform && (
                                        <span>{game.platform}</span>
                                    )}

                                    {game.rating && (
                                        <span className="rating-inline">
                                            ⭐ {game.rating.toFixed(1)}{game.favourite && (<span>❤️</span>)}
                                        </span>
                                    )}

                                </div>
                            </div>

                            <div className="hero-actions">
                                {details.igdbUrl && (
                                    <a
                                        href={details.igdbUrl}
                                        target="_blank"
                                        rel="noreferrer"
                                        className="action-btn igdb"
                                    >
                                        <span>IGDB</span>
                                    </a>
                                )}
                            </div>
                        </div>

                    </div>
                </div>

                <div className="container details-body">

                    {game.notes && (
                        <div className="notes-card">
                            <div className="section-header">
                                <span className="section-icon">📝</span>
                                <h3>My Notes</h3>
                            </div>
                            <p>{game.notes}</p>
                        </div>
                    )}

                    <div className="description glass">
                        <div className="section-header">
                            <h3>Description</h3>
                        </div>

                        {details.summary ? (
                            <p className="summary">{details.summary}</p>
                        ) : (
                            <p className="muted">No description available.</p>
                        )}

                        {details.storyline && (
                            <div className="storyline">
                                <h4>Storyline</h4>
                                <p>{details.storyline}</p>
                            </div>
                        )}
                    </div>

                </div>

            </div>
        </>
    );
};

export default GameDetails;