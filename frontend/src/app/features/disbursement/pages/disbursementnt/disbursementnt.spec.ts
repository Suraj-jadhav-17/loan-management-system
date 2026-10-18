import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Disbursementnt } from './disbursementnt';

describe('Disbursementnt', () => {
  let component: Disbursementnt;
  let fixture: ComponentFixture<Disbursementnt>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Disbursementnt],
    }).compileComponents();

    fixture = TestBed.createComponent(Disbursementnt);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
