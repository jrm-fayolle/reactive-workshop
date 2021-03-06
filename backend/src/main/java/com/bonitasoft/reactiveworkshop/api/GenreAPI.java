package com.bonitasoft.reactiveworkshop.api;

import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.bonitasoft.reactiveworkshop.Constants;
import com.bonitasoft.reactiveworkshop.domain.Artist;
import com.bonitasoft.reactiveworkshop.domain.Comment;
import com.bonitasoft.reactiveworkshop.domain.CommentFull;
import com.bonitasoft.reactiveworkshop.domain.GenreComment;
import com.bonitasoft.reactiveworkshop.infra.DataInitializer;
import com.bonitasoft.reactiveworkshop.repository.ArtistRepository;
import com.bonitasoft.reactiveworkshop.repository.CommentRepository;

import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import reactor.core.publisher.Flux;

@RestController
public class GenreAPI {

	private ArtistRepository artistRepository;
	private CommentRepository commentRepository;
	private WebClient webClient;
	private static Logger logger = LoggerFactory.getLogger(GenreAPI.class);

	public GenreAPI(ArtistRepository artistRepository, CommentRepository commentRepository, WebClient webClient) {
		this.artistRepository = artistRepository;
		this.commentRepository = commentRepository;
		this.webClient = webClient;
	}

	@GetMapping("/genres")
	public List<String> findAll() {
		return artistRepository.findAll().stream()
				.map(c -> c.getGenre())
				.filter(g -> !g.isEmpty())
				.distinct()
				.sorted()
				.collect(Collectors.toList());
	}

	Comparator<CommentFull> dateAsc = new Comparator<CommentFull>() {
		@Override
		public int compare(CommentFull i1, CommentFull i2) {
			return i1.getDate().compareTo(i2.getDate());
		}
	};

	@GetMapping("/genre/{genre}/comments")
	public Flux<GenreComment> getGenreComments(@PathVariable String genre) {
		return Flux.fromStream(commentRepository.findLastCommentsByGenre(genre).stream()
				.limit(10)
				.sorted(dateAsc)
				.map(c -> GenreComment.builder()
						.artistId(c.getArtistId())
						.artistName(c.getArtist().getName())
						.userName(c.getUserName())
						.comment(c.getComment())
						.build()));
	}

	private Flux<GenreComment> findNewestCommentsByGenre(String genre) {
		Flux<GenreComment> results = webClient.get()
				.uri(Constants.COMMENTS_STREAM)
				.retrieve()
				.bodyToFlux(Comment.class)
				.mapNotNull(comment -> commentToGenreCommentAndSave(comment, genre))
				.timeout(Duration.ofMillis(Constants.TIMEOUT_STREAM))
				.onErrorMap(ReadTimeoutException.class, ex -> new HttpTimeoutException("ReadTimeout"))
				.doOnError(WriteTimeoutException.class, ex -> logger.error("WriteTimeout"))
				.filter(p -> p != null);
		return results;

	}

	private GenreComment commentToGenreCommentAndSave(Comment comment, String genre) {
		Optional<Artist> artist = artistRepository.findById(comment.getArtist());
		if (artist.isPresent() && genre.equals(artist.get().getGenre())) {
			String md5 = DataInitializer.md5(comment.getArtist() + comment.getUserName() + comment.getComment());
			commentRepository.save(CommentFull.builder()
					.id(md5)
					.artistId(comment.getArtist())
					.userName(comment.getUserName())
					.comment(comment.getComment())
					.build());

			return GenreComment.toGenreComment(artist.get(), comment);
		}
		else {
			return null;
		}
	}

	@GetMapping(path = "/genre/{genre}/comments/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
	public Flux<GenreComment> getGenreCommentsStream(@PathVariable String genre) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		Publisher<GenreComment> last10 = getGenreComments(genre);
		Publisher<GenreComment> stream = findNewestCommentsByGenre(genre);
		return Flux.mergeSequential(last10, stream);
	}

}
