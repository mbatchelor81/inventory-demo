import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../core/services/api.service';
import { Inventory } from '../../core/models/inventory';

@Component({
  selector: 'app-inventory',
  templateUrl: './inventory.component.html',
  styleUrls: ['./inventory.component.css']
})
export class InventoryComponent implements OnInit {
  inventoryItems: Inventory[] = [];
  filteredInventory: Inventory[] = [];
  loading = true;
  error: string | null = null;
  searchTerm = '';
  adjustingItems: { [key: number]: boolean } = {};

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.fetchInventory();
  }

  fetchInventory(): void {
    this.loading = true;
    this.apiService.getAllInventory().subscribe({
      next: (data) => {
        this.inventoryItems = data;
        this.filteredInventory = data;
        this.error = null;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching inventory:', err);
        this.error = 'Failed to load inventory';
        this.loading = false;
      }
    });
  }

  onSearchChange(): void {
    const term = this.searchTerm.toLowerCase();
    this.filteredInventory = this.inventoryItems.filter(item =>
      item.product.name.toLowerCase().includes(term) ||
      item.product.sku.toLowerCase().includes(term)
    );
  }

  adjustQuantity(productId: number, currentQuantity: number, adjustment: number): void {
    const newQuantity = currentQuantity + adjustment;
    
    if (newQuantity < 0) {
      alert('Quantity cannot be negative');
      return;
    }

    this.adjustingItems[productId] = true;
    this.apiService.adjustInventory(productId, adjustment).subscribe({
      next: () => {
        this.fetchInventory();
        this.adjustingItems[productId] = false;
      },
      error: (err) => {
        console.error('Error adjusting inventory:', err);
        alert('Failed to adjust inventory');
        this.adjustingItems[productId] = false;
      }
    });
  }

  getStockStatus(quantity: number): string {
    if (quantity === 0) return 'out-of-stock';
    if (quantity < 10) return 'low-stock';
    if (quantity < 50) return 'medium-stock';
    return 'in-stock';
  }

  getStockStatusText(quantity: number): string {
    if (quantity === 0) return 'Out of Stock';
    if (quantity < 10) return 'Low Stock';
    if (quantity < 50) return 'Limited Stock';
    return 'In Stock';
  }
}
