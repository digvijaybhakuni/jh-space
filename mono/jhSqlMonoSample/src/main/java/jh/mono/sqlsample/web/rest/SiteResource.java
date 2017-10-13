package jh.mono.sqlsample.web.rest;

import com.codahale.metrics.annotation.Timed;
import jh.mono.sqlsample.service.SiteService;
import jh.mono.sqlsample.web.rest.util.HeaderUtil;
import jh.mono.sqlsample.service.dto.SiteDTO;
import jh.mono.sqlsample.service.dto.SiteCriteria;
import jh.mono.sqlsample.service.SiteQueryService;
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
 * REST controller for managing Site.
 */
@RestController
@RequestMapping("/api")
public class SiteResource {

    private final Logger log = LoggerFactory.getLogger(SiteResource.class);

    private static final String ENTITY_NAME = "site";

    private final SiteService siteService;
    private final SiteQueryService siteQueryService;

    public SiteResource(SiteService siteService, SiteQueryService siteQueryService) {
        this.siteService = siteService;
        this.siteQueryService = siteQueryService;
    }

    /**
     * POST  /sites : Create a new site.
     *
     * @param siteDTO the siteDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new siteDTO, or with status 400 (Bad Request) if the site has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/sites")
    @Timed
    public ResponseEntity<SiteDTO> createSite(@Valid @RequestBody SiteDTO siteDTO) throws URISyntaxException {
        log.debug("REST request to save Site : {}", siteDTO);
        if (siteDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new site cannot already have an ID")).body(null);
        }
        SiteDTO result = siteService.save(siteDTO);
        return ResponseEntity.created(new URI("/api/sites/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /sites : Updates an existing site.
     *
     * @param siteDTO the siteDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated siteDTO,
     * or with status 400 (Bad Request) if the siteDTO is not valid,
     * or with status 500 (Internal Server Error) if the siteDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/sites")
    @Timed
    public ResponseEntity<SiteDTO> updateSite(@Valid @RequestBody SiteDTO siteDTO) throws URISyntaxException {
        log.debug("REST request to update Site : {}", siteDTO);
        if (siteDTO.getId() == null) {
            return createSite(siteDTO);
        }
        SiteDTO result = siteService.save(siteDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, siteDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /sites : get all the sites.
     *
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of sites in body
     */
    @GetMapping("/sites")
    @Timed
    public ResponseEntity<List<SiteDTO>> getAllSites(SiteCriteria criteria) {
        log.debug("REST request to get Sites by criteria: {}", criteria);
        List<SiteDTO> entityList = siteQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * GET  /sites/:id : get the "id" site.
     *
     * @param id the id of the siteDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the siteDTO, or with status 404 (Not Found)
     */
    @GetMapping("/sites/{id}")
    @Timed
    public ResponseEntity<SiteDTO> getSite(@PathVariable Long id) {
        log.debug("REST request to get Site : {}", id);
        SiteDTO siteDTO = siteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(siteDTO));
    }

    /**
     * DELETE  /sites/:id : delete the "id" site.
     *
     * @param id the id of the siteDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/sites/{id}")
    @Timed
    public ResponseEntity<Void> deleteSite(@PathVariable Long id) {
        log.debug("REST request to delete Site : {}", id);
        siteService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
