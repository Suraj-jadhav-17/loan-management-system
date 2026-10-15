import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EmiPayments } from './emi-payments';

describe('EmiPayments', () => {
  let component: EmiPayments;
  let fixture: ComponentFixture<EmiPayments>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmiPayments],
    }).compileComponents();

    fixture = TestBed.createComponent(EmiPayments);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
