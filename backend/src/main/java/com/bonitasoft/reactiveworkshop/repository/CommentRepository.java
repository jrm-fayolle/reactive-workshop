package com.bonitasoft.reactiveworkshop.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bonitasoft.reactiveworkshop.domain.CommentFull;
import com.bonitasoft.reactiveworkshop.domain.GenreComment;

@Repository
public interface CommentRepository extends JpaRepository<CommentFull, String> {

	@Query("SELECT u FROM CommentFull u WHERE artist.genre = ?1 ORDER BY date desc")
	List<CommentFull> findLastCommentsByGenre(String genre);

	@Query("SELECT u FROM CommentFull u WHERE artist.genre = ?1 AND date > ?2 ORDER BY u.date")
	List<CommentFull> findCommentsByGenre(String genre, Date date);

}
