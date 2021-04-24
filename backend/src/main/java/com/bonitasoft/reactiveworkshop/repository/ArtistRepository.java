package com.bonitasoft.reactiveworkshop.repository;

import com.bonitasoft.reactiveworkshop.domain.Artist;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, String> {

	public List<Artist> findByGenre(@Param("genre") String genre);
}
