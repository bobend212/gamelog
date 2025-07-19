import React, { useState } from 'react';
import gameService from '../../services/gameService';
import SearchResultCard from './SearchResultCard';
import LoadingSpinner from '../Common/LoadingSpinner';
import ErrorMessage from '../Common/ErrorMessage';
import './GameSearch.css';
import { POPULAR_SEARCHES } from '../../utils/constants';

const GameSearch = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [hasSearched, setHasSearched] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);

  const handleSearch = async (query = searchQuery, page = 1) => {
    if (!query.trim()) {
      setError('Please enter a search term');
      return;
    }

    try {
      setLoading(true);
      setError(null);

      const results = await gameService.searchGames(query.trim(), page);

      if (page === 1) {
        setSearchResults(results);
      } else {
        setSearchResults(prev => [...prev, ...results]);
      }

      setCurrentPage(page);
      setHasSearched(true);
    } catch (err) {
      setError(err.message);
      if (page === 1) {
        setSearchResults([]);
      }
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setCurrentPage(1);
    handleSearch(searchQuery, 1);
  };

  const handleQuickSearch = (query) => {
    setSearchQuery(query);
    setCurrentPage(1);
    handleSearch(query, 1);
  };

  return (
    <div className="game-search">
      <div className="container">
        <div className="search-header">
          <h1>Search Games</h1>
        </div>

        {/* Search Form */}
        <div className="search-form-container">
          <form onSubmit={handleSubmit} className="search-form">
            <div className="search-input-group">
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search for games..."
                className="search-input"
                disabled={loading}
              />
              <button
                type="submit"
                className="btn btn-primary search-btn"
                disabled={loading || !searchQuery.trim()}
              >
                {loading ? 'Searching...' : 'Search'}
              </button>
            </div>
          </form>

          {/* Quick Search Suggestions */}
          {!hasSearched && (
            <div className="quick-search">
              <div className="quick-search-tags">
                {POPULAR_SEARCHES.map((term, index) => (
                  <button
                    key={index}
                    onClick={() => handleQuickSearch(term)}
                    className="quick-search-tag"
                    disabled={loading}
                  >
                    {term}
                  </button>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* Error Display */}
        {error && <ErrorMessage message={error} />}

        {/* Search Results */}
        {hasSearched && (
          <div className="search-results">
            <div className="results-header">
              <h2>Search Results</h2>
              {searchResults.length > 0 && (
                <p>{searchResults.length} games found for "{searchQuery}" | metadata by RAWG API</p>
              )}
            </div>

            {searchResults.length > 0 ? (
              <>
                <div className="results-grid">
                  {searchResults.map((game) => (
                    <SearchResultCard
                      key={game.id}
                      game={game}
                      onGameAdded={() => {
                        setSearchQuery('');
                        setSearchResults([]);
                      }}
                    />
                  ))}
                </div>
              </>
            ) : (
              !loading && (
                <div className="no-results">
                  <h3>No games found</h3>
                  <p>Try searching with different keywords or check your spelling.</p>
                </div>
              )
            )}
          </div>
        )}
        <p className="footer" >provided by RAWG API</p>
        {/* Loading Spinner */}
        {loading && currentPage === 1 && <LoadingSpinner />}
      </div>
    </div>
  );
};

export default GameSearch;
