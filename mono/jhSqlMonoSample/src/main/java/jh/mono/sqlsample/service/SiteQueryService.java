package jh.mono.sqlsample.service;


import java.time.ZonedDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import jh.mono.sqlsample.domain.Site;
import jh.mono.sqlsample.domain.*; // for static metamodels
import jh.mono.sqlsample.repository.SiteRepository;
import jh.mono.sqlsample.service.dto.SiteCriteria;

import jh.mono.sqlsample.service.dto.SiteDTO;
import jh.mono.sqlsample.service.mapper.SiteMapper;

/**
 * Service for executing complex queries for Site entities in the database.
 * The main input is a {@link SiteCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {%link SiteDTO} or a {@link Page} of {%link SiteDTO} which fulfills the criterias
 */
@Service
@Transactional(readOnly = true)
public class SiteQueryService extends QueryService<Site> {

    private final Logger log = LoggerFactory.getLogger(SiteQueryService.class);


    private final SiteRepository siteRepository;

    private final SiteMapper siteMapper;
    public SiteQueryService(SiteRepository siteRepository, SiteMapper siteMapper) {
        this.siteRepository = siteRepository;
        this.siteMapper = siteMapper;
    }

    /**
     * Return a {@link List} of {%link SiteDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SiteDTO> findByCriteria(SiteCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<Site> specification = createSpecification(criteria);
        return siteMapper.toDto(siteRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {%link SiteDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SiteDTO> findByCriteria(SiteCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<Site> specification = createSpecification(criteria);
        final Page<Site> result = siteRepository.findAll(specification, page);
        return result.map(siteMapper::toDto);
    }

    /**
     * Function to convert SiteCriteria to a {@link Specifications}
     */
    private Specifications<Site> createSpecification(SiteCriteria criteria) {
        Specifications<Site> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Site_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Site_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Site_.description));
            }
            if (criteria.getCreationDateTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreationDateTime(), Site_.creationDateTime));
            }
            if (criteria.getSystemId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getSystemId(), Site_.system, System_.id));
            }
        }
        return specification;
    }

}
