package louzaynej.pi.pi.services;

import louzaynej.pi.pi.model.Patient;
import louzaynej.pi.pi.repositories.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
    }
    public Patient updatePatient(Long id, Patient updatedPatient) {

        Patient existingPatient = getPatientById(id);

        existingPatient.setNomPatient(updatedPatient.getNomPatient());
        existingPatient.setEmail(updatedPatient.getEmail());
        existingPatient.setTelephone(updatedPatient.getTelephone());

        return patientRepository.save(existingPatient);
    }

    public void deletePatient(Long id) {

        Patient patient = getPatientById(id);
        patientRepository.delete(patient);
    }
}

