import { TestBed } from '@angular/core/testing';
import { DealReviewService } from './deal-review-service';

describe('DealReviewService', () => {
  let service: DealReviewService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DealReviewService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
