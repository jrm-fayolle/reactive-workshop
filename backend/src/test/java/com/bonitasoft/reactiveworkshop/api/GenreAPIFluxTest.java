package com.bonitasoft.reactiveworkshop.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.bonitasoft.reactiveworkshop.domain.Artist;
import com.bonitasoft.reactiveworkshop.domain.CommentFull;
import com.bonitasoft.reactiveworkshop.domain.GenreComment;
import com.bonitasoft.reactiveworkshop.infra.DataInitializer;
import com.bonitasoft.reactiveworkshop.repository.ArtistRepository;
import com.bonitasoft.reactiveworkshop.repository.CommentRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "10000")
@TestMethodOrder(OrderAnnotation.class)
public class GenreAPIFluxTest {

	private static final String GENRE_COUNTRY = "Country";
	private static final int NUMBER_COMMENTS = 15;
	private static String GENRE_COMMENTS = "/genre/{genre}/comments";
	private static String GENRE_COMMENTS_STREAM = GENRE_COMMENTS + "/stream";

	private static Logger logger = LoggerFactory.getLogger(GenreAPIFluxTest.class);

	@Autowired
	private GenreAPI service;

	@Autowired
	private ArtistRepository artistRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private WebTestClient webClient;

	private static final Artist dummyArtist = Artist.builder().id(random()).genre("genreFlux" + random()).name("random").build();

	@Test
	@Order(1)
	public void contextLoads() throws Exception {
		assertThat(service).isNotNull();
		assertThat(artistRepository).isNotNull();
		assertThat(commentRepository).isNotNull();
		assertThat(webClient).isNotNull();
	}

	public static String random() {
		byte[] array = new byte[7]; // length is bounded by 7
		new Random().nextBytes(array);
		return Base64.getEncoder().encodeToString(array).replace("/", "0");
	}

	@Test
	@Order(2)
	public void initObjects() {
		logger.info("initialising dumyArtist");
		artistRepository.save(dummyArtist);
		assertThat(artistRepository.findById(dummyArtist.getId())).isNotEmpty();
		ArrayList<GenreComment> arrayComments = new ArrayList<GenreComment>();
		for (int i = 0; i < NUMBER_COMMENTS; i++) {
			CommentFull commentFull = CommentFull.builder()
					.id(DataInitializer.md5(random()))
					.artistId(dummyArtist.getId())
					.comment(random())
					.userName(random()).build();
			arrayComments.add(commentFull.toGenreComment(dummyArtist));
			commentRepository.save(commentFull);
		}
		assertThat(commentRepository.findLastCommentsByGenre(dummyArtist.getGenre()).size() == NUMBER_COMMENTS).isTrue();
	}

	@Test
	@Order(3)
	public void givenDummyGenre_1_whenGetGenreComments_shouldReturnComments() throws Exception {
		logger.info("calling uri " + GENRE_COMMENTS + " with " + dummyArtist.getGenre());
		this.webClient.get().uri(GENRE_COMMENTS, dummyArtist.getGenre())
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(GenreComment.class)
				.hasSize(10)
				.consumeWith(allComments -> assertThat(allComments.getResponseBody())
						.allSatisfy(comment -> assertThat(comment.getArtistId().equals(dummyArtist.getId())).isTrue()));
	}

	@Test
	@Order(4)
	public void givenDummyGenre_2_whenGetGenreCommentsStream_shouldReturnComments() throws Exception {
		logger.info("calling uri " + GENRE_COMMENTS_STREAM + " with " + dummyArtist.getGenre());
		List<GenreComment> result = this.webClient.get().uri(GENRE_COMMENTS_STREAM, dummyArtist.getGenre())
				.accept(MediaType.APPLICATION_NDJSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_NDJSON)
				.returnResult(GenreComment.class)
				.getResponseBody()
				.take(9)
				.collectList()
				.block();
	}

	@Test
	@Order(5)
	public void givenCountryGenre_whenGetGenreCommentsStream_shouldReturnComments() throws Exception {
		logger.info("calling uri " + GENRE_COMMENTS_STREAM + " with " + GENRE_COUNTRY);
		List<Artist> countryArtists = artistRepository.findByGenre(GENRE_COUNTRY);
		for (int i = 0; i < NUMBER_COMMENTS; i++) {
			CommentFull commentFull = CommentFull.builder()
					.id(DataInitializer.md5(random()))
					.artistId(countryArtists.get(i).getId())
					.comment(random())
					.userName(random()).build();
			commentRepository.save(commentFull);
		}
		List<GenreComment> result = this.webClient.get().uri(GENRE_COMMENTS_STREAM, GENRE_COUNTRY)
				.accept(MediaType.APPLICATION_NDJSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_NDJSON)
				.returnResult(GenreComment.class)
				.getResponseBody()
				.take(15)
				.collectList()
				.block();
	}

}
