package com.bonitasoft.reactiveworkshop.api;

import java.util.List;

import com.bonitasoft.reactiveworkshop.Constants;
import com.bonitasoft.reactiveworkshop.domain.Artist;
import com.bonitasoft.reactiveworkshop.domain.ArtistsComment;
import com.bonitasoft.reactiveworkshop.domain.Comment;
import com.bonitasoft.reactiveworkshop.exception.NotFoundException;
import com.bonitasoft.reactiveworkshop.repository.ArtistRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ArtistAPI {

	private ArtistRepository artistRepository;
	private RestTemplate restTemplate;

	public ArtistAPI(ArtistRepository artistRepository, RestTemplate restTemplate) {
		this.artistRepository = artistRepository;
		this.restTemplate = restTemplate;
	}

	@GetMapping("/artist/{id}")
	public Artist findById(@PathVariable String id) throws NotFoundException {
		return artistRepository.findById(id).orElseThrow(NotFoundException::new);
	}

	@GetMapping("/artist/{id}/comments")
	public ArtistsComment findCommentsById(@PathVariable String id) throws NotFoundException {
		Artist artist = artistRepository.findById(id).orElseThrow(NotFoundException::new);
		Comment[] comments = restTemplate.getForObject(Constants.COMMENT_URI + Constants.COMMENTS_ARTIST_LAST10, Comment[].class, id);
		for (int i = 0; i < comments.length; i++) {
			comments[i].setArtist(null);
		}
		return ArtistsComment.builder().artistId(artist.getId()).artistName(artist.getName()).genre(artist.getGenre()).comments(comments).build();
	}

	@GetMapping("/artists")
	public List<Artist> findAll() throws NotFoundException {
		return artistRepository.findAll();
	}

}
