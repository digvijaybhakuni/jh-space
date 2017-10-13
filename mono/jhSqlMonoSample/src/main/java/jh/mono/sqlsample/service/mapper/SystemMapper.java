package jh.mono.sqlsample.service.mapper;

import jh.mono.sqlsample.domain.AppSystem;
import jh.mono.sqlsample.service.dto.SystemDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity AppSystem and its DTO SystemDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface SystemMapper extends EntityMapper <SystemDTO, AppSystem> {

    @Mapping(target = "sites", ignore = true)
    AppSystem toEntity(SystemDTO systemDTO);
    default AppSystem fromId(Long id) {
        if (id == null) {
            return null;
        }
        AppSystem appSystem = new AppSystem();
        appSystem.setId(id);
        return appSystem;
    }
}
