package louzaynej.pi.pi.controllers;


import louzaynej.pi.pi.model.RendezVous;
import louzaynej.pi.pi.services.RendezVousService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rendezvous")
@CrossOrigin
public class RendezVousController {

    private final RendezVousService rendezVousService;

    public RendezVousController(RendezVousService rendezVousService) {
        this.rendezVousService = rendezVousService;
    }

    @PostMapping
    public RendezVous create(@RequestBody RendezVous rendezVous) {
        return rendezVousService.createRendezVous(rendezVous);
    }

    @GetMapping
    public List<RendezVous> getAll() {
        return rendezVousService.getAllRendezVous();
    }

    @GetMapping("/{id}")
    public RendezVous getById(@PathVariable Long id) {
        return rendezVousService.getRendezVousById(id);
    }

    @PutMapping("/{id}")
    public RendezVous update(@PathVariable Long id, @RequestBody RendezVous rendezVous) {
        return rendezVousService.updateRendezVous(id, rendezVous);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        rendezVousService.deleteRendezVous(id);
    }
    @GetMapping("/medecin/{medecinId}")
    public List<RendezVous> getByMedecin(@PathVariable Long medecinId) {
        return rendezVousService.getRendezVousByMedecin(medecinId);
    }

    @GetMapping("/patient/{patientId}")
    public List<RendezVous> getByPatient(@PathVariable Long patientId) {
        return rendezVousService.getRendezVousByPatient(patientId);
    }

}
