import React, { useState } from 'react';
import './EditGameModal.css';
import { PLAYED_ON_OPTIONS } from '../../utils/constants';

const EditGameModal = ({ game, onSave, onCancel }) => {
  const [formData, setFormData] = useState({
    playedOn: game.playedOn || '',
    status: game.status || 'BACKLOG',
    notes: game.notes || '',
    rating: game.rating || '',
    completedAt: game.completedAt || null
  });

  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    // Clear error when user starts typing
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};

    if (formData.rating && (isNaN(formData.rating) || formData.rating < 0 || formData.rating > 5)) {
      newErrors.rating = 'Rating must be between 0 and 5';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (validateForm()) {
      onSave({
        ...formData,
        rating: formData.rating ? parseFloat(formData.rating) : null
      });
    }
  };

  const handleOverlayClick = (e) => {
    if (e.target === e.currentTarget) {
      onCancel();
    }
  };

  return (
    <div className="modal-overlay" onClick={handleOverlayClick}>
      <div className="modal-content">
        <div className="modal-header">
          <h2>{game.title}</h2>
          <button onClick={onCancel} className="modal-close">×</button>
        </div>

        <form onSubmit={handleSubmit} className="edit-form">
          {/* Platform and Status Row */}
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="playedOn">Played On</label>
              <select
                id="playedOn"
                name="playedOn"
                value={formData.playedOn}
                onChange={handleChange}
                className="form-select"
              >
                <option value="">Select</option>
                {PLAYED_ON_OPTIONS.map(playedOn => (
                  <option key={playedOn} value={playedOn}>{playedOn}</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="status">Status</label>
              <select
                id="status"
                name="status"
                value={formData.status}
                onChange={handleChange}
                className="form-select"
              >
                <option value="WISHLIST">Wishlist</option>
                <option value="BACKLOG">Backlog</option>
                <option value="PLAYING">Playing</option>
                <option value="COMPLETED">Completed</option>
                <option value="DROPPED">Dropped</option>
              </select>
            </div>
          </div>

          {/* Category and Rating Row */}
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="rating">Rating (0-5)</label>
              <input
                type="number"
                id="rating"
                name="rating"
                value={formData.rating}
                onChange={handleChange}
                min="0"
                max="5"
                step="0.5"
                className={errors.rating ? 'error' : ''}
                placeholder="4.5"
              />
              {errors.rating && <span className="error-text">{errors.rating}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="completedAt">
                Completion Date
              </label>
              <div className="date-input-wrapper">
                <input
                  type="date"
                  id="completedAt"
                  name="completedAt"
                  value={formData.completedAt}
                  onChange={handleChange}
                  className={errors.completedAt ? 'error' : ''}
                  max={new Date().toISOString().split('T')[0]} // Prevent future dates
                />
                {formData.completedAt && (
                  <button
                    type="button"
                    onClick={() => setFormData(prev => ({ ...prev, completedAt: '' }))}
                    className="clear-date-btn"
                    title="Clear completion date"
                  >
                    ✕
                  </button>
                )}
              </div>
              {errors.completedAt && <span className="error-text">{errors.completedAt}</span>}
            </div>

          </div>

          {/* Notes */}
          <div className="form-group">
            <label htmlFor="notes">Notes</label>
            <textarea
              id="notes"
              name="notes"
              value={formData.notes}
              onChange={handleChange}
              rows="2"
              placeholder="Add your personal notes about this game..."
              className="form-textarea"
            />
          </div>

          <div className="modal-actions">
            <button type="button" onClick={onCancel} className="btn btn-secondary">
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              Save
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default EditGameModal;
