import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ClosureHistory } from './closure-history';

describe('ClosureHistory', () => {
  let component: ClosureHistory;
  let fixture: ComponentFixture<ClosureHistory>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClosureHistory],
    }).compileComponents();

    fixture = TestBed.createComponent(ClosureHistory);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
