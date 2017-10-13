package jh.mono.sqlsample.service.mapper;

import jh.mono.sqlsample.domain.*;
import jh.mono.sqlsample.service.dto.SiteDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Site and its DTO SiteDTO.
 */
@Mapper(componentModel = "spring", uses = {SystemMapper.class, })
public interface SiteMapper extends EntityMapper <SiteDTO, Site> {

    @Mapping(source = "system.id", target = "systemId")
    SiteDTO toDto(Site site); 

    @Mapping(source = "systemId", target = "system")
    Site toEntity(SiteDTO siteDTO); 
    default Site fromId(Long id) {
        if (id == null) {
            return null;
        }
        Site site = new Site();
        site.setId(id);
        return site;
    }
}
