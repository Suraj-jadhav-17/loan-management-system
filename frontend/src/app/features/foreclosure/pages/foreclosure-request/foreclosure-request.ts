import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Foreclosure } from '../../services/foreclosure';
import {
  ForeClosureResponse,
  ForeClosureType
} from '../../models/foreclosure.model';
import { environment } from '../../../../../environments/environment';

interface RazorpayResponse {
  razorpay_order_id: string;
  razorpay_payment_id: string;
  razorpay_signature: string;
}

interface RazorpayFailureResponse {
  error?: {
    description?: string;
  };
}

interface RazorpayOptions {
  key: string;
  amount: number;
  currency: string;
  name: string;
  description: string;
  order_id: string;
  handler: (response: RazorpayResponse) => void;
  modal?: {
    ondismiss?: () => void;
  };
}

interface RazorpayInstance {
  open(): void;
  on(
    event: string,
    handler: (response: RazorpayFailureResponse) => void
  ): void;
}

type RazorpayConstructor = new (
  options: RazorpayOptions
) => RazorpayInstance;

type RazorpayWindow = Window & {
  Razorpay?: RazorpayConstructor;
};

@Component({
  selector: 'app-foreclosure-request',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './foreclosure-request.html',
  styleUrl: './foreclosure-request.scss'
})
export class ForeclosureRequestPage {
  private readonly foreclosure = inject(Foreclosure);

  loanAccountId: number | null = null;
  type: ForeClosureType = 'FULL';
  partialAmount: number | null = null;
  requestId: number | null = null;

  request: ForeClosureResponse | null = null;
  loading = false;
  error = '';
  message = '';

  submitRequest(): void {
    if (this.loading) {
      return;
    }

    this.error = '';
    this.message = '';
    this.request = null;

    if (
      this.loanAccountId === null ||
      !Number.isInteger(Number(this.loanAccountId)) ||
      Number(this.loanAccountId) <= 0
    ) {
      this.error = 'Enter a valid loan account ID.';
      return;
    }

    if (
      this.type === 'PARTIAL' &&
      (
        this.partialAmount === null ||
        !Number.isFinite(Number(this.partialAmount)) ||
        Number(this.partialAmount) <= 0
      )
    ) {
      this.error = 'Enter a valid partial foreclosure amount.';
      return;
    }

    this.loading = true;

    this.foreclosure.createRequest({
      loanAccountId: Number(this.loanAccountId),
      foreClosureType: this.type,
      partialAmount:
        this.type === 'PARTIAL' ? Number(this.partialAmount) : null
    }).subscribe({
      next: (result) => {
        this.request = result;
        this.requestId = result.foreClosureId;
        this.loading = false;
        this.message = 'Foreclosure request submitted successfully.';
      },
      error: (err) => {
        this.loading = false;
        this.error =
          err?.error?.message || 'Unable to submit foreclosure request.';
      }
    });
  }

  loadRequest(): void {
    if (this.loading) {
      return;
    }

    this.error = '';
    this.message = '';
    this.request = null;

    if (
      this.requestId === null ||
      !Number.isInteger(Number(this.requestId)) ||
      Number(this.requestId) <= 0
    ) {
      this.error = 'Enter a valid foreclosure request ID.';
      return;
    }

    this.loading = true;

    this.foreclosure.getCustomerRequest(Number(this.requestId)).subscribe({
      next: (result) => {
        this.request = result;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.error =
          err?.error?.message || 'Unable to load foreclosure request.';
      }
    });
  }

