package com.bonitasoft.reactiveworkshop.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "comments")
public class CommentFull {
	@Id
	@Column(nullable = false, length = 32)
	private String id;
	@Builder.Default
	@Column(nullable = false)
	private Date date = new Date();
	@ManyToOne
	@JoinColumn(name = "artistId", nullable = false, insertable = false, updatable = false)
	private Artist artist;

	@Column(nullable = false, length = 32)
	private String artistId;

	@Column(nullable = false, length = 40)
	private String userName;
	@Column(nullable = false, length = 20000)
	private String comment;

}
