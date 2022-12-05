package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.models.Site;

@Repository
public interface SiteRepository extends JpaRepository<Site,Integer> {
}
