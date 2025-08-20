import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Dashboard from './components/_games/Dashboard/Dashboard';
import Library from './components/_games/Library/Library';
import Wishlist from './components/_games/Wishlist/Wishlist';
import GameSearch from './components/_games/Search/GameSearch';
import './App.css';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import Homepage from './components/_home/Homepage';
import MoviesDashboard from './components/_placeholders/MoviesDashboard';
import TVSeriesDashboard from './components/_tv-series/Dashboard/TVSeriesDashboard';
import SearchSeries from './components/_tv-series/Search/SearchSeries';
import SeriesDetails from './components/_tv-series/Details/SeriesDetails';
import SyncLibrary from './components/_games/Sync/SyncLibrary';

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

            {/* Future TV Series */}
            <Route path="/tv-series/dashboard" element={<TVSeriesDashboard />} />
            <Route path="/tv-series/search" element={<SearchSeries />} />
            <Route path="/series/:id" element={<SeriesDetails />} />

            {/* Future Movies */}
            <Route path="/movies" element={<MoviesDashboard />} />

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
