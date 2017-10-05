package jh.cms.srv.repository;

import jh.cms.srv.domain.Post;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Post entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PostRepository extends MongoRepository<Post, String> {

}
