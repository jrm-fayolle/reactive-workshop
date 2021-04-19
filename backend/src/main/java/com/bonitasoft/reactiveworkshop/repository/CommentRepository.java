package com.bonitasoft.reactiveworkshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bonitasoft.reactiveworkshop.domain.CommentFull;

@Repository
public interface CommentRepository extends JpaRepository<CommentFull, String> {

	// TODO: check query
	@Query("SELECT u FROM CommentFull u WHERE artist.genre = ?1 ORDER BY date desc")
	List<CommentFull> findLastCommentsByGenre(String genre);

}