  payRequest(): void {
    if (this.loading || !this.request) {
      return;
    }

    if (this.request.status !== 'APPROVED' || this.request.isPaid) {
      this.error = 'Only approved and unpaid requests can be paid.';
      return;
    }

    const keyId = environment.razorpayKeyId;

    if (!keyId || keyId === 'YOUR_RAZORPAY_KEY_ID') {
      this.error =
        'Configure your Razorpay test Key ID in environment.ts before payment.';
      return;
    }

    if (
      !Number.isFinite(Number(this.request.foreClosureAmount)) ||
      Number(this.request.foreClosureAmount) <= 0
    ) {
      this.error = 'The foreclosure amount is invalid.';
      return;
    }

    this.error = '';
    this.message = '';
    this.loading = true;

    this.foreclosure
      .createPaymentOrder(this.request.foreClosureId)
      .subscribe({
        next: (orderId) => {
          if (!orderId || !orderId.trim()) {
            this.loading = false;
            this.error = 'The server did not return a valid payment order ID.';
            return;
          }

          this.openRazorpayCheckout(orderId.trim());
        },
        error: (err) => {
          this.loading = false;
          this.error =
            err?.error?.message ||
            'Unable to create a payment order. Please try again later.';
        }
      });
  }

  private openRazorpayCheckout(orderId: string): void {
    this.loadRazorpayScript()
      .then((loaded) => {
        if (!loaded) {
          this.loading = false;
          this.error =
            'Unable to load Razorpay Checkout. Check your internet connection and try again.';
          return;
        }

        const razorpayWindow = window as RazorpayWindow;
        const Razorpay = razorpayWindow.Razorpay;

        if (!Razorpay || !this.request) {
          this.loading = false;
          this.error = 'Razorpay Checkout could not be initialized.';
          return;
        }

        const amountInPaise = Math.round(
          Number(this.request.foreClosureAmount) * 100
        );

        const options: RazorpayOptions = {
          key: environment.razorpayKeyId,
          amount: amountInPaise,
          currency: 'INR',
          name: 'Loan Management System',
          description: `Foreclosure Request #${this.request.foreClosureId}`,
          order_id: orderId,
          handler: (response: RazorpayResponse) => {
            this.confirmPayment(response);
          },
          modal: {
            ondismiss: () => {
              this.message = 'Payment window closed. Your request is unchanged.';
            }
          }
        };

        const checkout = new Razorpay(options);

        checkout.on('payment.failed', (response) => {
          this.error =
            response?.error?.description ||
            'Payment failed. Please try again.';
        });

        this.loading = false;
        checkout.open();
      })
      .catch(() => {
        this.loading = false;
        this.error = 'Unable to initialize Razorpay Checkout.';
      });
  }

  private loadRazorpayScript(): Promise<boolean> {
    if ((window as RazorpayWindow).Razorpay) {
      return Promise.resolve(true);
    }

    return new Promise<boolean>((resolve) => {
      const existingScript = document.querySelector(
        'script[src="https://checkout.razorpay.com/v1/checkout.js"]'
      ) as HTMLScriptElement | null;

      if (existingScript) {
        existingScript.addEventListener(
          'load',
          () => resolve(!!(window as RazorpayWindow).Razorpay),
          { once: true }
        );
        existingScript.addEventListener(
          'error',
          () => resolve(false),
          { once: true }
        );
        return;
      }

      const script = document.createElement('script');
      script.src = 'https://checkout.razorpay.com/v1/checkout.js';
      script.async = true;

      script.onload = () => {
        resolve(!!(window as RazorpayWindow).Razorpay);
      };

      script.onerror = () => resolve(false);

      document.body.appendChild(script);
    });
  }

  private confirmPayment(response: RazorpayResponse): void {
    if (!this.request) {
      this.error = 'Foreclosure request details are unavailable.';
      return;
    }

    this.loading = true;
    this.error = '';
    this.message = '';

    this.foreclosure.makePayment({
      foreClosureId: this.request.foreClosureId,
      paidAmount: Number(this.request.foreClosureAmount),
      orderId: response.razorpay_order_id,
      paymentId: response.razorpay_payment_id,
      signature: response.razorpay_signature
    }).subscribe({
      next: (result) => {
        this.request = result;
        this.requestId = result.foreClosureId;
        this.loading = false;
        this.message = 'Payment confirmation received successfully.';
      },
      error: (err) => {
        this.loading = false;
        this.error =
          err?.error?.message ||
          'Payment was completed, but the server could not confirm it. Reload the request status before retrying.';
      }
    });
  }
}