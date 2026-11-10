import React, { useState, useEffect } from 'react';
import { loanApi } from '../api/loanApi';

export default function ApplicationForm({ initialLoanType, onSuccess }) {
  const [formData, setFormData] = useState({
    customerName: '',
    customerEmail: '',
    monthlyIncome: '',
    loanType: initialLoanType || 'PERSONAL',
    requestedAmount: '',
    tenureMonths: '24'
  });

  const [submitting, setSubmitting] = useState(false);
  const [successResult, setSuccessResult] = useState(null);
  const [errorMsg, setErrorMsg] = useState(null);

  useEffect(() => {
    if (initialLoanType) {
      setFormData(prev => ({ ...prev, loanType: initialLoanType }));
    }
  }, [initialLoanType]);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setErrorMsg(null);
    setSuccessResult(null);

    try {
      const payload = {
        customerName: formData.customerName,
        customerEmail: formData.customerEmail,
        monthlyIncome: parseFloat(formData.monthlyIncome),
        loanType: formData.loanType,
        requestedAmount: parseFloat(formData.requestedAmount),
        tenureMonths: parseInt(formData.tenureMonths, 10)
      };

      const res = await loanApi.submitApplication(payload);
      setSuccessResult(res.data);
      if (onSuccess) onSuccess(res.data.id);
    } catch (err) {
      const resp = err.response?.data;
      if (resp?.errors) {
        const details = Object.entries(resp.errors).map(([k, v]) => `${k}: ${v}`).join(', ');
        setErrorMsg('Validation Error - ' + details);
      } else {
        setErrorMsg(resp?.message || err.message);
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="card">
      <h2>Submit Loan Application</h2>
      <p style={{ color: 'var(--text-muted)', marginBottom: '1.5rem' }}>
        Enter your details below. Validated on backend using JSR-380 annotations and processed through our design pattern workflow.
      </p>

      {errorMsg && <div className="alert-box alert-danger">{errorMsg}</div>}
      {successResult && (
        <div className="alert-box alert-success">
          <strong>Application Submitted Successfully!</strong> Application Reference ID: #{successResult.id} (Status: {successResult.applicationStatus})
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
          <div className="form-group">
            <label>Full Legal Name</label>
            <input 
              type="text" 
              name="customerName"
              className="form-control"
              placeholder="e.g. John Miller" 
              value={formData.customerName}
              onChange={handleChange}
              required 
            />
          </div>

          <div className="form-group">
            <label>Email Address</label>
            <input 
              type="email" 
              name="customerEmail"
              className="form-control"
              placeholder="e.g. john.miller@example.com" 
              value={formData.customerEmail}
              onChange={handleChange}
              required 
            />
          </div>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '1rem' }}>
          <div className="form-group">
            <label>Monthly Income (₹)</label>
            <input 
              type="number" 
              name="monthlyIncome"
              className="form-control"
              placeholder="e.g. 65000" 
              min="1000"
              step="500"
              value={formData.monthlyIncome}
              onChange={handleChange}
              required 
            />
          </div>

          <div className="form-group">
            <label>Loan Product Type</label>
            <select 
              name="loanType" 
              className="form-control"
              value={formData.loanType}
              onChange={handleChange}
            >
              <option value="PERSONAL">PERSONAL LOAN</option>
              <option value="HOME">HOME LOAN</option>
              <option value="EDUCATION">EDUCATION LOAN</option>
            </select>
          </div>

          <div className="form-group">
            <label>Requested Amount (₹)</label>
            <input 
              type="number" 
              name="requestedAmount"
              className="form-control"
              placeholder="e.g. 500000" 
              min="10000"
              step="5000"
              value={formData.requestedAmount}
              onChange={handleChange}
              required 
            />
          </div>
        </div>

        <div className="form-group">
          <label>Repayment Tenure (Months)</label>
          <select 
            name="tenureMonths" 
            className="form-control"
            value={formData.tenureMonths}
            onChange={handleChange}
          >
            <option value="12">12 Months (1 Year)</option>
            <option value="24">24 Months (2 Years)</option>
            <option value="36">36 Months (3 Years)</option>
            <option value="60">60 Months (5 Years)</option>
            <option value="120">120 Months (10 Years)</option>
            <option value="180">180 Months (15 Years)</option>
            <option value="240">240 Months (20 Years)</option>
          </select>
        </div>

        <button type="submit" className="btn btn-primary" disabled={submitting}>
          {submitting ? 'Submitting Application...' : 'Submit Application'}
        </button>
      </form>
    </div>
  );
}
