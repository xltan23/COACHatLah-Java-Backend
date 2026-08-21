package ais.prs.educhat.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import static ais.prs.educhat.repositories.Queries.*;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import ais.prs.educhat.models.FlagRecord;

@Repository
public class FlagRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean addFlagRecord(FlagRecord flagRecord) {
        System.out.println("\nInserting flag record into database: " + flagRecord);
        return jdbcTemplate.update(SQL_INSERT_FLAGRECORD_IN_FLAGRECORDS,
            flagRecord.getSenderId(),
            flagRecord.getReceiverId(),
            flagRecord.getMessageId(),
            flagRecord.getOriginalText(),
            flagRecord.getRephraseSuggested(),
            flagRecord.getFlagCategory().name(),
            Timestamp.from(flagRecord.getFlaggedAt())
        ) > 0;
    }

    // For disciplinary committee to view Flag Records
    public List<FlagRecord> getFlagRecords(String id) {
        System.out.printf("\nRetrieving flag records for profile %s from database ", id);

        SqlRowSet srs = jdbcTemplate.queryForRowSet(SQL_SELECT_FLAGRECORDS_BY_ID, id);
        List<FlagRecord> flagRecords = new LinkedList<>();
        while (srs.next()) {
            flagRecords.add(FlagRecord.createFlagRecord(srs));
        }

        return flagRecords;
    }
}
