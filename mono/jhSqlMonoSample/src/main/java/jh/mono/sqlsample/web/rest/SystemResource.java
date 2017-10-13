package jh.mono.sqlsample.web.rest;

import com.codahale.metrics.annotation.Timed;
import jh.mono.sqlsample.service.SystemService;
import jh.mono.sqlsample.web.rest.util.HeaderUtil;
import jh.mono.sqlsample.service.dto.SystemDTO;
import jh.mono.sqlsample.service.dto.SystemCriteria;
import jh.mono.sqlsample.service.SystemQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing AppSystem.
 */
@RestController
@RequestMapping("/api")
public class SystemResource {

    private final Logger log = LoggerFactory.getLogger(SystemResource.class);

    private static final String ENTITY_NAME = "system";

    private final SystemService systemService;
    private final SystemQueryService systemQueryService;

    public SystemResource(SystemService systemService, SystemQueryService systemQueryService) {
        this.systemService = systemService;
        this.systemQueryService = systemQueryService;
    }

    /**
     * POST  /systems : Create a new system.
     *
     * @param systemDTO the systemDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new systemDTO, or with status 400 (Bad Request) if the system has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/systems")
    @Timed
    public ResponseEntity<SystemDTO> createSystem(@Valid @RequestBody SystemDTO systemDTO) throws URISyntaxException {
        log.debug("REST request to save AppSystem : {}", systemDTO);
        if (systemDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new system cannot already have an ID")).body(null);
        }
        SystemDTO result = systemService.save(systemDTO);
        return ResponseEntity.created(new URI("/api/systems/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /systems : Updates an existing system.
     *
     * @param systemDTO the systemDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated systemDTO,
     * or with status 400 (Bad Request) if the systemDTO is not valid,
     * or with status 500 (Internal Server Error) if the systemDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/systems")
    @Timed
    public ResponseEntity<SystemDTO> updateSystem(@Valid @RequestBody SystemDTO systemDTO) throws URISyntaxException {
        log.debug("REST request to update AppSystem : {}", systemDTO);
        if (systemDTO.getId() == null) {
            return createSystem(systemDTO);
        }
        SystemDTO result = systemService.save(systemDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, systemDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /systems : get all the systems.
     *
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of systems in body
     */
    @GetMapping("/systems")
    @Timed
    public ResponseEntity<List<SystemDTO>> getAllSystems(SystemCriteria criteria) {
        log.debug("REST request to get Systems by criteria: {}", criteria);
        List<SystemDTO> entityList = systemQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * GET  /systems/:id : get the "id" system.
     *
     * @param id the id of the systemDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the systemDTO, or with status 404 (Not Found)
     */
    @GetMapping("/systems/{id}")
    @Timed
    public ResponseEntity<SystemDTO> getSystem(@PathVariable Long id) {
        log.debug("REST request to get AppSystem : {}", id);
        SystemDTO systemDTO = systemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(systemDTO));
    }

    /**
     * DELETE  /systems/:id : delete the "id" system.
     *
     * @param id the id of the systemDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/systems/{id}")
    @Timed
    public ResponseEntity<Void> deleteSystem(@PathVariable Long id) {
        log.debug("REST request to delete AppSystem : {}", id);
        systemService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
