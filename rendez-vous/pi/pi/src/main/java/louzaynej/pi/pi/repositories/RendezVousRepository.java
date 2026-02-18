package louzaynej.pi.pi.repositories;

import louzaynej.pi.pi.model.RendezVous;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RendezVousRepository extends JpaRepository <RendezVous,Long> {
    List<RendezVous> findByMedecinId(Long medecinId);

    List<RendezVous> findByPatientId(Long patientId);
}
