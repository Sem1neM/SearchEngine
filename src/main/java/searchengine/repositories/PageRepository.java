package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.models.Page;

public interface PageRepository extends JpaRepository<Page, Integer> {
}
