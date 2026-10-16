import { formatTime } from "../services/format.js";

function SyncHistory({ runs, loading }) {
  if (loading && runs.length === 0) {
    return <p className="message">Loading sync history...</p>;
  }
  if (runs.length === 0) {
    return <p className="message">No sync runs yet.</p>;
  }

  return (
    <div className="table-wrapper">
      <table>
        <thead>
          <tr>
            <th>Run Time</th>
            <th>Trigger</th>
            <th>Result</th>
            <th>Fetched</th>
            <th>New</th>
            <th>Updated</th>
            <th>Unchanged</th>
          </tr>
        </thead>
        <tbody>
          {runs.map((run) => (
            <tr key={run.id}>
              <td>{formatTime(run.startedAt)}</td>
              <td>{run.triggeredBy}</td>
              <td>
                <span className={`badge badge-run-${run.status.toLowerCase()}`} title={run.errorMessage || ""}>
                  {run.status}
                </span>
              </td>
              <td>{run.fetchedCount}</td>
              <td>{run.newCount}</td>
              <td>{run.updatedCount}</td>
              <td>{run.unchangedCount}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default SyncHistory;