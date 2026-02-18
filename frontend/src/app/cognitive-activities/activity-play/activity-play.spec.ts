import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivityPlay } from './activity-play';

describe('ActivityPlay', () => {
  let component: ActivityPlay;
  let fixture: ComponentFixture<ActivityPlay>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActivityPlay]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActivityPlay);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
