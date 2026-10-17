import { TestBed } from '@angular/core/testing';
import { LoanDealService } from './loan-deal-service';

describe('LoanDealService', () => {
  let service: LoanDealService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoanDealService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
