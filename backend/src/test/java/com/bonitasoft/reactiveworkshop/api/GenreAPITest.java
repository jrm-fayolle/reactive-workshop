package com.bonitasoft.reactiveworkshop.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bonitasoft.reactiveworkshop.domain.Artist;
import com.bonitasoft.reactiveworkshop.domain.CommentFull;
import com.bonitasoft.reactiveworkshop.domain.GenreComment;
import com.bonitasoft.reactiveworkshop.infra.DataInitializer;
import com.bonitasoft.reactiveworkshop.repository.ArtistRepository;
import com.bonitasoft.reactiveworkshop.repository.CommentRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class GenreAPITest {

	private static String GENRES = "/genres";

	@Autowired
	private GenreAPI service;

	@Autowired
	private ArtistRepository artistRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void contextLoads() throws Exception {
		assertThat(service).isNotNull();
		assertThat(artistRepository).isNotNull();
		assertThat(commentRepository).isNotNull();
		assertThat(mockMvc).isNotNull();
	}

	public String random() {
		byte[] array = new byte[7]; // length is bounded by 7
		new Random().nextBytes(array);
		return Base64.getEncoder().encodeToString(array).replace("/", "0");
	}

	@Test
	public void shouldReturnGenres() throws Exception {
		Long nbArtists = artistRepository.findAll().stream()
				.map(c -> c.getGenre())
				.filter(g -> !g.isEmpty())
				.distinct().count();
		this.mockMvc.perform(get(GENRES)
				.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(nbArtists.intValue())));
	}

}
