package com.bonitasoft.reactiveworkshop.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "artists")
public class Artist {

	@Id
	@Column(nullable = false, length = 32)
	private String id;
	@Column(nullable = false, length = 200)
	private String name;
	@Column(length = 100)
	private String genre;

}
