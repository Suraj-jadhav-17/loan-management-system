import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoanDeal } from './loan-deal';

describe('LoanDeal', () => {
  let component: LoanDeal;
  let fixture: ComponentFixture<LoanDeal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoanDeal],
    }).compileComponents();

    fixture = TestBed.createComponent(LoanDeal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
