package jh.cms.srv.service.mapper;

import jh.cms.srv.domain.*;
import jh.cms.srv.service.dto.PostDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Post and its DTO PostDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PostMapper extends EntityMapper <PostDTO, Post> {
    
    

}
