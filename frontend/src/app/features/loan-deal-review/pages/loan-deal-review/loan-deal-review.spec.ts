import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoanDealReview } from './loan-deal-review';

describe('LoanDealReview', () => {
  let component: LoanDealReview;
  let fixture: ComponentFixture<LoanDealReview>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoanDealReview],
    }).compileComponents();

    fixture = TestBed.createComponent(LoanDealReview);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
