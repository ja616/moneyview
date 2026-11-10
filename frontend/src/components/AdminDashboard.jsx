import React, { useState, useEffect } from 'react';
import { loanApi } from '../api/loanApi';

export default function AdminDashboard({ onInspectApplication }) {
  const [applications, setApplications] = useState([]);
  const [diagnostic, setDiagnostic] = useState(null);
  const [loading, setLoading] = useState(true);
  const [actionMsg, setActionMsg] = useState(null);

  const loadData = async () => {
    setLoading(true);
    try {
      const [appsRes, diagRes] = await Promise.all([
        loanApi.getAllApplications(),
        loanApi.getSingletonDiagnostic()
      ]);
      setApplications(appsRes.data);
      setDiagnostic(diagRes.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleApprove = async (id) => {
    try {
      await loanApi.approveApplication(id);
      setActionMsg(`Application #${id} has been APPROVED.`);
      loadData();
    } catch (err) {
      alert('Error approving application: ' + (err.response?.data?.message || err.message));
    }
  };

  const handleReject = async (id) => {
    const reason = prompt('Enter rejection reason:', 'Underwriting criteria not satisfied.');
    if (reason === null) return;
    try {
      await loanApi.rejectApplication(id, reason);
      setActionMsg(`Application #${id} has been REJECTED.`);
      loadData();
    } catch (err) {
      alert('Error rejecting application: ' + (err.response?.data?.message || err.message));
    }
  };

  return (
    <div>
      {/* Singleton Inspector Card */}
      {diagnostic && (
        <div className="card" style={{ background: '#f8fafc', borderColor: '#cbd5e1' }}>
          <h3 style={{ fontSize: '1rem', color: 'var(--primary)', marginBottom: '0.4rem' }}>
            Spring IoC Architecture Inspector: Singleton Pattern Demonstration
          </h3>
          <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
            <div><strong>Bean Class:</strong> {diagnostic.beanClass}</div>
            <div><strong>Scope:</strong> {diagnostic.scope}</div>
            <div><strong>Identity Hash Code:</strong> <code>{diagnostic.identityHashCode}</code> (Shared across all requests)</div>
            <div style={{ marginTop: '0.3rem' }}><em>{diagnostic.explanation}</em></div>
          </div>
        </div>
      )}

      <div className="card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
          <h2>Admin Application Review Portal</h2>
          <button className="btn btn-primary btn-sm" onClick={loadData}>Refresh Queue</button>
        </div>

        {actionMsg && <div className="alert-box alert-info">{actionMsg}</div>}

        {loading ? (
          <p>Loading application queue...</p>
        ) : applications.length === 0 ? (
          <p style={{ color: 'var(--text-muted)' }}>No applications submitted yet. Submit one from the 'Apply Now' tab!</p>
        ) : (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Customer</th>
                  <th>Loan Type</th>
                  <th>Requested Amount</th>
                  <th>Tenure</th>
                  <th>Eligibility</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {applications.map(app => (
                  <tr key={app.id}>
                    <td><strong>#{app.id}</strong></td>
                    <td>
                      <div>{app.customerName}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{app.customerEmail}</div>
                    </td>
                    <td>{app.loanType}</td>
                    <td>₹{Number(app.requestedAmount).toLocaleString()}</td>
                    <td>{app.tenureMonths}m</td>
                    <td>
                      <span className={`badge badge-${app.eligibilityStatus}`}>
                        {app.eligibilityStatus}
                      </span>
                    </td>
                    <td>
                      <span className={`badge badge-${app.applicationStatus}`}>
                        {app.applicationStatus}
                      </span>
                    </td>
                    <td>
                      <div style={{ display: 'flex', gap: '0.35rem' }}>
                        <button 
                          className="btn btn-sm btn-primary"
                          onClick={() => onInspectApplication(app.id)}
                        >
                          Inspect
                        </button>
                        {app.applicationStatus === 'SUBMITTED' && (
                          <>
                            <button 
                              className="btn btn-sm btn-success"
                              onClick={() => handleApprove(app.id)}
                            >
                              Approve
                            </button>
                            <button 
                              className="btn btn-sm btn-danger"
                              onClick={() => handleReject(app.id)}
                            >
                              Reject
                            </button>
                          </>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
