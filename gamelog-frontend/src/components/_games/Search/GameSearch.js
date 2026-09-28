import React, { useState } from 'react';
import gameService from '../services/gameService';
import { toast } from 'react-toastify';
import EditGameModal from '../Library/EditGameModal';
import LoadingSpinner from '../Common/LoadingSpinner';
import ErrorMessage from '../Common/ErrorMessage';
import { POPULAR_SEARCHES } from '../utils/constants';
import Navbar from '../Navigation/Navbar';

import {
  Box,
  Button,
  Chip,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Typography
} from '@mui/material';

const GameSearch = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [hasSearched, setHasSearched] = useState(false);
  const [isAdding, setIsAdding] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [gameData, setGameData] = useState(null);
  const [selectedGame, setSelectedGame] = useState(null);

  const handleSearch = async (query = searchQuery) => {
    if (!query.trim()) {
      setError('Please enter a search term');
      return;
    }

    try {
      setLoading(true);
      setError(null);

      const results = await gameService.searchGames(query.trim());

      setSearchResults(results);
      setHasSearched(true);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    handleSearch(searchQuery);
  };

  const handleQuickSearch = (query) => {
    setSearchQuery(query);
    handleSearch(query);
  };

  const formatDate = (dateString) => {
    if (!dateString) {
      return 'TBA';
    }

    try {
      return new Date(dateString).toLocaleDateString('en-GB', {
        day: '2-digit',
        month: 'long',
        year: 'numeric'
      });
    } catch {
      return 'TBA';
    }
  };

  const handleSaveGame = async (game, gameStatus) => {
    try {
      setIsAdding(true);

      const result = await gameService.saveGame(
        game.externalId,
        gameStatus
      );

      if (gameStatus === 'WISHLIST') {
        toast.success(`"${game.title}" saved to Wishlist!`);
      } else {
        toast.success(`"${game.title}" saved to Backlog!`);
        setSelectedGame(game);
        setGameData(result);
        setShowEditModal(true);
      }
    } catch (error) {
      if (error.status === 409) {
        toast.warning(error.message, {
          autoClose: 3000,
          position: 'bottom-right',
          theme: 'dark'
        });
      } else {
        toast.error('Game save failed');
      }
    } finally {
      setIsAdding(false);
    }
  };

  const handleCloseModal = () => {
    setShowEditModal(false);
    setGameData(null);
    setSelectedGame(null);
  };

  const handleEditSave = async (updatedGame) => {
    try {
      await gameService.updateGame(gameData.id, updatedGame);

      setShowEditModal(false);
      setGameData(null);
      setSelectedGame(null);

      toast.success(`"${selectedGame.title}" saved to Library!`);

      setSearchQuery('');
      setSearchResults([]);
      setHasSearched(false);
    } catch (error) {
      console.error('Failed to update game:', error);
      toast.error('Failed to update game. Please try again.');
    }
  };

  return (
    <>
      <Navbar />

      <Box
        sx={{
          minHeight: 'calc(100vh - 64px)',
          backgroundColor: '#1a1a1a',
          color: '#f5f5f5',
          py: 4
        }}
      >
        <Box
          sx={{
            maxWidth: 1400,
            mx: 'auto',
            px: { xs: 2, md: 4 }
          }}
        >
          <Typography
            variant="h4"
            component="h1"
            sx={{
              fontWeight: 700,
              mb: 3,
              color: '#f5f5f5'
            }}
          >
            Search Games
          </Typography>

          <Paper
            elevation={0}
            sx={{
              p: { xs: 2, md: 3 },
              mb: 4,
              backgroundColor: '#1e1e1e',
              border: '1px solid #333',
              borderRadius: 2
            }}
          >
            <Box
              component="form"
              onSubmit={handleSubmit}
            >
              <Stack
                direction={{ xs: 'column', sm: 'row' }}
                spacing={2}
              >
                <TextField
                  fullWidth
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  placeholder="Search for games..."
                  disabled={loading}
                  size="small"
                  sx={{
                    '& .MuiOutlinedInput-root': {
                      color: '#f5f5f5',
                      backgroundColor: '#121212',
                      '& fieldset': {
                        borderColor: '#444'
                      },
                      '&:hover fieldset': {
                        borderColor: '#666'
                      },
                      '&.Mui-focused fieldset': {
                        borderColor: '#90caf9'
                      }
                    },
                    '& .MuiInputBase-input::placeholder': {
                      color: '#888',
                      opacity: 1
                    }
                  }}
                />

                <Button
                  type="submit"
                  variant="contained"
                  sx={{
                    minWidth: 120
                  }}
                >
                  {loading ? 'Searching...' : 'Search'}
                </Button>
              </Stack>
            </Box>

            <Stack
              direction="row"
              spacing={1}
              flexWrap="wrap"
              useFlexGap
              sx={{ mt: 2 }}
            >
              {POPULAR_SEARCHES.map((term) => (
                <Chip
                  key={term}
                  label={term}
                  onClick={() => handleQuickSearch(term)}
                  disabled={loading}
                  clickable
                  sx={{
                    backgroundColor: '#292929',
                    color: '#ddd',
                    border: '1px solid #3a3a3a',
                    '&:hover': {
                      backgroundColor: '#333'
                    }
                  }}
                />
              ))}
            </Stack>
          </Paper>

          {error && (
            <Box sx={{ mb: 3 }}>
              <ErrorMessage message={error} />
            </Box>
          )}

          {hasSearched && (
            <Box>
              <Stack
                direction={{ xs: 'column', sm: 'row' }}
                justifyContent="space-between"
                alignItems={{ xs: 'flex-start', sm: 'center' }}
                spacing={1}
                sx={{ mb: 2 }}
              >
                <Typography
                  variant="h5"
                  component="h2"
                  sx={{
                    fontWeight: 700,
                    color: '#f5f5f5'
                  }}
                >
                  Search Results
                </Typography>

                {searchResults.length > 0 && (
                  <Typography
                    variant="body2"
                    sx={{ color: '#999' }}
                  >
                    most accurate results for "{searchQuery}"
                  </Typography>
                )}
              </Stack>

              {searchResults.length > 0 ? (
                <TableContainer
                  component={Paper}
                  elevation={0}
                  sx={{
                    backgroundColor: '#1e1e1e',
                    border: '1px solid #333',
                    borderRadius: 2,
                    overflow: 'hidden'
                  }}
                >
                  <Table>
                    <TableHead>
                      <TableRow
                        sx={{
                          backgroundColor: '#252525'
                        }}
                      >
                        <TableCell
                          sx={{
                            width: 70,
                            color: '#aaa',
                            fontWeight: 600,
                            borderBottom: '1px solid #333'
                          }}
                        >
                          Cover
                        </TableCell>

                        <TableCell
                          sx={{
                            color: '#aaa',
                            fontWeight: 600,
                            borderBottom: '1px solid #333'
                          }}
                        >
                          Title
                        </TableCell>

                        <TableCell
                          sx={{
                            color: '#aaa',
                            fontWeight: 600,
                            borderBottom: '1px solid #333'
                          }}
                        >
                          Release Date
                        </TableCell>

                        <TableCell
                          sx={{
                            width: 220,
                            borderBottom: '1px solid #333'
                          }}
                        />
                      </TableRow>
                    </TableHead>

                    <TableBody>
                      {searchResults.map((game) => (
                        <TableRow
                          key={game.externalId}
                          sx={{
                            '&:last-child td': {
                              borderBottom: 0
                            },
                            '&:hover': {
                              backgroundColor: '#292929'
                            }
                          }}
                        >
                          <TableCell
                            sx={{
                              borderBottom: '1px solid #333',
                              py: 1.5
                            }}
                          >
                            {game.imageUrl ? (
                              <Box
                                component="img"
                                src={game.imageUrl}
                                alt={game.title}
                                sx={{
                                  width: 45,
                                  height: 60,
                                  objectFit: 'cover',
                                  borderRadius: 1,
                                  display: 'block'
                                }}
                              />
                            ) : (
                              <Box
                                sx={{
                                  width: 45,
                                  height: 60,
                                  borderRadius: 1,
                                  backgroundColor: '#292929',
                                  display: 'flex',
                                  alignItems: 'center',
                                  justifyContent: 'center',
                                  color: '#777'
                                }}
                              >
                                -
                              </Box>
                            )}
                          </TableCell>

                          <TableCell
                            sx={{
                              borderBottom: '1px solid #333'
                            }}
                          >
                            <Typography
                              variant="body1"
                              sx={{
                                fontWeight: 600,
                                color: '#f5f5f5'
                              }}
                            >
                              {game.title}
                            </Typography>
                          </TableCell>

                          <TableCell
                            sx={{
                              borderBottom: '1px solid #333'
                            }}
                          >
                            <Typography
                              variant="body2"
                              sx={{ color: '#999' }}
                            >
                              {formatDate(game.releaseDate)}
                            </Typography>
                          </TableCell>

                          <TableCell
                            align="right"
                            sx={{
                              borderBottom: '1px solid #333'
                            }}
                          >
                            <Stack
                              direction="row"
                              spacing={1}
                              justifyContent="flex-end"
                            >
                              <Button
                                variant="contained"
                                size="small"
                                disabled={isAdding}
                                onClick={() =>
                                  handleSaveGame(
                                    game,
                                    'BACKLOG'
                                  )
                                }
                              >
                                Library
                              </Button>

                              <Button
                                variant="outlined"
                                size="small"
                                disabled={isAdding}
                                onClick={() =>
                                  handleSaveGame(
                                    game,
                                    'WISHLIST'
                                  )
                                }
                                sx={{
                                  color: '#ce93d8',
                                  borderColor: '#ce93d8',
                                  '&:hover': {
                                    borderColor: '#e1bee7',
                                    backgroundColor: 'rgba(206, 147, 216, 0.08)'
                                  }
                                }}
                              >
                                Wishlist
                              </Button>
                            </Stack>
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              ) : (
                !loading && (
                  <Paper
                    elevation={0}
                    sx={{
                      p: 4,
                      textAlign: 'center',
                      backgroundColor: '#1e1e1e',
                      border: '1px solid #333',
                      borderRadius: 2
                    }}
                  >
                    <Typography
                      variant="h6"
                      gutterBottom
                      sx={{ color: '#f5f5f5' }}
                    >
                      No games found.
                    </Typography>

                    <Typography
                      variant="body2"
                      sx={{ color: '#999' }}
                    >
                      Try searching for a different game.
                    </Typography>
                  </Paper>
                )
              )}
            </Box>
          )}

          <Typography
            variant="caption"
            sx={{
              display: 'block',
              textAlign: 'center',
              mt: 4,
              color: '#666'
            }}
          >
            metadata by IGDB API
          </Typography>

          {loading && <LoadingSpinner />}
        </Box>
      </Box>

      {showEditModal && gameData && (
        <EditGameModal
          game={gameData}
          onSave={handleEditSave}
          onCancel={handleCloseModal}
        />
      )}
    </>
  );
};

export default GameSearch;