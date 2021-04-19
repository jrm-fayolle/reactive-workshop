package com.bonitasoft.reactiveworkshop.api;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.bonitasoft.reactiveworkshop.domain.Artist;
import com.bonitasoft.reactiveworkshop.domain.GenreComment;
import com.bonitasoft.reactiveworkshop.repository.ArtistRepository;
import com.bonitasoft.reactiveworkshop.repository.CommentRepository;

import reactor.core.publisher.Flux;

@RestController
public class GenreApi {

	private ArtistRepository artistRepository;
	private CommentRepository commentRepository;
	private RestTemplate restTemplate;

	public GenreApi(ArtistRepository artistRepository, CommentRepository commentRepository, RestTemplate restTemplate) {
		this.artistRepository = artistRepository;
		this.commentRepository = commentRepository;
		this.restTemplate = restTemplate;
	}

	@GetMapping("/genres")
	public List<String> findAll() {
		return artistRepository.findAll().stream()
				.map(Artist::getGenre)
				.filter(g -> !g.isEmpty())
				.distinct()
				.sorted()
				.collect(Collectors.toList());
	}

	@GetMapping("/genre/{genre}/comments")
	public List<GenreComment> getGenreComments(@PathVariable String genre) {
		return commentRepository.findLastCommentsByGenre(genre).stream()
				.limit(10)
				.map(c -> GenreComment.builder()
						.artistId(c.getArtistId())
						.artistName(c.getArtist().getName())
						.userName(c.getUserName())
						.comment(c.getComment())
						.build())
				.collect(Collectors.toList());
	}

	@GetMapping(path = "/genre/{genre}/comments/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
	public Flux<GenreComment> getGenreCommentsStream(@PathVariable String genre) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		return Flux.fromIterable(commentRepository.findCommentsByGenre(genre, cal.getTime()).stream()
				.map(c -> GenreComment.builder()
						.artistId(c.getArtistId())
						.artistName(c.getArtist().getName())
						.userName(c.getUserName())
						.comment(c.getComment())
						.build())
				.collect(Collectors.toList()));
	}
}
