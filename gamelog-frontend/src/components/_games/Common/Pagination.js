import React from 'react';
import './Pagination.css';

const Pagination = ({ currentPage, totalPages, onPageChange }) => {
    const visiblePages = 3;

    const getPageNumbers = () => {
        if (totalPages <= visiblePages) {
            return Array.from({ length: totalPages }, (_, i) => i);
        }

        let start = currentPage - Math.floor(visiblePages / 2);
        let end = start + visiblePages - 1;

        if (start < 0) {
            start = 0;
            end = visiblePages - 1;
        }

        if (end >= totalPages) {
            end = totalPages - 1;
            start = totalPages - visiblePages;
        }

        const pages = [];
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