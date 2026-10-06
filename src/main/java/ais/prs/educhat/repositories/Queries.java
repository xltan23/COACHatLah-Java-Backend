package ais.prs.educhat.repositories;

public class Queries {
    
    public final static String SQL_INSERT_PROFILE_IN_PROFILES = """
        INSERT INTO profiles (id, name, gender, role, school, class_cohort, cca, status, disciplinary_status, current_semester_flag_count, created_at, updated_at) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

    public final static String SQL_SELECT_PROFILES_BY_SCHOOL_AND_CLASS = """
        SELECT * FROM profiles WHERE school = ? AND class_cohort = ?
    """;

    public final static String SQL_SELECT_PROFILES_BY_SCHOOL_AND_CCA = """
        SELECT * FROM profiles WHERE school = ? AND cca = ?
    """;

    public final static String SQL_SELECT_PROFILES_BY_ID = """
        SELECT * FROM profiles WHERE id = ?
    """;

    public final static String SQL_INCREMENT_FLAG_COUNT = """
        UPDATE profiles SET current_semester_flag_count = current_semester_flag_count + 1, updated_at = ? WHERE id = ?
    """;

    public final static String SQL_GET_FLAG_COUNT = """
        SELECT current_semester_flag_count FROM profiles WHERE id = ?        
    """;

    public final static String SQL_INSERT_FLAGRECORD_IN_FLAGRECORDS = """
        INSERT INTO flag_records (sender_id, recipient_id, message_id, original_text, rephrase_suggested, toxic, inappropriate, cyberbullying, confidence_scores, flagged_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

    public final static String SQL_SELECT_FLAGRECORDS_BY_ID = """
        SELECT * FROM flag_records WHERE sender_id = ?        
    """;

    // Hourly flag counts per sender for flagged_at in [start, end); only hours with at least one flag are returned
    public final static String SQL_SELECT_HOURLY_FLAG_COUNTS = """
        SELECT sender_id, HOUR(flagged_at) AS hour_of_day, COUNT(*) AS flag_count
        FROM flag_records
        WHERE flagged_at >= ? AND flagged_at < ?
        GROUP BY sender_id, HOUR(flagged_at)
    """;

    public final static String SQL_INSERT_ESCALATION_IN_ESCALATIONS = """
        INSERT INTO escalations (student_id, class_cohort, trigger_type, trigger_detail, review_status, created_at) VALUES (?, ?, ?, ?, ?, ?)
    """;

    public final static String SQL_SELECT_ALL_ESCALATIONS = """
        SELECT * FROM escalations
    """;
}
