import React, { useState, useEffect } from 'react';
import { checkHealth } from '../services/api';

const HealthCheck = () => {
  const [healthStatus, setHealthStatus] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchHealthStatus = async () => {
      try {
        const data = await checkHealth();
        setHealthStatus(data);
        setError(null);
      } catch (err) {
        console.error('Failed to fetch health status:', err);
        setError('Failed to connect to the backend service');
      } finally {
        setIsLoading(false);
      }
    };

    fetchHealthStatus();
  }, []);

  if (isLoading) {
    return <div className="health-check loading">Checking backend status...</div>;
  }

  if (error) {
    return (
      <div className="health-check error">
        <h3>Backend Status: <span className="status-error">Error</span></h3>
        <p>{error}</p>
      </div>
    );
  }

  return (
    <div className="health-check">
      <h3>Backend Status: <span className="status-success">Healthy</span></h3>
      <p>Message: {healthStatus?.message}</p>
      <p>Last Checked: {new Date(healthStatus?.timestamp).toLocaleString()}</p>
    </div>
  );
};

export default HealthCheck;
