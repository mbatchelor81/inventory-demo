import React, { useState, useEffect } from 'react';
import { getAllInventory, adjustInventory } from '../../services/api';
import './InventoryList.css';

const InventoryList = () => {
  const [inventoryItems, setInventoryItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [adjustingItems, setAdjustingItems] = useState({});

  useEffect(() => {
    fetchInventory();
  }, []);

  const fetchInventory = async () => {
    try {
      setLoading(true);
      const data = await getAllInventory();
      setInventoryItems(data);
      setError(null);
    } catch (err) {
      console.error('Error fetching inventory:', err);
      setError('Failed to load inventory');
    } finally {
      setLoading(false);
    }
  };

  const handleAdjustQuantity = async (productId, currentQuantity, adjustment) => {
    const newQuantity = currentQuantity + adjustment;
    
    if (newQuantity < 0) {
      alert('Quantity cannot be negative');
      return;
    }
    
    try {
      setAdjustingItems(prev => ({ ...prev, [productId]: true }));
      await adjustInventory(productId, adjustment);
      // Refresh the inventory list
      fetchInventory();
    } catch (err) {
      console.error('Error adjusting inventory:', err);
      alert('Failed to adjust inventory');
    } finally {
      setAdjustingItems(prev => ({ ...prev, [productId]: false }));
    }
  };

  const filteredInventory = inventoryItems.filter(item =>
    item.product.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    item.product.sku.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const getStockStatus = (quantity) => {
    if (quantity === 0) return 'out-of-stock';
    if (quantity < 10) return 'low-stock';
    if (quantity < 50) return 'medium-stock';
    return 'in-stock';
  };

  const getStockStatusText = (quantity) => {
    if (quantity === 0) return 'Out of Stock';
    if (quantity < 10) return 'Low Stock';
    if (quantity < 50) return 'Limited Stock';
    return 'In Stock';
  };

  if (loading) {
    return (
      <div className="inventory-list">
        <div className="loading">Loading inventory...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="inventory-list">
        <div className="error-message">
          <h3>Error</h3>
          <p>{error}</p>
          <button onClick={fetchInventory} className="retry-button">Retry</button>
        </div>
      </div>
    );
  }

  return (
    <div className="inventory-list">
      <div className="inventory-list-header">
        <h2>Inventory Tracking</h2>
        <button onClick={fetchInventory} className="btn btn-secondary">
          <i className="fas fa-sync-alt"></i> Refresh
        </button>
      </div>

      <div className="inventory-list-controls">
        <div className="search-box">
          <i className="fas fa-search"></i>
          <input
            type="text"
            placeholder="Search inventory..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      {filteredInventory.length === 0 ? (
        <div className="no-inventory">
          <i className="fas fa-clipboard-list"></i>
          <h3>No inventory items found</h3>
          <p>{searchTerm ? 'Try adjusting your search criteria' : 'No inventory items available'}</p>
        </div>
      ) : (
        <div className="inventory-table-container">
          <table className="inventory-table">
            <thead>
              <tr>
                <th>Product</th>
                <th>SKU</th>
                <th>Current Stock</th>
                <th>Status</th>
                <th>Adjust Quantity</th>
              </tr>
            </thead>
            <tbody>
              {filteredInventory.map(item => (
                <tr key={item.product.id}>
                  <td>
                    <div className="product-info">
                      <div className="product-name">{item.product.name}</div>
                    </div>
                  </td>
                  <td>{item.product.sku}</td>
                  <td>
                    <div className="quantity-display">
                      {item.quantity}
                    </div>
                  </td>
                  <td>
                    <span className={`stock-status ${getStockStatus(item.quantity)}`}>
                      {getStockStatusText(item.quantity)}
                    </span>
                  </td>
                  <td>
                    <div className="adjust-controls">
                      <button 
                        onClick={() => handleAdjustQuantity(item.product.id, item.quantity, -1)}
                        disabled={adjustingItems[item.product.id]}
                        className="btn btn-secondary btn-sm"
                      >
                        {adjustingItems[item.product.id] ? (
                          <i className="fas fa-spinner fa-spin"></i>
                        ) : (
                          <i className="fas fa-minus"></i>
                        )}
                      </button>
                      <button 
                        onClick={() => handleAdjustQuantity(item.product.id, item.quantity, 1)}
                        disabled={adjustingItems[item.product.id]}
                        className="btn btn-primary btn-sm"
                      >
                        {adjustingItems[item.product.id] ? (
                          <i className="fas fa-spinner fa-spin"></i>
                        ) : (
                          <i className="fas fa-plus"></i>
                        )}
                      </button>
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

export default InventoryList;
