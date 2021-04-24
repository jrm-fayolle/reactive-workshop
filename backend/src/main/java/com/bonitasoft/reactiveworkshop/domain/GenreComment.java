package com.bonitasoft.reactiveworkshop.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenreComment {
	private String artistId;
	private String artistName;
	private String userName;
	private String comment;
	
	public static GenreComment toGenreComment(Artist artist, Comment comment) {
		if (artist.getId().equals(comment.getArtist()))
			return GenreComment.builder()
				.artistId(comment.getArtist())
				.artistName(artist.getName())
				.userName(comment.getUserName())
				.comment(comment.getComment())
				.build();
		else {
			throw new IllegalArgumentException("artist & comment doesn't match");
		}
	}
}
