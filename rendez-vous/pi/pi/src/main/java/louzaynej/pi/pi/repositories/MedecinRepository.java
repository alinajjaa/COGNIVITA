package louzaynej.pi.pi.repositories;

import louzaynej.pi.pi.model.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedecinRepository extends JpaRepository<Medecin, Long> {
    boolean existsByEmail(String email);

}
