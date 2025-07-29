import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import './Layout.css';

const Layout = ({ children }) => {
  const location = useLocation();

  const isActive = (path) => {
    return location.pathname === path;
  };

  return (
    <div className="app-layout">
      {/* Header - now spans full width */}
      <header className="app-header">
      </header>
      
      <div className="content-container">
        {/* Sidebar - now below header */}
        <nav className="sidebar">
          <ul className="sidebar-menu">
            <li>
              <Link 
                to="/" 
                className={isActive('/') ? 'active' : ''}
              >
                <i className="fas fa-tachometer-alt"></i>
                <span>Dashboard</span>
              </Link>
            </li>
            <li>
              <Link 
                to="/products" 
                className={isActive('/products') ? 'active' : ''}
              >
                <i className="fas fa-box-open"></i>
                <span>Products</span>
              </Link>
            </li>
            <li>
              <Link 
                to="/inventory" 
                className={isActive('/inventory') ? 'active' : ''}
              >
                <i className="fas fa-clipboard-list"></i>
                <span>Inventory</span>
              </Link>
            </li>
            <li>
              <Link 
                to="/orders" 
                className={isActive('/orders') ? 'active' : ''}
              >
                <i className="fas fa-shopping-cart"></i>
                <span>Orders</span>
              </Link>
            </li>
          </ul>
        </nav>

        {/* Main Content */}
        <main className="main-content">
          <div className="content">
            {children}
          </div>
        </main>
      </div>
    </div>
  );
};

export default Layout;
