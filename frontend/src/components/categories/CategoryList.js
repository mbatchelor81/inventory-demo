import React, { useState, useEffect } from 'react';
import { getAllCategories, getProductsByCategory } from '../../services/api';
import './CategoryList.css';

const CategoryList = () => {
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      setLoading(true);
      const data = await getAllCategories();
      setCategories(data);
      setError(null);
    } catch (err) {
      console.error('Error fetching categories:', err);
      setError('Failed to load categories');
    } finally {
      setLoading(false);
    }
  };

  const handleCategoryClick = async (category) => {
    try {
      setSelectedCategory(category);
      setLoading(true);
      const data = await getProductsByCategory(category.id);
      setProducts(data);
      setError(null);
    } catch (err) {
      console.error('Error fetching products for category:', err);
      setError('Failed to load products for this category');
    } finally {
      setLoading(false);
    }
  };

  const handleClearSelection = () => {
    setSelectedCategory(null);
    setProducts([]);
  };

  if (loading && categories.length === 0) {
    return (
      <div className="category-list">
        <div className="loading">Loading categories...</div>
      </div>
    );
  }

  if (error && categories.length === 0) {
    return (
      <div className="category-list">
        <div className="error-message">
          <h3>Error</h3>
          <p>{error}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="category-list">
      <div className="category-header">
        <h2>Product Categories</h2>
      </div>

      <div className="categories-grid">
        {categories.map((category) => (
          <div
            key={category.id}
            className={`category-card ${selectedCategory?.id === category.id ? 'selected' : ''}`}
            onClick={() => handleCategoryClick(category)}
          >
            <h3>{category.name}</h3>
            <p>{category.description}</p>
          </div>
        ))}
      </div>

      {selectedCategory && (
        <div className="category-products">
          <div className="products-header">
            <h3>Products in {selectedCategory.name}</h3>
            <button onClick={handleClearSelection} className="clear-button">
              Clear Selection
            </button>
          </div>

          {loading ? (
            <div className="loading">Loading products...</div>
          ) : products.length === 0 ? (
            <p className="no-products">No products in this category</p>
          ) : (
            <div className="products-grid">
              {products.map((product) => (
                <div key={product.id} className="product-card">
                  <h4>{product.name}</h4>
                  <p className="product-sku">SKU: {product.sku}</p>
                  <p className="product-description">{product.description}</p>
                  <p className="product-price">${product.price}</p>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default CategoryList;
