package jh.mono.sqlsample.service;

import jh.mono.sqlsample.domain.Site;
import jh.mono.sqlsample.repository.SiteRepository;
import jh.mono.sqlsample.service.dto.SiteDTO;
import jh.mono.sqlsample.service.mapper.SiteMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Site.
 */
@Service
@Transactional
public class SiteService {

    private final Logger log = LoggerFactory.getLogger(SiteService.class);

    private final SiteRepository siteRepository;

    private final SiteMapper siteMapper;
    public SiteService(SiteRepository siteRepository, SiteMapper siteMapper) {
        this.siteRepository = siteRepository;
        this.siteMapper = siteMapper;
    }

    /**
     * Save a site.
     *
     * @param siteDTO the entity to save
     * @return the persisted entity
     */
    public SiteDTO save(SiteDTO siteDTO) {
        log.debug("Request to save Site : {}", siteDTO);
        Site site = siteMapper.toEntity(siteDTO);
        site = siteRepository.save(site);
        return siteMapper.toDto(site);
    }

    /**
     *  Get all the sites.
     *
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<SiteDTO> findAll() {
        log.debug("Request to get all Sites");
        return siteRepository.findAll().stream()
            .map(siteMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get one site by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public SiteDTO findOne(Long id) {
        log.debug("Request to get Site : {}", id);
        Site site = siteRepository.findOne(id);
        return siteMapper.toDto(site);
    }

    /**
     *  Delete the  site by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Site : {}", id);
        siteRepository.delete(id);
    }
}
