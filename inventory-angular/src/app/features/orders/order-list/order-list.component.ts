import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { Order } from '../../../core/models/order';

@Component({
  selector: 'app-order-list',
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.css'],
  standalone: false
})
export class OrderListComponent implements OnInit {
  orders: Order[] = [];
  filteredOrders: Order[] = [];
  loading = true;
  error: string | null = null;
  searchTerm = '';
  statusFilter = 'ALL';
  processingOrders: { [key: number]: boolean } = {};

  constructor(
    private apiService: ApiService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.fetchOrders();
  }

  fetchOrders(): void {
    this.loading = true;
    this.apiService.getAllOrders().subscribe({
      next: (data) => {
        this.orders = data;
        this.applyFilters();
        this.error = null;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching orders:', err);
        this.error = 'Failed to load orders';
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    const term = this.searchTerm.toLowerCase();
    this.filteredOrders = this.orders.filter(order => {
      const matchesSearch = 
        order.customerName?.toLowerCase().includes(term) ||
        order.customerEmail?.toLowerCase().includes(term) ||
        order.id.toString().includes(term);
      
      const matchesStatus = this.statusFilter === 'ALL' || order.status === this.statusFilter;
      
      return matchesSearch && matchesStatus;
    });
  }

  onSearchChange(): void {
    this.applyFilters();
  }

  onStatusFilterChange(): void {
    this.applyFilters();
  }

  processOrder(orderId: number): void {
    if (confirm('Are you sure you want to process this order?')) {
      this.processingOrders[orderId] = true;
      this.apiService.processOrder(orderId).subscribe({
        next: () => {
          this.fetchOrders();
          this.processingOrders[orderId] = false;
        },
        error: (err) => {
          console.error('Error processing order:', err);
          alert('Failed to process order');
          this.processingOrders[orderId] = false;
        }
      });
    }
  }

  cancelOrder(orderId: number): void {
    if (confirm('Are you sure you want to cancel this order?')) {
      this.processingOrders[orderId] = true;
      this.apiService.cancelOrder(orderId).subscribe({
        next: () => {
          this.fetchOrders();
          this.processingOrders[orderId] = false;
        },
        error: (err) => {
          console.error('Error canceling order:', err);
          alert('Failed to cancel order');
          this.processingOrders[orderId] = false;
        }
      });
    }
  }

  getStatusBadge(status: string): string {
    const statusClasses: { [key: string]: string } = {
      'CREATED': 'status-created',
      'PROCESSING': 'status-processing',
      'COMPLETED': 'status-completed',
      'CANCELLED': 'status-cancelled'
    };
    return statusClasses[status] || 'status-default';
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  createOrder(): void {
    this.router.navigate(['/orders/new']);
  }
}
