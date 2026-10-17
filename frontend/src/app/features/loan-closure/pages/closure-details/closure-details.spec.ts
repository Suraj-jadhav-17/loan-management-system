import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ClosureDetails } from './closure-details';

describe('ClosureDetails', () => {
  let component: ClosureDetails;
  let fixture: ComponentFixture<ClosureDetails>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClosureDetails],
    }).compileComponents();

    fixture = TestBed.createComponent(ClosureDetails);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
