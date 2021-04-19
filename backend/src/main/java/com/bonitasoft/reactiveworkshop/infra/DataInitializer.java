package com.bonitasoft.reactiveworkshop.infra;

import static org.springframework.util.DigestUtils.md5DigestAsHex;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bonitasoft.reactiveworkshop.Constants;
import com.bonitasoft.reactiveworkshop.domain.Artist;
import com.bonitasoft.reactiveworkshop.domain.Comment;
import com.bonitasoft.reactiveworkshop.domain.CommentFull;
import com.bonitasoft.reactiveworkshop.repository.ArtistRepository;
import com.bonitasoft.reactiveworkshop.repository.CommentRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
@Slf4j
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

	private final ArtistRepository artistRepository;
	private final CommentRepository commentRepository;

	public DataInitializer(ArtistRepository artistRepository, CommentRepository commentRepository) {
		this.artistRepository = artistRepository;
		this.commentRepository = commentRepository;
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		if (artistRepository.count() == 0) {
			artistRepository.deleteAll();

			List<Artist> allArtists = new ArrayList<>();
			try (CSVReader reader = new CSVReader(new InputStreamReader(DataInitializer.class.getResourceAsStream("/artists_genre.csv")))) {
				String[] line;
				while ((line = reader.readNext()) != null) {
					// name,facebook,twitter,website,genre,mtv
					String name = line[0].trim();
					Artist artist = Artist.builder()
							.id(md5(name))
							.name(name)
							.genre(line[4].trim()).build();
					allArtists.add(artist);

				}
			}
			catch (IOException | CsvValidationException e) {
				throw new IllegalStateException(e);
			}
			Set<String> artistIds = new HashSet<>(3000);

			allArtists.stream()
					.filter(a -> artistIds.add(a.getId()))
					.forEach(artistRepository::save);
		}

		WebClient webClient = WebClient.create(Constants.COMMENT_URI);
		Flux<Comment> results = webClient.get().uri(Constants.COMMENTS_STREAM).retrieve().bodyToFlux(Comment.class);
		results.subscribe(result -> {
			String md5 = md5(result.getArtist() + result.getUserName() + result.getComment());
			commentRepository.save(CommentFull.builder()
					.id(md5)
					.artistId(result.getArtist())
					.userName(result.getUserName())
					.comment(result.getComment())
					.build());
		});
	}

	private String md5(String name) {
		try {
			return md5DigestAsHex(name.getBytes("UTF-8"));
		}
		catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Unable to parse artists file");
		}
	}
}
