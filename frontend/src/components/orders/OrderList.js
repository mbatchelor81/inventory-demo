import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getAllOrders, processOrder, cancelOrder } from '../../services/api';
import './OrderList.css';

const OrderList = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [processingOrders, setProcessingOrders] = useState({});

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const data = await getAllOrders();
      setOrders(data);
      setError(null);
    } catch (err) {
      console.error('Error fetching orders:', err);
      setError('Failed to load orders');
    } finally {
      setLoading(false);
    }
  };

  const handleProcessOrder = async (orderId) => {
    if (window.confirm('Are you sure you want to process this order?')) {
      try {
        setProcessingOrders(prev => ({ ...prev, [orderId]: true }));
        await processOrder(orderId);
        fetchOrders(); // Refresh the list
      } catch (err) {
        console.error('Error processing order:', err);
        alert('Failed to process order');
      } finally {
        setProcessingOrders(prev => ({ ...prev, [orderId]: false }));
      }
    }
  };

  const handleCancelOrder = async (orderId) => {
    if (window.confirm('Are you sure you want to cancel this order?')) {
      try {
        setProcessingOrders(prev => ({ ...prev, [orderId]: true }));
        await cancelOrder(orderId);
        fetchOrders(); // Refresh the list
      } catch (err) {
        console.error('Error canceling order:', err);
        alert('Failed to cancel order');
      } finally {
        setProcessingOrders(prev => ({ ...prev, [orderId]: false }));
      }
    }
  };

  const filteredOrders = orders.filter(order => {
    const matchesSearch = 
      order.customerName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      order.customerEmail?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      order.id.toString().includes(searchTerm);
    
    const matchesStatus = statusFilter === 'ALL' || order.status === statusFilter;
    
    return matchesSearch && matchesStatus;
  });

  const getStatusBadge = (status) => {
    const statusClasses = {
      CREATED: 'status-created',
      PROCESSING: 'status-processing',
      COMPLETED: 'status-completed',
      CANCELLED: 'status-cancelled'
    };
    
    return statusClasses[status] || 'status-default';
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  if (loading) {
    return (
      <div className="order-list">
        <div className="loading">Loading orders...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="order-list">
        <div className="error-message">
          <h3>Error</h3>
          <p>{error}</p>
          <button onClick={fetchOrders} className="retry-button">Retry</button>
        </div>
      </div>
    );
  }

  return (
    <div className="order-list">
      <div className="order-list-header">
        <h2>Order Management</h2>
        <Link to="/orders/new" className="btn btn-primary">
          <i className="fas fa-plus"></i> Create Order
        </Link>
      </div>

      <div className="order-list-controls">
        <div className="search-box">
          <i className="fas fa-search"></i>
          <input
            type="text"
            placeholder="Search orders..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        
        <div className="filter-box">
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
          >
            <option value="ALL">All Status</option>
            <option value="CREATED">Created</option>
            <option value="PROCESSING">Processing</option>
            <option value="COMPLETED">Completed</option>
            <option value="CANCELLED">Cancelled</option>
          </select>
        </div>
      </div>

      {filteredOrders.length === 0 ? (
        <div className="no-orders">
          <i className="fas fa-shopping-cart"></i>
          <h3>No orders found</h3>
          <p>{searchTerm || statusFilter !== 'ALL' ? 'Try adjusting your search criteria' : 'Get started by creating your first order'}</p>
          {!searchTerm && statusFilter === 'ALL' && (
            <Link to="/orders/new" className="btn btn-primary">Create Order</Link>
          )}
        </div>
      ) : (
        <div className="order-table-container">
          <table className="order-table">
            <thead>
              <tr>
                <th>Order ID</th>
                <th>Customer</th>
                <th>Date</th>
                <th>Status</th>
                <th>Total</th>
                <th>Items</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredOrders.map(order => (
                <tr key={order.id}>
                  <td>
                    <div className="order-id">#{order.id}</div>
                  </td>
                  <td>
                    <div className="customer-info">
                      <div className="customer-name">{order.customerName || 'N/A'}</div>
                      <div className="customer-email">{order.customerEmail || 'N/A'}</div>
                    </div>
                  </td>
                  <td>{formatDate(order.orderDate)}</td>
                  <td>
                    <span className={`status-badge ${getStatusBadge(order.status)}`}>
                      {order.status}
                    </span>
                  </td>
                  <td>${order.totalAmount ? order.totalAmount.toFixed(2) : '0.00'}</td>
                  <td>{order.items ? order.items.length : 0} items</td>
                  <td>
                    <div className="action-buttons">
                      {order.status === 'CREATED' && (
                        <button 
                          onClick={() => handleProcessOrder(order.id)}
                          disabled={processingOrders[order.id]}
                          className="btn btn-primary btn-sm"
                        >
                          {processingOrders[order.id] ? (
                            <i className="fas fa-spinner fa-spin"></i>
                          ) : (
                            <>
                              <i className="fas fa-play"></i> Process
                            </>
                          )}
                        </button>
                      )}
                      {(order.status === 'CREATED' || order.status === 'PROCESSING') && (
                        <button 
                          onClick={() => handleCancelOrder(order.id)}
                          disabled={processingOrders[order.id]}
                          className="btn btn-danger btn-sm"
                        >
                          {processingOrders[order.id] ? (
                            <i className="fas fa-spinner fa-spin"></i>
                          ) : (
                            <>
                              <i className="fas fa-times"></i> Cancel
                            </>
                          )}
                        </button>
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
  );
};

export default OrderList;
