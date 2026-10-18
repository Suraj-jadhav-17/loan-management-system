import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ForeclosureRequestPage } from './foreclosure-request';

describe('ForeclosureRequest', () => {
  let component: ForeclosureRequestPage;
  let fixture: ComponentFixture<ForeclosureRequestPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ForeclosureRequestPage],
    }).compileComponents();

    fixture = TestBed.createComponent(ForeclosureRequestPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
