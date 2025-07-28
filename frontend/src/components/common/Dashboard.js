import React, { useState, useEffect } from 'react';
import { getAllProducts, getAllInventory, getAllOrders } from '../../services/api';
import './Dashboard.css';

const Dashboard = () => {
  const [stats, setStats] = useState({
    totalProducts: 0,
    lowStockItems: 0,
    pendingOrders: 0,
    totalOrders: 0
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setLoading(true);
        const [products, inventory, orders] = await Promise.all([
          getAllProducts(),
          getAllInventory(),
          getAllOrders()
        ]);

        // Calculate stats
        const totalProducts = products.length;
        const lowStockItems = inventory.filter(item => item.quantity < 10).length;
        const pendingOrders = orders.filter(order => order.status === 'CREATED' || order.status === 'PROCESSING').length;
        const totalOrders = orders.length;

        setStats({
          totalProducts,
          lowStockItems,
          pendingOrders,
          totalOrders
        });
        setError(null);
      } catch (err) {
        console.error('Error fetching dashboard data:', err);
        setError('Failed to load dashboard data');
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  if (loading) {
    return (
      <div className="dashboard">
        <div className="loading">Loading dashboard data...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="dashboard">
        <div className="error-message">
          <h3>Error</h3>
          <p>{error}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h2>Dashboard Overview</h2>
        <p>Welcome to your inventory management system</p>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon products">
            <i className="fas fa-box-open"></i>
          </div>
          <div className="stat-info">
            <h3>{stats.totalProducts}</h3>
            <p>Total Products</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon inventory">
            <i className="fas fa-exclamation-triangle"></i>
          </div>
          <div className="stat-info">
            <h3>{stats.lowStockItems}</h3>
            <p>Low Stock Items</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon orders">
            <i className="fas fa-shopping-cart"></i>
          </div>
          <div className="stat-info">
            <h3>{stats.pendingOrders}</h3>
            <p>Pending Orders</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon total">
            <i className="fas fa-chart-line"></i>
          </div>
          <div className="stat-info">
            <h3>{stats.totalOrders}</h3>
            <p>Total Orders</p>
          </div>
        </div>
      </div>

      <div className="dashboard-content">
        <div className="recent-section">
          <h3>Quick Actions</h3>
          <div className="quick-actions">
            <button className="action-button">
              <span>Add Product</span>
            </button>
            <button className="action-button">
              <span>Check Inventory</span>
            </button>
            <button className="action-button">
              <span>Create Order</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
