package com.naturalprogrammer.spring.lemon.auth.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Base class for all entities.
 * 
 * @author Sanjay Patel
  */
//todo alle meine Entites sollten hier von erben -> meine TimeStamp abstract Entity raushauen
@MappedSuperclass
@Getter @Setter
@JsonIgnoreProperties({ "createdById", "lastModifiedById", "createdDate", "lastModifiedDate", "new" })
@EntityListeners(AuditingEntityListener.class)
@ToString(callSuper = true)
public class LemonEntity<ID extends Serializable> extends IdentifiableEntityImpl<ID>
{

	private static final long serialVersionUID = -8151190931948396443L;
	
	@CreatedBy
	private ID createdById;
	
	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	
	@LastModifiedBy
	private ID lastModifiedById;
	
	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModifiedDate;

	
}
