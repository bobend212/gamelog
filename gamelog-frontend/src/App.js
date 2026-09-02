import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Dashboard from './components/_games/Dashboard/Dashboard';
import Library from './components/_games/Library/Library';
import GameSearch from './components/_games/Search/GameSearch';
import './App.css';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import Homepage from './components/_home/Homepage';
import MoviesDashboard from './components/_movies/Dashboard/MoviesDashboard';
import SyncLibrary from './components/_games/Sync/SyncLibrary';
import MovieDetails from './components/_movies/Details/MovieDetails';
import MovieSearch from './components/_movies/Search/MovieSearch';
import SyncMovies from './components/_movies/Sync/SyncMovies';
import TVShowsDashboard from './components/_tv-shows/Dashboard/TVShowsDashboard';
import TVShowSearch from './components/_tv-shows/Search/TVShowSearch';
import TVShowDetails from './components/_tv-shows/Details/TVShowDetails';
import TVShowSync from './components/_tv-shows/Sync/TVShowSync';
import GameDetails from './components/_games/Details/GameDetails';
import GameListProposal from './components/_games/Temp/GameListProposal';

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
            <Route path="/games/sync" element={<SyncLibrary />} />
            <Route path="/games/search" element={<GameSearch />} />
            <Route path="/games/details/:gameId" element={<GameDetails />} />
            <Route path="/games/temp" element={<GameListProposal />} />

            {/* TV Shows */}
            <Route path="/tv-shows/dashboard" element={<TVShowsDashboard />} />
            <Route path="/tv-shows/search" element={<TVShowSearch />} />
            <Route path="/tv-shows/sync" element={<TVShowSync />} />
            <Route path="/tv-shows/:id" element={<TVShowDetails />} />

            {/* Movies */}
            <Route path="/movies/dashboard" element={<MoviesDashboard />} />
            <Route path="/movies/search" element={<MovieSearch />} />
            <Route path="/movies/:id" element={<MovieDetails />} />
            <Route path="/movies/sync" element={<SyncMovies />} />

          </Routes>
        </main>

        <ToastContainer
          position="bottom-right"
          theme="dark"
          hideProgressBar={false}
          newestOnTop={true}
          closeOnClick
          pauseOnHover
        />
      </div>
    </Router>
  );
}

export default App;
