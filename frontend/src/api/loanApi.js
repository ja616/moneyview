import axios from 'axios';

// API client pointing to Spring Boot backend
const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

export const loanApi = {
  // Products
  getProducts: () => api.get('/loans/products'),

  // Applications
  submitApplication: (data) => api.post('/loans/applications', data),
  getAllApplications: () => api.get('/loans/applications'),
  getApplicationById: (id) => api.get(`/loans/applications/${id}`),
  evaluateApplication: (id) => api.post(`/loans/applications/${id}/evaluate`),

  // Observers: Audit activities and notifications
  getActivities: (id) => api.get(`/loans/applications/${id}/activities`),
  getNotifications: (id) => api.get(`/loans/applications/${id}/notifications`),

  // Admin
  approveApplication: (id) => api.patch(`/admin/applications/${id}/approve`),
  rejectApplication: (id, reason) => api.patch(`/admin/applications/${id}/reject`, { reason }),

  // Diagnostic
  getSingletonDiagnostic: () => api.get('/loans/diagnostic/singleton')
};
