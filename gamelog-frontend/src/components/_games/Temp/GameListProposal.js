import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Avatar,
  Box,
  Button,
  Chip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  FormControl,
  IconButton,
  InputAdornment,
  MenuItem,
  Paper,
  Select,
  Stack,
  TextField,
  ToggleButton,
  ToggleButtonGroup,
  Tooltip,
  Typography,
  useMediaQuery,
  useTheme,
} from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import SearchRoundedIcon from '@mui/icons-material/SearchRounded';
import TuneRoundedIcon from '@mui/icons-material/TuneRounded';
import ClearRoundedIcon from '@mui/icons-material/ClearRounded';
import CalendarMonthRoundedIcon from '@mui/icons-material/CalendarMonthRounded';
import EditRoundedIcon from '@mui/icons-material/EditRounded';
import DeleteOutlineRoundedIcon from '@mui/icons-material/DeleteOutlineRounded';
import OpenInNewRoundedIcon from '@mui/icons-material/OpenInNewRounded';
import FavoriteRoundedIcon from '@mui/icons-material/FavoriteRounded';
import StarRoundedIcon from '@mui/icons-material/StarRounded';
import PlaylistPlayRoundedIcon from '@mui/icons-material/PlaylistPlayRounded';
import UpcomingRoundedIcon from '@mui/icons-material/UpcomingRounded';
import Navbar from '../Navigation/Navbar';
import gameService from '../services/gameService';
import EditGameModal from '../Library/EditGameModal';
import ErrorMessage from '../Common/ErrorMessage';
import { toast } from 'react-toastify';

const statuses = [
  ['ALL', 'All statuses'],
  ['PLAYING', 'Playing'],
  ['BACKLOG', 'Backlog'],
  ['WISHLIST', 'Wishlist'],
  ['COMPLETED', 'Completed'],
  ['DROPPED', 'Dropped'],
  ['ONLINE', 'Online'],
];

const statusStyle = {
  PLAYING: { label: 'Playing', color: '#a78bfa' },
  BACKLOG: { label: 'Backlog', color: '#fbbf24' },
  WISHLIST: { label: 'Wishlist', color: '#fb7185' },
  COMPLETED: { label: 'Completed', color: '#34d399' },
  DROPPED: { label: 'Dropped', color: '#94a3b8' },
  ONLINE: { label: 'Online', color: '#38bdf8' },
};

const dateFormatter = new Intl.DateTimeFormat('en-GB', {
  day: '2-digit', month: 'short', year: 'numeric',
});

const formatDate = (value) => {
  if (!value) return 'TBA';
  const date = new Date(`${value.slice(0, 10)}T12:00:00`);
  return Number.isNaN(date.getTime()) ? 'TBA' : dateFormatter.format(date);
};

const getWishlistReleaseLabel = (game) => {
  if (!game.releaseDate) return 'Release date TBA';
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const releaseDate = new Date(`${game.releaseDate}T00:00:00`);
  const difference = Math.ceil((releaseDate - today) / 86400000);
  if (difference === 0) return 'Out today';
  if (difference > 0) return `In ${difference} day${difference === 1 ? '' : 's'}`;
  return 'Released';
};

