package jh.mono.sqlsample.repository;

import jh.mono.sqlsample.domain.Site;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Site entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SiteRepository extends JpaRepository<Site, Long>, JpaSpecificationExecutor<Site> {

}
