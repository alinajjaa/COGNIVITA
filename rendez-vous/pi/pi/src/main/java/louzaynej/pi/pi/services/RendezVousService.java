package louzaynej.pi.pi.services;

import louzaynej.pi.pi.model.RendezVous;
import louzaynej.pi.pi.repositories.RendezVousRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RendezVousService {

    private final RendezVousRepository rendezVousRepository;

    public RendezVousService(RendezVousRepository rendezVousRepository) {
        this.rendezVousRepository = rendezVousRepository;
    }
    public RendezVous createRendezVous(RendezVous rendezVous) {
        return rendezVousRepository.save(rendezVous);
    }
    public List<RendezVous> getAllRendezVous() {
        return rendezVousRepository.findAll();
    }
    public RendezVous getRendezVousById(Long id) {
        return rendezVousRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous not found with id: " + id));
    }
    public RendezVous updateRendezVous(Long id, RendezVous updatedRendezVous) {

        RendezVous existing = getRendezVousById(id);

        if (updatedRendezVous.getDateHeure() != null) {
            existing.setDateHeure(updatedRendezVous.getDateHeure());
        }

        if (updatedRendezVous.getStatus() != null) {
            existing.setStatus(updatedRendezVous.getStatus());
        }

        if (updatedRendezVous.getMedecin() != null) {
            existing.setMedecin(updatedRendezVous.getMedecin());
        }

        if (updatedRendezVous.getPatient() != null) {
            existing.setPatient(updatedRendezVous.getPatient());
        }

        return rendezVousRepository.save(existing);
    }

    public void deleteRendezVous(Long id) {

        RendezVous rendezVous = getRendezVousById(id);
        rendezVousRepository.delete(rendezVous);
    }
    public List<RendezVous> getRendezVousByMedecin(Long medecinId) {
        return rendezVousRepository.findByMedecinId(medecinId);
    }

    public List<RendezVous> getRendezVousByPatient(Long patientId) {
        return rendezVousRepository.findByPatientId(patientId);
    }

}

