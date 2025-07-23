import React, { useState, useEffect } from 'react';
import gameService from '../../services/gameService';
import StatCard from './StatCard';
import GameCard from '../Library/GameCard';
import LoadingSpinner from '../Common/LoadingSpinner';
import ErrorMessage from '../Common/ErrorMessage';
import './Dashboard.css';
import {
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Paper, Typography
} from '@mui/material';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';

const Dashboard = () => {
  const [stats, setStats] = useState({
    totalLibrary: 0,
    totalWishlist: 0,
    completedGames: 0,
    currentlyPlaying: 0,
    backloggedGames: 0,
    droppedGames: 0,
    onlineGames: 0
  });
  const [lastEditedGames, setLastEditedGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [games, setGames] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalRows, setTotalRows] = useState(0);

  // Function to load dashboard data (stats + last edited)
  const loadDashboardData = async () => {
    try {
      setLoading(true);
      const [libraryGames, wishlistGames] = await Promise.all([
        gameService.getAllLibraryGames(),
        gameService.getAllWishlistGames(),
      ]);

      // Stats calculations
      setStats({
        totalLibrary: libraryGames.length,
        totalWishlist: wishlistGames.length,
        completedGames: libraryGames.filter(g => g.status === 'COMPLETED').length,
        currentlyPlaying: libraryGames.filter(g => g.status === 'PLAYING').length,
        backloggedGames: libraryGames.filter(g => g.status === 'BACKLOG').length,
        droppedGames: libraryGames.filter(g => g.status === 'DROPPED').length,
        onlineGames: libraryGames.filter(g => g.status === 'ONLINE').length,
      });

      // Last edited games slice
      const allGames = [...libraryGames, ...wishlistGames];
      const sortedGames = allGames
        .sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt))
        .slice(0, 4);
      setLastEditedGames(sortedGames);

    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const fetchWishlistGames = async () => {
    try {
      const response = await gameService.getWishlistGamesDashboard(
        page,
        rowsPerPage,
        'releaseDate,asc', // default sort
        'NOT_RELEASED_ONLY'
      );

      setGames(response.content);
      setTotalRows(response.totalElements);
    } catch (err) {
      console.error('Failed to fetch wishlist games:', err);
    }
  };

  useEffect(() => {
    loadDashboardData();
    fetchWishlistGames();
  }, []);

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorMessage message={error} />;

  const darkTheme = createTheme({
    palette: {
      mode: 'dark',
      primary: {
        main: '#90caf9',
      },
      background: {
        default: '#121212',
        paper: '#1d1d1d',
      },
    },
  });

  return (
    <div className="dashboard">
      <div className="container">
        <h1>Dashboard</h1>

        {/* Statistics Section */}
        <div className="stats-grid">
          <StatCard
            title="Logged"
            value={stats.totalLibrary}
            icon="📚"
            color="#3b82f6"
          />
          <StatCard
            title="Wishlisted"
            value={stats.totalWishlist}
            icon="❤️"
            color="#ef4444"
          />
          <StatCard
            title="Playing"
            value={stats.currentlyPlaying}
            icon="🎮"
            color="#8b5cf6"
          />
          <StatCard
            title="Completed"
            value={stats.completedGames}
            icon="✅"
            color="#10b981"
          />
          <StatCard
            title="Dropped"
            value={stats.droppedGames}
            icon="👎"
            color="#6b7280"
          />
          <StatCard
            title="Online / PVP"
            value={stats.onlineGames}
            icon="🌐"
            color="#38bdf8"
          />
        </div>

        {/* Last Edited Games Section */}
        <div className="last-edited-section">
          <h2>Recently Updated</h2>
          {lastEditedGames.length > 0 ? (
            <div className="games-grid">
              {lastEditedGames.map(game => (
                <GameCard
                  key={game.id}
                  game={game}
                  onUpdate={loadDashboardData}
                  showStatus={true}
                />
              ))}
            </div>
          ) : (
            <p className="no-games">No games found. Start by adding some games to your library!</p>
          )}
        </div>
        <div className="last-edited-section">
          <h2>Wishlist Games (not released)</h2>
          <ThemeProvider theme={darkTheme}>
            <CssBaseline />
            <Paper sx={{ width: '100%', overflow: 'hidden' }}>
              <TableContainer component={Paper} >
                <Table size="small" aria-label="wishlist table" color="secondary">
                  <TableHead>
                    <TableRow>
                      <TableCell>Title</TableCell>
                      <TableCell>Release Date</TableCell>
                      <TableCell>Days to Release</TableCell>
                    </TableRow>
                  </TableHead>

                  <TableBody>
                    {games.length > 0 ? (
                      games.map((game) => (
                        <TableRow key={game.id}>
                          <TableCell>{game.title}</TableCell>
                          <TableCell>{game.releaseDate || 'TBA'}</TableCell>
                          <TableCell>{game.daysToRelease != null ? `${game.daysToRelease} days` : '-'}</TableCell>
                        </TableRow>
                      ))
                    ) : (
                      <TableRow>
                        <TableCell colSpan={8}>
                          <Typography align="center" variant="body2" color="textSecondary">
                            No wishlist games found.
                          </Typography>
                        </TableCell>
                      </TableRow>
                    )}
                  </TableBody>
                </Table>
              </TableContainer>
            </Paper>
          </ThemeProvider>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;