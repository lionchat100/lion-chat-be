package com.lion.be.user.domain.entity;

import com.lion.be.user.domain.AgreementType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "agreements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Agreements {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Enumerated(EnumType.STRING)
	private AgreementType agreementType;  // REQUIRED, MARKETING 등

	private boolean agreed;

	public Agreements(User user, AgreementType agreementType, Boolean agreed) {
		this.user = user;
		this.agreementType = agreementType;
		this.agreed = agreed != null ? agreed : false ;
	}
}
