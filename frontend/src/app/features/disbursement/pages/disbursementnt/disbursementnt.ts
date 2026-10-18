import { Component, inject } from '@angular/core';
import { Disbursement, DisbursementRequest } from '../../models/disbursement.model';
import { DisbursementService } from '../../service/disbursement-service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  imports: [CommonModule,FormsModule],
  selector: 'app-disbursementnt',
  styleUrl: './disbursementnt.css',
  templateUrl: './disbursementnt.html',
})
export class Disbursementnt {

  private readonly disbursementService = inject(DisbursementService);

  disbursements: Disbursement[] = [];
  selectedDisbursement: Disbursement | null = null;

  isLoading = false;
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';

  page = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;

  searchText = '';
  startDate = '';
  endDate = '';

  bankPartner = '';
  disbursementDate = '';

  readonly pageSizes = [10, 25, 50, 100];

  ngOnInit(): void {
    this.loadDisbursements();
  }

  loadDisbursements(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.disbursementService
      .getAllDisbursements(this.page, this.pageSize)
      .subscribe({
        next: (response) => {
          this.disbursements = response.data?.content ?? [];
          this.totalElements = response.data?.totalElements ?? 0;
          this.totalPages = response.data?.totalPages ?? 0;
          this.isLoading = false;
        },
        error: (error) => {
          this.disbursements = [];
          this.errorMessage =
            error.error?.message || 'Unable to load disbursements.';
          this.isLoading = false;
        }
      });
  }

  get filteredDisbursements(): Disbursement[] {
    const search = this.searchText.trim().toLowerCase();

    return this.disbursements.filter((item) => {
      const customer = item.loanDeal?.customer;

      const customerName = [
        customer?.firstName,
        customer?.lastName
      ].filter(Boolean).join(' ').toLowerCase();

      const loanType = item.loanDeal?.loanType?.toLowerCase() ?? '';
      const customerId = String(customer?.customerId ?? '');
      const amount = String(item.disburseAmount ?? item.loanDeal?.amount ?? '');
      const createdAt = item.loanDeal?.createdAt?.slice(0, 10) ?? '';

      return (
        (!search ||
          customerName.includes(search) ||
          customerId.includes(search) ||
          loanType.includes(search) ||
          amount.includes(search)) &&
        (!this.startDate || createdAt >= this.startDate) &&
        (!this.endDate || createdAt <= this.endDate)
      );
    });
  }

  openDisbursement(item: Disbursement): void {
    this.selectedDisbursement = item;
    this.bankPartner = item.bankPartner ?? '';
    this.disbursementDate = item.disbursementDate
      ? item.disbursementDate.slice(0, 16)
      : '';
    this.errorMessage = '';
    this.successMessage = '';
  }

  closeDisbursement(): void {
    if (!this.isSubmitting) {
      this.selectedDisbursement = null;
    }
  }

  completeDisbursement(): void {
    if (
      !this.selectedDisbursement ||
      !this.bankPartner.trim() ||
      !this.disbursementDate ||
      this.isSubmitting
    ) {
      this.errorMessage = 'Enter the bank partner and disbursement date.';
      return;
    }

    const request: DisbursementRequest = {
      disbursementId: this.selectedDisbursement.disbursementId,
      bankPartner: this.bankPartner.trim(),
      disbursementDate: this.disbursementDate.length === 16
        ? `${this.disbursementDate}:00`
        : this.disbursementDate
    };

    this.isSubmitting = true;
    this.errorMessage = '';

    this.disbursementService.completeDisbursement(request).subscribe({
      next: (response) => {
        this.successMessage =
          response.message || 'Disbursement completed successfully.';
        this.selectedDisbursement = null;
        this.isSubmitting = false;
        this.loadDisbursements();
      },
      error: (error) => {
        this.errorMessage =
          error.error?.message || 'Unable to complete disbursement.';
        this.isSubmitting = false;
      }
    });
  }

  viewSanctionLetter(item: Disbursement): void {
    this.disbursementService
      .viewSanctionLetter(item.loanDeal.id)
      .subscribe({
        next: (pdf) => {
          const url = URL.createObjectURL(pdf);
          window.open(url, '_blank');
          setTimeout(() => URL.revokeObjectURL(url), 60000);
        },
        error: (error) => {
          this.errorMessage =
            error.error?.message || 'Unable to open the sanction letter.';
        }
      });
  }

  downloadSanctionLetter(item: Disbursement): void {
    this.disbursementService
      .downloadSanctionLetter(item.loanDeal.id)
      .subscribe({
        next: (pdf) => {
          const url = URL.createObjectURL(pdf);
          const link = document.createElement('a');
          link.href = url;
          link.download = `sanction-letter-${item.loanDeal.id}.pdf`;
          link.click();
          URL.revokeObjectURL(url);
        },
        error: (error) => {
          this.errorMessage =
            error.error?.message || 'Unable to download the sanction letter.';
        }
      });
  }

  clearFilters(): void {
    this.searchText = '';
    this.startDate = '';
    this.endDate = '';
    this.page = 0;
    this.loadDisbursements();
  }

  changePageSize(value: string): void {
    this.pageSize = Number(value);
    this.page = 0;
    this.loadDisbursements();
  }

  previousPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadDisbursements();
    }
  }

  nextPage(): void {
    if (this.page + 1 < this.totalPages) {
      this.page++;
      this.loadDisbursements();
    }
  }

  get firstEntry(): number {
    return this.totalElements === 0 ? 0 : this.page * this.pageSize + 1;
  }

  get lastEntry(): number {
    return Math.min((this.page + 1) * this.pageSize, this.totalElements);
  }
}
