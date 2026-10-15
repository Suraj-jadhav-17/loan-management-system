import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EmiSchedulerComponent } from './emi-scheduler';

describe('EmiScheduler', () => {
  let component: EmiSchedulerComponent;
  let fixture: ComponentFixture<EmiSchedulerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmiSchedulerComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(EmiSchedulerComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
