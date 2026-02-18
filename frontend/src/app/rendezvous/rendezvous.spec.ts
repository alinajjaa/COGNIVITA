import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Rendezvous } from './rendezvous';

describe('Rendezvous', () => {
  let component: Rendezvous;
  let fixture: ComponentFixture<Rendezvous>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Rendezvous]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Rendezvous);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
