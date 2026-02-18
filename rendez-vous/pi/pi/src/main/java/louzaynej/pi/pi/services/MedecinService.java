package louzaynej.pi.pi.services;


import louzaynej.pi.pi.dto.MedecinOptionDTO;
import louzaynej.pi.pi.model.Medecin;
import louzaynej.pi.pi.repositories.MedecinRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedecinService {

    private final MedecinRepository medecinRepository;

    public MedecinService(MedecinRepository medecinRepository) {
        this.medecinRepository = medecinRepository;
    }

    public Medecin createMedecin(Medecin medecin) {

        if (medecinRepository.existsByEmail(medecin.getEmail())) {
            throw new RuntimeException("Un médecin avec cet email existe déjà !");
        }

        return medecinRepository.save(medecin);
    }

    public List<Medecin> getAllMedecins() {
        return medecinRepository.findAll();
    }

    public Medecin getMedecinById(Long id) {
        return medecinRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé avec id : " + id));
    }

    public Medecin updateMedecin(Long id, Medecin updatedMedecin) {

        Medecin existing = getMedecinById(id);

        existing.setNomMedecin(updatedMedecin.getNomMedecin());
        existing.setSpecialite(updatedMedecin.getSpecialite());
        existing.setEmail(updatedMedecin.getEmail());
        existing.setTelephone(updatedMedecin.getTelephone());

        return medecinRepository.save(existing);
    }

    public void deleteMedecin(Long id) {

        Medecin medecin = getMedecinById(id);
        medecinRepository.delete(medecin);
    }
    public List<MedecinOptionDTO> getMedecinOptions() {
        return medecinRepository.findAll()
                .stream()
                .map(m -> new MedecinOptionDTO(m.getId(), m.getNomMedecin()))
                .collect(Collectors.toList());
    }
}

