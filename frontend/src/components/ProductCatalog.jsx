import React, { useEffect, useState } from 'react';
import { loanApi } from '../api/loanApi';

export default function ProductCatalog({ onSelectProduct }) {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loanApi.getProducts()
      .then(res => {
        setProducts(res.data);
        setLoading(false);
      })
      .catch(err => {
        setError('Failed to connect to backend: ' + (err.response?.data?.message || err.message));
        setLoading(false);
      });
  }, []);

  if (loading) return <div className="card"><p>Loading loan catalog...</p></div>;
  if (error) return <div className="alert-box alert-danger">{error}</div>;

  return (
    <div>
      <div className="card">
        <h2>Available Loan Products</h2>
        <p style={{ color: 'var(--text-muted)', marginBottom: '1.5rem' }}>
          Browse our retail financing products. Created polymorphically via the <strong>Factory Pattern</strong>.
        </p>
        <div className="grid-cards">
          {products.map(p => (
            <div key={p.id} className="product-card">
              <div>
                <h3>{p.productName}</h3>
                <span className="rate-badge">Interest Rate: {p.interestRate}% p.a.</span>
                <p style={{ fontSize: '0.9rem', color: 'var(--text-muted)', marginBottom: '1rem' }}>
                  {p.description}
                </p>
                <div style={{ fontSize: '0.85rem', marginBottom: '1rem' }}>
                  <div><strong>Min Amount:</strong> ₹{Number(p.minimumAmount).toLocaleString()}</div>
                  <div><strong>Max Amount:</strong> ₹{Number(p.maximumAmount).toLocaleString()}</div>
                  <div><strong>Loan Type:</strong> {p.loanType}</div>
                </div>
              </div>
              <button 
                className="btn btn-primary"
                onClick={() => onSelectProduct(p.loanType)}
              >
                Apply for this Loan
              </button>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
