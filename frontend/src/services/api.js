// Use relative URL to work with proxy configuration in package.json
const API_BASE_URL = '/api';

const fetchWithAuth = async (url, options = {}) => {
  const response = await fetch(url, {
    ...options,
    credentials: 'include', // Include cookies and HTTP authentication
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
  });

  if (!response.ok) {
    const error = new Error('Network response was not ok');
    error.status = response.status;
    throw error;
  }

  return response.json();
};

// Health check
export const checkHealth = async () => {
  try {
    return await fetchWithAuth(`${API_BASE_URL}/ping`);
  } catch (error) {
    console.error('Error checking health:', error);
    throw error;
  }
};

// Product API
export const getAllProducts = async () => {
  try {
    return await fetchWithAuth(`${API_BASE_URL}/products`);
  } catch (error) {
    console.error('Error fetching products:', error);
    throw error;
  }
};

export const getProductById = async (id) => {
  try {
    return await fetchWithAuth(`${API_BASE_URL}/products/${id}`);
  } catch (error) {
    console.error(`Error fetching product with id ${id}:`, error);
    throw error;
  }
};

export const createProduct = async (productData) => {
  try {
    return await fetchWithAuth(`${API_BASE_URL}/products`, {
      method: 'POST',
      body: JSON.stringify(productData),
    });
  } catch (error) {
    console.error('Error creating product:', error);
    throw error;
  }
};

export const updateProduct = async (id, productData) => {
  try {
    return await fetchWithAuth(`${API_BASE_URL}/products/${id}`, {
      method: 'PUT',
      body: JSON.stringify(productData),
    });
  } catch (error) {
    console.error(`Error updating product with id ${id}:`, error);
    throw error;
  }
};

export const deleteProduct = async (id) => {
  try {
    return await fetchWithAuth(`${API_BASE_URL}/products/${id}`, {
      method: 'DELETE',
    });
  } catch (error) {
    console.error(`Error deleting product with id ${id}:`, error);
    throw error;
  }
};

// Inventory API
export const getAllInventory = async () => {
  try {
    return await fetchWithAuth(`${API_BASE_URL}/inventory`);
  } catch (error) {
    console.error('Error fetching inventory:', error);
    throw error;
  }
};

export const getInventoryByProductId = async (productId) => {
  try {
    return await fetchWithAuth(`${API_BASE_URL}/inventory/${productId}`);
  } catch (error) {
    console.error(`Error fetching inventory for product id ${productId}:`, error);
    throw error;
  }
};

export const updateInventory = async (productId, quantity) => {
  try {
    return await fetchWithAuth(`${API_BASE_URL}/inventory/${productId}`, {
      method: 'PUT',
      body: JSON.stringify({ quantity }),
    });
  } catch (error) {
    console.error(`Error updating inventory for product id ${productId}:`, error);
    throw error;
  }
};

export const adjustInventory = async (productId, quantityChange) => {
  try {
    return await fetchWithAuth(`${API_BASE_URL}/inventory/${productId}/adjust`, {
      method: 'PATCH',
      body: JSON.stringify({ quantityChange }),
    });
  } catch (error) {
    console.error(`Error adjusting inventory for product id ${productId}:`, error);
    throw error;
  }
};

// Order API
export const getAllOrders = async () => {
  try {
    return await fetchWithAuth(`${API_BASE_URL}/orders`);
  } catch (error) {
    console.error('Error fetching orders:', error);
    throw error;
  }
};

export const getOrderById = async (id) => {
  try {
    return await fetchWithAuth(`${API_BASE_URL}/orders/${id}`);
  } catch (error) {
    console.error(`Error fetching order with id ${id}:`, error);
    throw error;
  }
};

export const createOrder = async (orderData) => {
  try {
    return await fetchWithAuth(`${API_BASE_URL}/orders`, {
      method: 'POST',
      body: JSON.stringify(orderData),
    });
  } catch (error) {
    console.error('Error creating order:', error);
    throw error;
  }
};

export const processOrder = async (id) => {
  try {
    return await fetchWithAuth(`${API_BASE_URL}/orders/${id}/process`, {
      method: 'POST',
    });
  } catch (error) {
    console.error(`Error processing order with id ${id}:`, error);
    throw error;
  }
};

export const cancelOrder = async (id) => {
  try {
    return await fetchWithAuth(`${API_BASE_URL}/orders/${id}/cancel`, {
      method: 'POST',
    });
  } catch (error) {
    console.error(`Error canceling order with id ${id}:`, error);
    throw error;
  }
};

export const getOrdersByStatus = async (status) => {
  try {
    return await fetchWithAuth(`${API_BASE_URL}/orders/status/${status}`);
  } catch (error) {
    console.error(`Error fetching orders with status ${status}:`, error);
    throw error;
  }
};

export const getAllCategories = async () => {
  try {
    return await fetchWithAuth(`${API_BASE_URL}/categories`);
  } catch (error) {
    console.error('Error fetching categories:', error);
    throw error;
  }
};

export const getCategoryById = async (id) => {
  try {
    return await fetchWithAuth(`${API_BASE_URL}/categories/${id}`);
  } catch (error) {
    console.error(`Error fetching category with id ${id}:`, error);
    throw error;
  }
};

export const createCategory = async (categoryData) => {
  try {
    return await fetchWithAuth(`${API_BASE_URL}/categories`, {
      method: 'POST',
      body: JSON.stringify(categoryData),
    });
  } catch (error) {
    console.error('Error creating category:', error);
    throw error;
  }
};

export const updateCategory = async (id, categoryData) => {
  try {
    return await fetchWithAuth(`${API_BASE_URL}/categories/${id}`, {
      method: 'PUT',
      body: JSON.stringify(categoryData),
    });
  } catch (error) {
    console.error(`Error updating category with id ${id}:`, error);
    throw error;
  }
};

export const deleteCategory = async (id) => {
  try {
    return await fetchWithAuth(`${API_BASE_URL}/categories/${id}`, {
      method: 'DELETE',
    });
  } catch (error) {
    console.error(`Error deleting category with id ${id}:`, error);
    throw error;
  }
};

export const getProductsByCategory = async (categoryId) => {
  try {
    return await fetchWithAuth(`${API_BASE_URL}/products/category/${categoryId}`);
  } catch (error) {
    console.error(`Error fetching products for category id ${categoryId}:`, error);
    throw error;
  }
};
