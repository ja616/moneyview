import React, { useState, useEffect } from 'react';
import { loanApi } from '../api/loanApi';

export default function ApplicationTracker({ initialAppId }) {
  const [appId, setAppId] = useState(initialAppId || '');
  const [application, setApplication] = useState(null);
  const [activities, setActivities] = useState([]);
  const [notifications, setNotifications] = useState([]);
  const [evalResult, setEvalResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [evaluating, setEvaluating] = useState(false);
  const [errorMsg, setErrorMsg] = useState(null);

  const fetchDetails = async (idToFetch) => {
    if (!idToFetch) return;
    setLoading(true);
    setErrorMsg(null);
    try {
      const [appRes, actRes, notifRes] = await Promise.all([
        loanApi.getApplicationById(idToFetch),
        loanApi.getActivities(idToFetch),
        loanApi.getNotifications(idToFetch)
      ]);
      setApplication(appRes.data);
      setActivities(actRes.data);
      setNotifications(notifRes.data);
    } catch (err) {
      setErrorMsg(err.response?.data?.message || 'Application not found with ID: ' + idToFetch);
      setApplication(null);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (initialAppId) {
      setAppId(initialAppId);
      fetchDetails(initialAppId);
    }
  }, [initialAppId]);

  const handleSearch = (e) => {
    e.preventDefault();
    fetchDetails(appId);
  };

  const handleEvaluate = async () => {
    if (!application) return;
    setEvaluating(true);
    try {
      const res = await loanApi.evaluateApplication(application.id);
      setEvalResult(res.data);
      // Refresh details and observer activities
      await fetchDetails(application.id);
    } catch (err) {
      setErrorMsg('Evaluation error: ' + (err.response?.data?.message || err.message));
    } finally {
      setEvaluating(false);
    }
  };

  return (
    <div>
      <div className="card">
        <h2>Application Tracker</h2>
        <p style={{ color: 'var(--text-muted)', marginBottom: '1.25rem' }}>
          Inspect application status, trigger eligibility evaluation, and view live events broadcast by the <strong>Observer Pattern</strong>.
        </p>

        <form onSubmit={handleSearch} style={{ display: 'flex', gap: '0.5rem', marginBottom: '1.5rem' }}>
          <input 
            type="number"
            className="form-control"
            placeholder="Enter Application Reference ID (e.g. 1)"
            value={appId}
            onChange={(e) => setAppId(e.target.value)}
            style={{ maxWidth: '300px' }}
            required
          />
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Searching...' : 'Track Application'}
          </button>
        </form>

        {errorMsg && <div className="alert-box alert-danger">{errorMsg}</div>}

        {application && (
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
              <h3>Application #{application.id} – {application.customerName}</h3>
              <div style={{ display: 'flex', gap: '0.5rem' }}>
                <span className={`badge badge-${application.applicationStatus}`}>
                  {application.applicationStatus}
                </span>
                <span className={`badge badge-${application.eligibilityStatus}`}>
                  {application.eligibilityStatus}
                </span>
              </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem', background: '#f8fafc', padding: '1rem', borderRadius: '6px', marginBottom: '1.5rem' }}>
              <div><strong>Email:</strong> {application.customerEmail}</div>
              <div><strong>Loan Type:</strong> {application.loanType}</div>
              <div><strong>Amount:</strong> ₹{Number(application.requestedAmount).toLocaleString()}</div>
              <div><strong>Tenure:</strong> {application.tenureMonths} Months</div>
              <div><strong>Submitted:</strong> {new Date(application.createdAt).toLocaleString()}</div>
            </div>

            <div style={{ marginBottom: '1.5rem' }}>
              <button 
                className="btn btn-success" 
                onClick={handleEvaluate} 
                disabled={evaluating}
              >
                {evaluating ? 'Running Underwriting Engine...' : 'Run Eligibility Evaluation'}
              </button>
              <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginLeft: '1rem' }}>
                Runs through <strong>Decorator Pattern</strong> (telemetry/timing) and <strong>Strategy Pattern</strong>.
              </span>
            </div>

            {evalResult && (
              <div className={`alert-box ${evalResult.eligible ? 'alert-success' : 'alert-danger'}`} style={{ marginBottom: '1.5rem' }}>
                <strong>Evaluation Outcome: {evalResult.eligible ? 'ELIGIBLE' : 'INELIGIBLE'}</strong>
                <div>Calculated Monthly EMI: ₹{Number(evalResult.calculatedEmi).toLocaleString()}</div>
                <div>Decision Rationale: {evalResult.decisionReason}</div>
              </div>
            )}

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem' }}>
              {/* Observer 1: Activity Timeline */}
              <div style={{ border: '1px solid var(--border)', borderRadius: '6px', padding: '1rem' }}>
                <h4 style={{ color: 'var(--primary)', marginBottom: '0.5rem' }}>
                  Activity Audit Timeline (LoanActivityObserver)
                </h4>
                {activities.length === 0 ? (
                  <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>No activities logged yet.</p>
                ) : (
                  <ul className="timeline">
                    {activities.map((a, idx) => (
                      <li key={idx} className="timeline-item">
                        <div style={{ fontSize: '0.9rem', fontWeight: 500 }}>{a.message}</div>
                        <div className="timeline-time">{new Date(a.timestamp).toLocaleTimeString()}</div>
                      </li>
                    ))}
                  </ul>
                )}
              </div>

              {/* Observer 2: Customer Notifications */}
              <div style={{ border: '1px solid var(--border)', borderRadius: '6px', padding: '1rem' }}>
                <h4 style={{ color: 'var(--primary)', marginBottom: '0.5rem' }}>
                  Customer In-App Alerts (LoanNotificationObserver)
                </h4>
                {notifications.length === 0 ? (
                  <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>No notifications generated yet.</p>
                ) : (
                  <div>
                    {notifications.map((n, idx) => (
                      <div key={idx} style={{ background: '#f1f5f9', padding: '0.75rem', borderRadius: '4px', marginBottom: '0.75rem' }}>
                        <div style={{ fontSize: '0.85rem', fontWeight: 600 }}>{n.title}</div>
                        <div style={{ fontSize: '0.85rem', color: 'var(--text-main)', marginTop: '0.2rem' }}>{n.body}</div>
                        <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.3rem' }}>
                          {new Date(n.timestamp).toLocaleTimeString()}
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
