package jh.mono.sqlsample.service;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import jh.mono.sqlsample.domain.AppSystem;
import jh.mono.sqlsample.domain.*; // for static metamodels
import jh.mono.sqlsample.repository.SystemRepository;
import jh.mono.sqlsample.service.dto.SystemCriteria;

import jh.mono.sqlsample.service.dto.SystemDTO;
import jh.mono.sqlsample.service.mapper.SystemMapper;

/**
 * Service for executing complex queries for AppSystem entities in the database.
 * The main input is a {@link SystemCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {%link SystemDTO} or a {@link Page} of {%link SystemDTO} which fulfills the criterias
 */
@Service
@Transactional(readOnly = true)
public class SystemQueryService extends QueryService<AppSystem> {

    private final Logger log = LoggerFactory.getLogger(SystemQueryService.class);


    private final SystemRepository systemRepository;

    private final SystemMapper systemMapper;
    public SystemQueryService(SystemRepository systemRepository, SystemMapper systemMapper) {
        this.systemRepository = systemRepository;
        this.systemMapper = systemMapper;
    }

    /**
     * Return a {@link List} of {%link SystemDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SystemDTO> findByCriteria(SystemCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<AppSystem> specification = createSpecification(criteria);
        return systemMapper.toDto(systemRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {%link SystemDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SystemDTO> findByCriteria(SystemCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<AppSystem> specification = createSpecification(criteria);
        final Page<AppSystem> result = systemRepository.findAll(specification, page);
        return result.map(systemMapper::toDto);
    }

    /**
     * Function to convert SystemCriteria to a {@link Specifications}
     */
    private Specifications<AppSystem> createSpecification(SystemCriteria criteria) {
        Specifications<AppSystem> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), System_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), System_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), System_.description));
            }
        }
        return specification;
    }

}
