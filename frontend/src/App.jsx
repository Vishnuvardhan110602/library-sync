import { useEffect, useState, useCallback } from "react";
import SummaryCards from "./components/SummaryCards.jsx";
import BookFilters from "./components/BookFilters.jsx";
import BooksTable from "./components/BooksTable.jsx";
import SyncHistory from "./components/SyncHistory.jsx";
import { getStats, getBooks, getSyncRuns, startSync } from "./services/api.js";

function App() {
  const [stats, setStats] = useState(null);
  const [books, setBooks] = useState([]);
  const [runs, setRuns] = useState([]);
  const [search, setSearch] = useState("");
  const [status, setStatus] = useState("");

  const [loadingBooks, setLoadingBooks] = useState(true);
  const [loadingRuns, setLoadingRuns] = useState(true);
  const [syncing, setSyncing] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [tick, setTick] = useState(0); 


  const loadSummary = useCallback(async () => {
    try {
      const [statsData, runsData] = await Promise.all([getStats(), getSyncRuns()]);
      setStats(statsData);
      setRuns(runsData);
      setError("");
    } catch (e) {
      setError(e.message);
    } finally {
      setLoadingRuns(false);
    }
  }, []);

  useEffect(() => {
    const id = setInterval(() => setTick((t) => t + 1), 30000);
    return () => clearInterval(id);
  }, []);

  useEffect(() => {
    loadSummary();
  }, [tick, loadSummary]);

  
  useEffect(() => {
    const timer = setTimeout(async () => {
      setLoadingBooks(true);
      try {
        setBooks(await getBooks(search, status));
      } catch (e) {
        setError(e.message);
      } finally {
        setLoadingBooks(false);
      }
    }, 300);
    return () => clearTimeout(timer);
  }, [search, status, tick]);

  async function handleSyncNow() {
    setSyncing(true);
    setError("");
    setSuccess("");
    try {
      const run = await startSync();
      if (run.status === "SUCCESS") {
        setSuccess(
          `Sync finished: ${run.newCount} new, ${run.updatedCount} updated, ${run.unchangedCount} unchanged.`
        );
      } else {
        setError(`Sync failed: ${run.errorMessage || "unknown error"}`);
      }
    } catch (e) {
      setError(e.message);
    } finally {
      setSyncing(false);
      setTick((t) => t + 1);
    }
  }

  return (
    <div className="container">
      <header className="header">
        <h1>Library Data Sync Dashboard</h1>
        <button className="sync-button" onClick={handleSyncNow} disabled={syncing}>
          {syncing && <span className="spinner" aria-hidden="true"></span>}
          {syncing ? "Syncing..." : "Sync Now"}
        </button>
      </header>

      {error && <div className="alert alert-error" role="alert">{error}</div>}
      {success && <div className="alert alert-success" role="status">{success}</div>}

      <SummaryCards stats={stats} />

      <section className="panel">
        <h2>Books</h2>
        <BookFilters
          search={search}
          onSearchChange={setSearch}
          status={status}
          onStatusChange={setStatus}
        />
        <BooksTable books={books} loading={loadingBooks} />
      </section>

      <section className="panel">
        <h2>Sync History</h2>
        <SyncHistory runs={runs} loading={loadingRuns} />
      </section>
    </div>
  );
}

export default App;