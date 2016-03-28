package org.apache.fineract.infrastructure.sequencegenerator.domain;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RecyclableSequenceRepository extends JpaRepository<RecyclableSequence, Long>, JpaSpecificationExecutor<RecyclableSequence> {

    @Modifying
    @Query(value="UPDATE sequence_mst_recyclable SET sequence_value = (case when sequence_value < max_seq_num then sequence_value + 1 else 1 end) WHERE name = ? ",nativeQuery = true)
    public int updateNextSequence(String sequenceName);
    
    @Query(value="SELECT lpad((sequence_value - 1) , length(max_seq_num), padding_char) from sequence_mst_recyclable where name = ? ", nativeQuery = true)
    public String getSequence(String sequenceName);
    
    @Modifying
    @Query(value="UPDATE sequence_mst_recyclable SET sequence_value = (case when sequence_value < max_seq_num then (sequence_value + 1)  else 1 end) WHERE name = ? and day_valid = ?",nativeQuery = true)
    public int updateNextSequenceForDate(String sequenceName, String whereDate);

    @Modifying
    @Query(value="UPDATE sequence_mst_recyclable SET sequence_value=(case when sequence_value < max_seq_num then 1 else 1 end), day_valid = ? WHERE name = ? ",nativeQuery = true)
    public int updateNextSequenceAndDateForSequence(Date updateDate, String sequenceName);
    
    @Query(value="SELECT lpad(sequence_value, length(max_seq_num), padding_char) from sequence_mst_recyclable where name = ? ", nativeQuery = true)
    public String getUpdatedSequence(String sequenceName);

    
    @Query(value="SELECT sequence_value from sequence_mst_recyclable where name = ? and day_valid = ? ", nativeQuery = true)
    public Long findOneByNameAndDayValid(String sequenceName, String date);
    
    @Modifying
    @Query(value="UPDATE sequence_mst_recyclable SET sequence_value = 0, day_valid = ? WHERE name = ? and day_valid = ? ",nativeQuery = true)
    public void updateByNameAndDayValid(Date dateToSet, String sequenceName, String whereDate);
    
}