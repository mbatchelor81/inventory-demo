import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { getAllProducts, createOrder } from '../../services/api';
import './OrderForm.css';

const OrderForm = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    customerName: '',
    customerEmail: '',
    items: []
  });

  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [formErrors, setFormErrors] = useState({});

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      const data = await getAllProducts();
      setProducts(data);
    } catch (err) {
      console.error('Error fetching products:', err);
      setError('Failed to load products');
    }
  };

  const validateForm = () => {
    const errors = {};
    
    if (!formData.customerName.trim()) {
      errors.customerName = 'Customer name is required';
    }
    
    if (!formData.customerEmail.trim()) {
      errors.customerEmail = 'Customer email is required';
    } else if (!/\S+@\S+\.\S+/.test(formData.customerEmail)) {
      errors.customerEmail = 'Please enter a valid email address';
    }
    
    if (formData.items.length === 0) {
      errors.items = 'At least one item is required';
    }
    
    // Validate each item
    formData.items.forEach((item, index) => {
      if (!item.productId) {
        errors[`item_${index}_product`] = 'Product is required';
      }
      if (!item.quantity || item.quantity <= 0) {
        errors[`item_${index}_quantity`] = 'Quantity must be greater than 0';
      }
    });
    
    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Clear error when user starts typing
    if (formErrors[name]) {
      setFormErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  const addItem = () => {
    setFormData(prev => ({
      ...prev,
      items: [...prev.items, { productId: '', quantity: 1 }]
    }));
  };

  const removeItem = (index) => {
    setFormData(prev => ({
      ...prev,
      items: prev.items.filter((_, i) => i !== index)
    }));
  };

  const updateItem = (index, field, value) => {
    setFormData(prev => ({
      ...prev,
      items: prev.items.map((item, i) => 
        i === index ? { ...item, [field]: value } : item
      )
    }));
    
    // Clear item-specific errors
    const errorKey = `item_${index}_${field}`;
    if (formErrors[errorKey]) {
      setFormErrors(prev => ({
        ...prev,
        [errorKey]: ''
      }));
    }
  };

  const calculateTotal = () => {
    return formData.items.reduce((total, item) => {
      const product = products.find(p => p.id === parseInt(item.productId));
      if (product && item.quantity) {
        return total + (product.price * item.quantity);
      }
      return total;
    }, 0);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    try {
      setLoading(true);
      
      // Format the order data for the API
      const orderData = {
        customerName: formData.customerName,
        customerEmail: formData.customerEmail,
        items: formData.items.map(item => ({
          productId: parseInt(item.productId),
          quantity: parseInt(item.quantity)
        }))
      };
      
      await createOrder(orderData);
      navigate('/orders');
    } catch (err) {
      console.error('Error creating order:', err);
      setError('Failed to create order');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="order-form">
      <div className="form-header">
        <h2>Create New Order</h2>
        <Link to="/orders" className="btn btn-secondary">
          <i className="fas fa-arrow-left"></i> Back to Orders
        </Link>
      </div>

      {error && (
        <div className="error-message">
          <p>{error}</p>
        </div>
      )}

      <div className="form-container">
        <form onSubmit={handleSubmit}>
          <div className="customer-section">
            <h3>Customer Information</h3>
            
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="customerName">Customer Name *</label>
                <input
                  type="text"
                  id="customerName"
                  name="customerName"
                  value={formData.customerName}
                  onChange={handleChange}
                  className={formErrors.customerName ? 'error' : ''}
                  placeholder="Enter customer name"
                />
                {formErrors.customerName && <span className="error-text">{formErrors.customerName}</span>}
              </div>

              <div className="form-group">
                <label htmlFor="customerEmail">Customer Email *</label>
                <input
                  type="email"
                  id="customerEmail"
                  name="customerEmail"
                  value={formData.customerEmail}
                  onChange={handleChange}
                  className={formErrors.customerEmail ? 'error' : ''}
                  placeholder="Enter customer email"
                />
                {formErrors.customerEmail && <span className="error-text">{formErrors.customerEmail}</span>}
              </div>
            </div>
          </div>

          <div className="items-section">
            <div className="items-header">
              <h3>Order Items</h3>
              <button type="button" onClick={addItem} className="btn btn-primary btn-sm">
                <i className="fas fa-plus"></i> Add Item
              </button>
            </div>
            
            {formErrors.items && <span className="error-text">{formErrors.items}</span>}
            
            {formData.items.length === 0 ? (
              <div className="no-items">
                <p>No items added yet. Click "Add Item" to get started.</p>
              </div>
            ) : (
              <div className="items-list">
                {formData.items.map((item, index) => (
                  <div key={index} className="item-row">
                    <div className="item-fields">
                      <div className="form-group">
                        <label>Product *</label>
                        <select
                          value={item.productId}
                          onChange={(e) => updateItem(index, 'productId', e.target.value)}
                          className={formErrors[`item_${index}_product`] ? 'error' : ''}
                        >
                          <option value="">Select a product</option>
                          {products.map(product => (
                            <option key={product.id} value={product.id}>
                              {product.name} - ${product.price?.toFixed(2)} ({product.sku})
                            </option>
                          ))}
                        </select>
                        {formErrors[`item_${index}_product`] && (
                          <span className="error-text">{formErrors[`item_${index}_product`]}</span>
                        )}
                      </div>
                      
                      <div className="form-group">
                        <label>Quantity *</label>
                        <input
                          type="number"
                          value={item.quantity}
                          onChange={(e) => updateItem(index, 'quantity', e.target.value)}
                          className={formErrors[`item_${index}_quantity`] ? 'error' : ''}
                          min="1"
                        />
                        {formErrors[`item_${index}_quantity`] && (
                          <span className="error-text">{formErrors[`item_${index}_quantity`]}</span>
                        )}
                      </div>
                      
                      <div className="item-total">
                        {(() => {
                          const product = products.find(p => p.id === parseInt(item.productId));
                          const subtotal = product && item.quantity ? product.price * item.quantity : 0;
                          return `$${subtotal.toFixed(2)}`;
                        })()}
                      </div>
                    </div>
                    
                    <button
                      type="button"
                      onClick={() => removeItem(index)}
                      className="btn btn-danger btn-sm remove-item"
                    >
                      <i className="fas fa-trash"></i>
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>

          {formData.items.length > 0 && (
            <div className="order-summary">
              <div className="total-amount">
                <strong>Total: ${calculateTotal().toFixed(2)}</strong>
              </div>
            </div>
          )}

          <div className="form-actions">
            <button type="button" onClick={() => navigate('/orders')} className="btn btn-secondary">
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? (
                <>
                  <i className="fas fa-spinner fa-spin"></i> Creating Order...
                </>
              ) : (
                <>
                  <i className="fas fa-save"></i> Create Order
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default OrderForm;
