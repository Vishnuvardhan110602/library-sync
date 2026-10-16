function BookFilters({ search, onSearchChange, status, onStatusChange }) {
  return (
    <div className="filters">
      <input
        type="text"
        placeholder="Search by title or author..."
        value={search}
        onChange={(e) => onSearchChange(e.target.value)}
        aria-label="Search books"
      />
      <select value={status} onChange={(e) => onStatusChange(e.target.value)} aria-label="Filter by status">
        <option value="">All statuses</option>
        <option value="NEW">New</option>
        <option value="UPDATED">Updated</option>
        <option value="UNCHANGED">Unchanged</option>
      </select>
    </div>
  );
}

export default BookFilters;