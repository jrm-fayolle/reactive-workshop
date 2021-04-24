package com.bonitasoft.reactiveworkshop.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "comments")
public class CommentFull {
	@Id
	@Column(nullable = false, length = 32)
	private String id;

	@Builder.Default
	@Column(nullable = false)
	private Date date = new Date();

	@ManyToOne
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "artistId", nullable = false, insertable = false, updatable = false)
	private Artist artist;

	@Column(nullable = false, length = 32)
	private String artistId;

	@Column(nullable = false, length = 40)
	private String userName;

	@Column(nullable = false, length = 20000)
	private String comment;

	public Comment toComment() {
		return Comment.builder()
				.artist(getArtistId())
				.userName(getUserName())
				.comment(getComment())
				.build();
	}

	public GenreComment toGenreComment(Artist artist) {

		if (artist.getId().equals(getArtistId())) {
			return GenreComment.builder()
					.artistId(getArtistId())
					.artistName(artist.getName())
					.userName(getUserName())
					.comment(getComment())
					.build();
		}
		else {
			throw new IllegalArgumentException("artist & comment doesn't match");
		}

	}
}
