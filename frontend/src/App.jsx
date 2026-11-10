import React, { useState } from 'react';
import ProductCatalog from './components/ProductCatalog';
import ApplicationForm from './components/ApplicationForm';
import ApplicationTracker from './components/ApplicationTracker';
import AdminDashboard from './components/AdminDashboard';

export default function App() {
  const [activeTab, setActiveTab] = useState('products');
  const [selectedProductType, setSelectedProductType] = useState('PERSONAL');
  const [trackedAppId, setTrackedAppId] = useState('');

  const handleSelectProduct = (loanType) => {
    setSelectedProductType(loanType);
    setActiveTab('apply');
  };

  const handleApplicationSuccess = (newId) => {
    setTrackedAppId(newId);
    setActiveTab('track');
  };

  const handleInspectApplication = (id) => {
    setTrackedAppId(id);
    setActiveTab('track');
  };

  return (
    <div>
      <header className="header">
        <div className="header-content">
          <div className="logo">
            FinFlow <span>v1.0 Monolith</span>
          </div>
          <nav className="nav-tabs">
            <button 
              className={`nav-tab ${activeTab === 'products' ? 'active' : ''}`}
              onClick={() => setActiveTab('products')}
            >
              Loan Products
            </button>
            <button 
              className={`nav-tab ${activeTab === 'apply' ? 'active' : ''}`}
              onClick={() => setActiveTab('apply')}
            >
              Apply Now
            </button>
            <button 
              className={`nav-tab ${activeTab === 'track' ? 'active' : ''}`}
              onClick={() => setActiveTab('track')}
            >
              Track Status
            </button>
            <button 
              className={`nav-tab ${activeTab === 'admin' ? 'active' : ''}`}
              onClick={() => setActiveTab('admin')}
            >
              Admin Portal
            </button>
          </nav>
        </div>
      </header>

      <main className="container">
        {activeTab === 'products' && (
          <ProductCatalog onSelectProduct={handleSelectProduct} />
        )}

        {activeTab === 'apply' && (
          <ApplicationForm 
            initialLoanType={selectedProductType}
            onSuccess={handleApplicationSuccess}
          />
        )}

        {activeTab === 'track' && (
          <ApplicationTracker initialAppId={trackedAppId} />
        )}

        {activeTab === 'admin' && (
          <AdminDashboard onInspectApplication={handleInspectApplication} />
        )}
      </main>
    </div>
  );
}
