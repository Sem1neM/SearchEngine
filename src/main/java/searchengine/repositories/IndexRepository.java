package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.models.Index;

@Repository
public interface IndexRepository extends JpaRepository<Index, Integer> {
    Index findByLemmaId(int id);
}
