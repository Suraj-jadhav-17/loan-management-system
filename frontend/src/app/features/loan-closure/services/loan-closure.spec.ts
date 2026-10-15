import { TestBed } from '@angular/core/testing';
import { LoanClosure } from './loan-closure';

describe('LoanClosure', () => {
  let service: LoanClosure;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoanClosure);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
