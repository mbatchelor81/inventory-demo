import './App.css';
import HealthCheck from './components/HealthCheck';

function App() {
  return (
    <div className="app">
      <header className="app-header">
        <h1>Inventory Management System</h1>
      </header>
      <main className="app-content">
        <div className="health-check-container">
          <h2>System Status</h2>
          <HealthCheck />
        </div>
      </main>
      <footer className="app-footer">
        <p>Inventory Service Demo</p>
      </footer>
    </div>
  );
}

export default App;
