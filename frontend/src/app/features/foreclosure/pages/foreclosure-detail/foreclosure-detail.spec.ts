import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ForeclosureDetail } from './foreclosure-detail';

describe('ForeclosureDetail', () => {
  let component: ForeclosureDetail;
  let fixture: ComponentFixture<ForeclosureDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ForeclosureDetail],
    }).compileComponents();

    fixture = TestBed.createComponent(ForeclosureDetail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
