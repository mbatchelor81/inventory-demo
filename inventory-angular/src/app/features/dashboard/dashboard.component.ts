import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import { ApiService } from '../../core/services/api.service';
import { Product } from '../../core/models/product';
import { Inventory } from '../../core/models/inventory';
import { Order } from '../../core/models/order';

interface DashboardStats {
  totalProducts: number;
  lowStockItems: number;
  pendingOrders: number;
  totalOrders: number;
}

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  standalone: false
})
export class DashboardComponent implements OnInit {
  stats: DashboardStats = {
    totalProducts: 0,
    lowStockItems: 0,
    pendingOrders: 0,
    totalOrders: 0
  };
  loading = true;
  error: string | null = null;

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.fetchDashboardData();
  }

  fetchDashboardData(): void {
    this.loading = true;
    
    forkJoin({
      products: this.apiService.getAllProducts(),
      inventory: this.apiService.getAllInventory(),
      orders: this.apiService.getAllOrders()
    }).subscribe({
      next: ({ products, inventory, orders }) => {
        // Calculate stats
        this.stats = {
          totalProducts: products.length,
          lowStockItems: inventory.filter(item => item.quantity < 10).length,
          pendingOrders: orders.filter(order => 
            order.status === 'CREATED' || order.status === 'PROCESSING'
          ).length,
          totalOrders: orders.length
        };
        this.error = null;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching dashboard data:', err);
        this.error = 'Failed to load dashboard data';
        this.loading = false;
      }
    });
  }
}
