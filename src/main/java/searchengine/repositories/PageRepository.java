package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.models.Page;


public interface PageRepository extends JpaRepository<Page, Integer> {
}
