package com.bonitasoft.reactiveworkshop.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArtistsComment {

	private String artistId;
	private String artistName;
	private String genre;
	private Comment[] comments;

}
