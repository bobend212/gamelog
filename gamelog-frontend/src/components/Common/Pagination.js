import React from 'react';
import './Pagination.css';

const Pagination = ({ currentPage, totalPages, onPageChange }) => {
    const getPageNumbers = () => {
        const pages = [];
        const start = Math.max(0, currentPage - 1);
        const end = Math.min(totalPages - 1, currentPage + 1);

        for (let i = start; i <= end; i++) {
            pages.push(i);
        }
        return pages;
    };

    return (
        <div className="pagination">
            <button
                onClick={() => onPageChange(currentPage - 1)}
                disabled={currentPage === 0}
                className="pagination-btn"
            >
                ←
            </button>

            {getPageNumbers().map(page => (
                <button
                    key={page}
                    onClick={() => onPageChange(page)}
                    className={`pagination-btn ${page === currentPage ? 'active' : ''}`}
                >
                    {page + 1}
                </button>
            ))}

            <button
                onClick={() => onPageChange(currentPage + 1)}
                disabled={currentPage === totalPages - 1}
                className="pagination-btn"
            >
                →
            </button>
        </div>
    );
};

export default Pagination;
