import { render, screen } from '@testing-library/react';
import App from './App';

test('renders inventory management system', () => {
  render(<App />);
  const headerElement = screen.getByText(/inventory management system/i);
  expect(headerElement).toBeInTheDocument();
});
