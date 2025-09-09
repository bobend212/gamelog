import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Dashboard from './components/_games/Dashboard/Dashboard';
import Library from './components/_games/Library/Library';
import Wishlist from './components/_games/Wishlist/Wishlist';
import GameSearch from './components/_games/Search/GameSearch';
import './App.css';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import Homepage from './components/_home/Homepage';
import MoviesDashboard from './components/_movies/Dashboard/MoviesDashboard';
import TVSeriesDashboard from './components/_tv-series/Dashboard/TVSeriesDashboard';
import SearchSeries from './components/_tv-series/Search/SearchSeries';
import SeriesDetails from './components/_tv-series/Details/SeriesDetails';
import SyncLibrary from './components/_games/Sync/SyncLibrary';
import SyncSeries from './components/_tv-series/Sync/SyncSeries';
import MovieDetails from './components/_movies/Details/MovieDetails';
import MovieSearch from './components/_movies/Search/MovieSearch';

function App() {
  return (
    <Router>
      <div className="App">

        <main className="main-content">
          <Routes>
            <Route path="/" element={<Homepage />} />

            {/* Games */}
            <Route path="/games/dashboard" element={<Dashboard />} />
            <Route path="/games/library" element={<Library />} />
            <Route path="/games/wishlist" element={<Wishlist />} />
            <Route path="/games/sync" element={<SyncLibrary />} />
            <Route path="/games/search" element={<GameSearch />} />

            {/* TV Series */}
            <Route path="/tv-series/dashboard" element={<TVSeriesDashboard />} />
            <Route path="/tv-series/search" element={<SearchSeries />} />
            <Route path="/tv-series/sync" element={<SyncSeries />} />
            <Route path="/tv-series/:id" element={<SeriesDetails />} />

            {/* Movies */}
            <Route path="/movies/dashboard" element={<MoviesDashboard />} />
            <Route path="/movies/search" element={<MovieSearch />} />
            <Route path="/movies/:id" element={<MovieDetails />} />

          </Routes>
        </main>

        <ToastContainer
          position="bottom-right"
          autoClose={3000}
          theme="dark"
          hideProgressBar={true}
          newestOnTop={true}
          closeOnClick
          pauseOnHover
          draggable
        />
      </div>
    </Router>
  );
}

export default App;
