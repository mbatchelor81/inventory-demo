import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { Product } from '../../../core/models/product';

@Component({
  selector: 'app-order-form',
  templateUrl: './order-form.component.html',
  styleUrls: ['./order-form.component.css'],
  standalone: false
})
export class OrderFormComponent implements OnInit {
  orderForm: FormGroup;
  products: Product[] = [];
  loading = false;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private apiService: ApiService,
    private router: Router
  ) {
    this.orderForm = this.fb.group({
      customerName: ['', Validators.required],
      customerEmail: ['', [Validators.required, Validators.email]],
      items: this.fb.array([])
    });
  }

  ngOnInit(): void {
    this.fetchProducts();
  }

  get items(): FormArray {
    return this.orderForm.get('items') as FormArray;
  }

  getItemFormGroup(index: number): FormGroup {
    return this.items.at(index) as FormGroup;
  }

  fetchProducts(): void {
    this.apiService.getAllProducts().subscribe({
      next: (data) => {
        this.products = data;
        this.error = null;
      },
      error: (err) => {
        console.error('Error fetching products:', err);
        this.error = 'Failed to load products';
      }
    });
  }

  addItem(): void {
    const itemGroup = this.fb.group({
      productId: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]]
    });
    this.items.push(itemGroup);
  }

  removeItem(index: number): void {
    this.items.removeAt(index);
  }

  getItemSubtotal(index: number): number {
    const item = this.items.at(index).value;
    const product = this.products.find(p => p.id === +item.productId);
    if (product && item.quantity) {
      return product.price * item.quantity;
    }
    return 0;
  }

  calculateTotal(): number {
    return this.items.controls.reduce((total, control) => {
      const item = control.value;
      const product = this.products.find(p => p.id === +item.productId);
      if (product && item.quantity) {
        return total + (product.price * item.quantity);
      }
      return total;
    }, 0);
  }

  onSubmit(): void {
    if (this.orderForm.invalid) {
      Object.keys(this.orderForm.controls).forEach(key => {
        this.orderForm.get(key)?.markAsTouched();
      });
      this.items.controls.forEach(control => {
        Object.keys((control as FormGroup).controls).forEach(key => {
          control.get(key)?.markAsTouched();
        });
      });
      return;
    }

    if (this.items.length === 0) {
      alert('At least one item is required');
      return;
    }

    this.loading = true;
    const orderData = {
      customerName: this.orderForm.value.customerName,
      customerEmail: this.orderForm.value.customerEmail,
      items: this.orderForm.value.items.map((item: any) => ({
        productId: +item.productId,
        quantity: +item.quantity
      }))
    };

    this.apiService.createOrder(orderData).subscribe({
      next: () => {
        this.router.navigate(['/orders']);
      },
      error: (err) => {
        console.error('Error creating order:', err);
        this.error = 'Failed to create order';
        this.loading = false;
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/orders']);
  }
}