const GameListProposal = () => {
  const navigate = useNavigate();
  const theme = useTheme();
  const compact = useMediaQuery(theme.breakpoints.down('sm'));
  const [mode, setMode] = useState('library');
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');
  const [status, setStatus] = useState('ALL');
  const [sortModel, setSortModel] = useState([{ field: 'updatedAt', sort: 'desc' }]);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(12);
  const [totalRows, setTotalRows] = useState(0);
  const [editingGame, setEditingGame] = useState(null);
  const [gameToDelete, setGameToDelete] = useState(null);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    const timeout = setTimeout(() => setDebouncedSearch(search), 350);
    return () => clearTimeout(timeout);
  }, [search]);

  useEffect(() => {
    setPage(0);
  }, [debouncedSearch, status, mode, sortModel]);

  const loadGames = useCallback(async () => {
    const activeStatus = mode === 'wishlist' ? 'WISHLIST' : status;
    const activeSort = sortModel[0] || { field: 'updatedAt', sort: 'desc' };
    const response = await gameService.getGames(page, pageSize, activeStatus, debouncedSearch, activeSort.field.replace(/([A-Z])/g, '_$1').toUpperCase(), activeSort.sort.toUpperCase());
    setGames(response.content || []);
    setTotalRows(response.totalElements || 0);
  }, [debouncedSearch, mode, page, pageSize, sortModel, status]);

  const loadData = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      await loadGames();
    } catch (requestError) {
      setError(requestError.message || 'Could not load games.');
    } finally {
      setLoading(false);
    }
  }, [loadGames]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const rows = useMemo(() => games, [games]);

  const handleDelete = async () => {
    if (!gameToDelete) return;
    try {
      setSaving(true);
      await gameService.deleteGame(gameToDelete.id);
      toast.info(`“${gameToDelete.title}” removed from your library.`);
      setGameToDelete(null);
      await loadData();
    } catch (deleteError) {
      toast.error('Could not remove this game.');
    } finally {
      setSaving(false);
    }
  };

  const handleSave = async (updatedGame) => {
    if (!editingGame) return;
    try {
      setSaving(true);
      await gameService.updateGame(editingGame.id, updatedGame);
      setEditingGame(null);
      toast.success(`“${editingGame.title}” updated.`);
      await loadData();
    } catch (saveError) {
      toast.error('Could not update this game.');
    } finally {
      setSaving(false);
    }
  };

  const clearFilters = () => {
    setSearch('');
    setDebouncedSearch('');
    setStatus('ALL');
    setSortModel([{ field: mode === 'wishlist' ? 'releaseDate' : 'updatedAt', sort: mode === 'wishlist' ? 'asc' : 'desc' }]);
  };

  const columns = [
    {
      field: 'cover', headerName: '', width: compact ? 52 : 76, sortable: false,
      renderCell: ({ row }) => (
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', width: '100%' }}><Avatar
          variant="rounded" src={row.imageUrl || '/gamer-placeholder.png'} alt=""
          imgProps={{ onError: (event) => { event.currentTarget.src = '/gamer-placeholder.png'; } }}
          sx={{ width: compact ? 36 : 52, height: compact ? 48 : 64, borderRadius: 1.5, bgcolor: '#243041' }}
        /></Box>
      ),
    },
    {
      field: 'title', headerName: 'Game', flex: 1, minWidth: compact ? 160 : 230, headerAlign: 'center',
      renderCell: ({ row }) => (
        <Stack spacing={0.25} alignItems="center" justifyContent="center" sx={{ minWidth: 0, width: '100%', height: '100%' }}>
          <Typography noWrap fontWeight={750} color="common.white">{row.title}</Typography>
          {!compact && <Typography noWrap variant="caption" sx={{ color: '#9aa9bd' }}>{row.platform || 'Platform not set'}</Typography>}
        </Stack>
      ),
    },
    !compact && {
      field: 'status', headerName: 'Status', width: 132, align: 'center', headerAlign: 'center', sortable: false,
      renderCell: ({ row }) => {
        const item = statusStyle[row.status || 'WISHLIST'];
        return <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', width: '100%' }}><Chip size="small" label={item.label} sx={{ color: item.color, border: `1px solid ${item.color}55`, bgcolor: `${item.color}14`, fontWeight: 700 }} /></Box>;
      },
    },
    {
      field: 'releaseDate', headerName: 'Release date', width: compact ? 105 : 148, align: 'center', headerAlign: 'center',
      renderCell: ({ row }) => (
        <Stack spacing={0.25} alignItems="center" justifyContent="center" sx={{ width: '100%', height: '100%' }}>
          <Typography variant="body2" color="common.white">{formatDate(row.releaseDate)}</Typography>
          {mode === 'wishlist' && <Typography variant="caption" color="primary.light">{getWishlistReleaseLabel(row)}</Typography>}
        </Stack>
      ),
    },
    !compact && {
      field: 'completedAt', headerName: 'Completed', width: 132, align: 'center', headerAlign: 'center',
      renderCell: ({ row }) => <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', width: '100%' }}><Typography variant="body2" sx={{ color: row.completedAt ? '#dbeafe' : '#9aa9bd' }}>{row.completedAt ? formatDate(row.completedAt) : '—'}</Typography></Box>,
    },
    !compact && {
      field: 'updatedAt', headerName: 'Last activity', width: 138, align: 'center', headerAlign: 'center',
      renderCell: ({ row }) => <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', width: '100%' }}><Typography variant="body2" sx={{ color: '#9aa9bd' }}>{formatDate(row.updatedAt || row.createdAt)}</Typography></Box>,
    },
    !compact && {
      field: 'rating', headerName: 'Your rating', width: 124, align: 'center', headerAlign: 'center',
      renderCell: ({ row }) => row.rating ? (
        <Stack direction="row" justifyContent="center" alignItems="center" spacing={0.5} color="#fbbf24" sx={{ width: '100%', height: '100%' }}>
          <StarRoundedIcon fontSize="small" />
          <Typography color="common.white" fontWeight={700}>{Number(row.rating).toFixed(1)}</Typography>
        </Stack>
      ) : <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', width: '100%' }}><Typography variant="body2" sx={{ color: '#9aa9bd' }}>—</Typography></Box>,
    },
    !compact && {
      field: 'favourite', headerName: '', width: 50, sortable: false, align: 'center', headerAlign: 'center',
      renderCell: ({ row }) => row.favourite ? <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', width: '100%' }}><Tooltip title="Favourite"><FavoriteRoundedIcon fontSize="small" sx={{ color: '#fb7185' }} /></Tooltip></Box> : null,
    },
    {
      field: 'actions', headerName: '', width: compact ? 86 : 126, sortable: false, filterable: false, align: 'center', headerAlign: 'center',
      renderCell: ({ row }) => (
        <Box onClick={(event) => event.stopPropagation()} sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', width: '100%' }}>
          <Tooltip title="View details"><IconButton size="small" onClick={() => navigate(`/games/details/${row.id}`)} sx={{ color: 'primary.light' }}><OpenInNewRoundedIcon fontSize="small" /></IconButton></Tooltip>
          <Tooltip title="Edit"><IconButton size="small" onClick={() => setEditingGame(row)} sx={{ color: '#a9b7ca' }}><EditRoundedIcon fontSize="small" /></IconButton></Tooltip>
          {!compact && <Tooltip title="Remove"><IconButton size="small" onClick={() => setGameToDelete(row)} sx={{ color: '#fb7185' }}><DeleteOutlineRoundedIcon fontSize="small" /></IconButton></Tooltip>}
        </Box>
      ),
    },
  ].filter(Boolean);

  if (error) return <><Navbar /><Box sx={{ maxWidth: 1200, mx: 'auto', p: 3 }}><ErrorMessage message={error} /></Box></>;

  const hasActiveFilters = search || (mode === 'library' && status !== 'ALL');

  const handleModeChange = (_, nextMode) => {
    if (!nextMode) return;
    setMode(nextMode);
    setStatus('ALL');
    setSortModel([{ field: nextMode === 'wishlist' ? 'releaseDate' : 'updatedAt', sort: nextMode === 'wishlist' ? 'asc' : 'desc' }]);
  };

  return (
    <>
      <Navbar />
      <Box sx={{ minHeight: 'calc(100vh - 70px)', bgcolor: '#111827', color: 'common.white', py: { xs: 2, md: 4 }, px: { xs: 1.5, md: 3 } }}>
        <Box sx={{ maxWidth: 1280, mx: 'auto' }}>
          <Stack direction={{ xs: 'column', md: 'row' }} justifyContent="space-between" alignItems={{ md: 'flex-start' }} spacing={2.5} mb={3}>
            <Box>
              <Stack direction="row" spacing={1} alignItems="center" mb={0.75}>
                <TuneRoundedIcon color="primary" />
                <Typography variant="overline" color="primary.light" fontWeight={800} letterSpacing={1.4}>UI EXPLORATION</Typography>
              </Stack>
              <Typography variant="h4" fontWeight={800} letterSpacing={-0.8}>Games, at a glance</Typography>
              <Typography sx={{ color: '#a9b7ca' }} mt={0.5}>A compact alternative to cards — designed for scanning, sorting and quick actions.</Typography>
            </Box>
            <ToggleButtonGroup value={mode} exclusive onChange={handleModeChange} size="small" sx={{ bgcolor: '#1f2937', borderRadius: 2, p: 0.5, '& .MuiToggleButton-root': { border: 0, color: '#a9b7ca', px: 1.5, '&.Mui-selected': { bgcolor: '#40516a', color: '#ffffff' } } }}>
              <ToggleButton value="library"><PlaylistPlayRoundedIcon fontSize="small" sx={{ mr: 0.75 }} />Library</ToggleButton>
              <ToggleButton value="wishlist"><UpcomingRoundedIcon fontSize="small" sx={{ mr: 0.75 }} />Wishlist</ToggleButton>
            </ToggleButtonGroup>
          </Stack>

          <Paper elevation={0} sx={{ bgcolor: '#172033', border: '1px solid #283548', borderRadius: 3, overflow: 'hidden' }}>
            <Stack direction={{ xs: 'column', md: 'row' }} spacing={1.5} alignItems={{ md: 'center' }} sx={{ p: 2, borderBottom: '1px solid #283548' }}>
              <TextField
                value={search}
                onChange={(event) => setSearch(event.target.value)}
                placeholder={mode === 'wishlist' ? 'Search your wishlist…' : 'Search your library…'}
                size="small"
                sx={{ width: { xs: '100%', md: 340 }, '& .MuiOutlinedInput-root': { bgcolor: '#111827', color: 'common.white' } }}
                InputProps={{ startAdornment: <InputAdornment position="start"><SearchRoundedIcon fontSize="small" /></InputAdornment> }}
              />
              {mode === 'library' && (
                <FormControl size="small" sx={{ minWidth: 165 }}>
                  <Select value={status} onChange={(event) => setStatus(event.target.value)} sx={{ bgcolor: '#111827', color: 'common.white' }}>
                    {statuses.map(([value, label]) => <MenuItem key={value} value={value}>{label}</MenuItem>)}
                  </Select>
                </FormControl>
              )}
              <Box flexGrow={1} />
              {hasActiveFilters && <Button onClick={clearFilters} startIcon={<ClearRoundedIcon />} color="inherit" sx={{ color: '#a9b7ca' }}>Clear filters</Button>}
              <Chip icon={<CalendarMonthRoundedIcon />} label={`${totalRows} game${totalRows === 1 ? '' : 's'}`} sx={{ bgcolor: '#243249', color: 'common.white', fontWeight: 700 }} />
            </Stack>

            <Box sx={{ height: { xs: 530, md: 650 }, width: '100%' }}>
              <DataGrid
                rows={rows}
                columns={columns}
                getRowHeight={() => compact ? 64 : 80}
                loading={loading}
                paginationMode="server"
                sortingMode="server"
                rowCount={totalRows}
                paginationModel={{ page, pageSize }}
                sortModel={sortModel}
                onSortModelChange={setSortModel}
                onPaginationModelChange={(model) => { setPage(model.page); setPageSize(model.pageSize); }}
                pageSizeOptions={[12, 24, 48]}
                onRowClick={({ row }) => navigate(`/games/details/${row.id}`)}
                disableColumnMenu
                disableRowSelectionOnClick
                sx={{
                  border: 0,
                  bgcolor: '#172033',
                  color: 'common.white',
                  '--DataGrid-containerBackground': '#1d293b',
                  '& .MuiDataGrid-main, & .MuiDataGrid-virtualScroller, & .MuiDataGrid-virtualScrollerContent, & .MuiDataGrid-virtualScrollerRenderZone': { backgroundColor: '#172033 !important' },
                  '& .MuiDataGrid-columnHeaders': { backgroundColor: '#1d293b !important', borderBottom: '1px solid #35445b', color: '#aebdd1', fontSize: 12, textTransform: 'uppercase', letterSpacing: 0.8 },
                  '& .MuiDataGrid-columnHeader, & .MuiDataGrid-filler, & .MuiDataGrid-scrollbarFiller': { backgroundColor: '#1d293b !important' },
                  '& .MuiDataGrid-columnHeader:focus, & .MuiDataGrid-cell:focus': { outline: 'none' },
                  '& .MuiDataGrid-cell': { borderBottom: '1px solid #2a3950', color: '#f8fafc' },
                  '& .MuiDataGrid-row': { cursor: 'pointer', bgcolor: '#172033', '&:hover': { bgcolor: '#223149' } },
                  '& .MuiDataGrid-footerContainer': { bgcolor: '#1d293b', borderTop: '1px solid #35445b', color: '#cbd5e1' },
                  '& .MuiTablePagination-root, & .MuiSvgIcon-root': { color: '#cbd5e1' },
                  '& .MuiDataGrid-overlay': { bgcolor: '#172033', color: '#94a3b8' },
                }}
                slotProps={{ noRowsOverlay: { children: <Stack alignItems="center" spacing={1}><Typography fontWeight={700}>Nothing to show yet</Typography><Typography variant="body2">Try changing the filters or add a game first.</Typography></Stack> } }}
              />
            </Box>
          </Paper>
        </Box>
      </Box>

      {editingGame && <EditGameModal game={editingGame} onSave={handleSave} onCancel={() => setEditingGame(null)} />}
      <Dialog open={Boolean(gameToDelete)} onClose={() => !saving && setGameToDelete(null)} PaperProps={{ sx: { bgcolor: '#1e293b', color: 'common.white', borderRadius: 3 } }}>
        <DialogTitle>Remove from library?</DialogTitle>
        <DialogContent><DialogContentText color="text.secondary">This will remove “{gameToDelete?.title}” from your tracked games.</DialogContentText></DialogContent>
        <DialogActions sx={{ p: 2 }}><Button onClick={() => setGameToDelete(null)} disabled={saving} color="inherit">Cancel</Button><Button onClick={handleDelete} disabled={saving} color="error" variant="contained">Remove</Button></DialogActions>
      </Dialog>
    </>
  );
};

export default GameListProposal;
