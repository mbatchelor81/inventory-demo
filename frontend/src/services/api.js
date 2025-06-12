const API_BASE_URL = 'http://localhost:8080/api';

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

export const checkHealth = async () => {
  try {
    return await fetchWithAuth(`${API_BASE_URL}/ping`);
  } catch (error) {
    console.error('Error checking health:', error);
    throw error;
  }
};
