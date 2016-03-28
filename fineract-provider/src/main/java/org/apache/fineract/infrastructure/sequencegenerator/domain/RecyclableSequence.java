package org.apache.fineract.infrastructure.sequencegenerator.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "sequence_mst_recyclable", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }, name = "name") })
public class RecyclableSequence extends AbstractPersistable<Long> {

    @Column(name = "name", length = 200)
    private String name;

    @Column(name="max_seq_num")
    private Long maxSeqNum;
    
    @Column(name="padding_char")
    private String padding; 
    
    @Column(name = "sequence_value")
    private Long value;
    
    @Column(name="day_valid")
    private Date dayValid;

    @Column(name="hour_valid")
    private Date hourValid;
    
}