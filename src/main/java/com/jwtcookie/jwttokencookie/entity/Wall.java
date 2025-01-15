package com.jwtcookie.jwttokencookie.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Wall implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String wallName;
	private String description;
	private String coverBackgroundUrl;
	
	@OneToOne
	private Profile profile;
	
	@OneToMany(mappedBy = "wall")
	private List<Product> products;
	
	@PrePersist
    protected void onCreate() {
		this.wallName = profile.getUser().getUsername();
		this.coverBackgroundUrl = "https://img.freepik.com/premium-vector/abstract-smooth-gradient-particle-wave-background-with-contour-lines-techfuturistic-style_181477-62.jpg";
        
    }
}
