package tn.esprit.cognivita.repository;

import tn.esprit.cognivita.entity.CognitiveActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CognitiveActivityRepository extends JpaRepository<CognitiveActivity, Long> {

    List<CognitiveActivity> findByType(String type);

    List<CognitiveActivity> findByDifficulty(String difficulty);

    List<CognitiveActivity> findByIsActiveTrue();

    @Query("SELECT a FROM CognitiveActivity a WHERE " +
            "LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<CognitiveActivity> searchByTitleOrDescription(@Param("keyword") String keyword);

    @Query("SELECT a FROM CognitiveActivity a WHERE " +
            "(:type IS NULL OR a.type = :type) AND " +
            "(:difficulty IS NULL OR a.difficulty = :difficulty) AND " +
            "a.isActive = true")
    List<CognitiveActivity> filterActivities(@Param("type") String type,
                                             @Param("difficulty") String difficulty);
}