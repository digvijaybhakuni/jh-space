package jh.mono.sqlsample.service;

import jh.mono.sqlsample.domain.AppSystem;
import jh.mono.sqlsample.repository.SystemRepository;
import jh.mono.sqlsample.service.dto.SystemDTO;
import jh.mono.sqlsample.service.mapper.SystemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing AppSystem.
 */
@Service
@Transactional
public class SystemService {

    private final Logger log = LoggerFactory.getLogger(SystemService.class);

    private final SystemRepository systemRepository;

    private final SystemMapper systemMapper;
    public SystemService(SystemRepository systemRepository, SystemMapper systemMapper) {
        this.systemRepository = systemRepository;
        this.systemMapper = systemMapper;
    }

    /**
     * Save a system.
     *
     * @param systemDTO the entity to save
     * @return the persisted entity
     */
    public SystemDTO save(SystemDTO systemDTO) {
        log.debug("Request to save AppSystem : {}", systemDTO);
        AppSystem appSystem = systemMapper.toEntity(systemDTO);
        appSystem = systemRepository.save(appSystem);
        return systemMapper.toDto(appSystem);
    }

    /**
     *  Get all the systems.
     *
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<SystemDTO> findAll() {
        log.debug("Request to get all Systems");
        return systemRepository.findAll().stream()
            .map(systemMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get one system by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public SystemDTO findOne(Long id) {
        log.debug("Request to get AppSystem : {}", id);
        AppSystem appSystem = systemRepository.findOne(id);
        return systemMapper.toDto(appSystem);
    }

    /**
     *  Delete the  system by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete AppSystem : {}", id);
        systemRepository.delete(id);
    }
}
