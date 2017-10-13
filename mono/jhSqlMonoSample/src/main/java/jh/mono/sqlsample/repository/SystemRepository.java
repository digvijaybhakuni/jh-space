package jh.mono.sqlsample.repository;

import jh.mono.sqlsample.domain.AppSystem;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the AppSystem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SystemRepository extends JpaRepository<AppSystem, Long>, JpaSpecificationExecutor<AppSystem> {

}
