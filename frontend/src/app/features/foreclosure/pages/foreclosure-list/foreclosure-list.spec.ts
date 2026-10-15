import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ForeclosureList } from './foreclosure-list';

describe('ForeclosureList', () => {
  let component: ForeclosureList;
  let fixture: ComponentFixture<ForeclosureList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ForeclosureList],
    }).compileComponents();

    fixture = TestBed.createComponent(ForeclosureList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
