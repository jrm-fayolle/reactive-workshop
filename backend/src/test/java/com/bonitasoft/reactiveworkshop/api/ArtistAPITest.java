package com.bonitasoft.reactiveworkshop.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bonitasoft.reactiveworkshop.domain.Artist;
import com.bonitasoft.reactiveworkshop.repository.ArtistRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class ArtistAPITest {

	private static final String ARTIST_ID_COMMENTS = "/artist/{id}/comments";

	@Autowired
	private ArtistAPI service;

	@Autowired
	private ArtistRepository artistRepository;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void contextLoads() throws Exception {
		assertThat(service).isNotNull();
		assertThat(artistRepository).isNotNull();
		assertThat(mockMvc).isNotNull();
	}

	public String random() {
		byte[] array = new byte[7]; // length is bounded by 7
		new Random().nextBytes(array);
		return Base64.getEncoder().encodeToString(array).replace("/", "0");
	}

	@Test
	public void givenArtist_whenFindAll_shouldReturnArtists() throws Exception {
		Artist artist = Artist.builder().id(random()).name("Artist").genre("genre").build();
		artistRepository.save(artist);

		List<Artist> allArtits = artistRepository.findAll();

		this.mockMvc.perform(get("/artists")
				.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(allArtits.size())));
	}

	@Test
	public void givenArtist_whenGetByIdComment_thenReturnJsonArray() throws Exception {
		Artist artist = Artist.builder().id(random()).name("Artist" + random()).genre("genre" + random()).build();
		this.mockMvc.perform(get(ARTIST_ID_COMMENTS, artist.getId())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		artistRepository.save(artist);
		this.mockMvc.perform(get(ARTIST_ID_COMMENTS, artist.getId())
				.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.artistName", is(artist.getName())))
				.andExpect(jsonPath("$.comments", hasSize(10)));

	}

}
