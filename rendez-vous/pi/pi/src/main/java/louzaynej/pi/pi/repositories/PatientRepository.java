package louzaynej.pi.pi.repositories;

import louzaynej.pi.pi.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
