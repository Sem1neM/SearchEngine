package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.models.Field;


public interface FieldRepository extends JpaRepository<Field, Integer> {
}
