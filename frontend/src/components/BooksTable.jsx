import { formatTime } from "../services/format.js";

function BooksTable({ books, loading }) {
  if (loading && books.length === 0) {
    return <p className="message">Loading books...</p>;
  }
  if (books.length === 0) {
    return <p className="message">No books found. Click "Sync Now" to load books, or change your filters.</p>;
  }

  return (
    <div className="table-wrapper">
      <table>
        <thead>
          <tr>
            <th>Title</th>
            <th>Author</th>
            <th>Year</th>
            <th>Status</th>
            <th>Last Checked</th>
          </tr>
        </thead>
        <tbody>
          {books.map((book) => (
            <tr key={book.id}>
              <td>{book.title}</td>
              <td>{book.authors || "Unknown"}</td>
              <td>{book.firstPublishYear ?? "-"}</td>
              <td>
                <span className={`badge badge-${book.syncStatus.toLowerCase()}`}>{book.syncStatus}</span>
              </td>
              <td>{formatTime(book.lastCheckedAt)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default BooksTable;