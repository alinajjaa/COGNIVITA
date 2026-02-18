import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RendezvousMedecinComponent } from './rendezvous-medecin';

describe('RendezvousMedecinComponent', () => {
  let component: RendezvousMedecinComponent;
  let fixture: ComponentFixture<RendezvousMedecinComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RendezvousMedecinComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(RendezvousMedecinComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
