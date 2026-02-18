package louzaynej.pi.pi.controllers;


import louzaynej.pi.pi.model.Medecin;
import louzaynej.pi.pi.services.MedecinService;
import org.springframework.web.bind.annotation.*;
import louzaynej.pi.pi.dto.MedecinOptionDTO;


import java.util.List;

@RestController
@RequestMapping("/api/medecins")
@CrossOrigin
public class MedecinController {

    private final MedecinService medecinService;

    public MedecinController(MedecinService medecinService) {
        this.medecinService = medecinService;
    }

    @PostMapping
    public Medecin create(@RequestBody Medecin medecin) {
        return medecinService.createMedecin(medecin);
    }

    @GetMapping
    public List<Medecin> getAll() {
        return medecinService.getAllMedecins();
    }

    @GetMapping("/{id}")
    public Medecin getById(@PathVariable Long id) {
        return medecinService.getMedecinById(id);
    }

    @PutMapping("/{id}")
    public Medecin update(@PathVariable Long id, @RequestBody Medecin medecin) {
        return medecinService.updateMedecin(id, medecin);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        medecinService.deleteMedecin(id);
    }

    @GetMapping("/options")
    public List<MedecinOptionDTO> getOptions() {
        return medecinService.getMedecinOptions();
    }
}
