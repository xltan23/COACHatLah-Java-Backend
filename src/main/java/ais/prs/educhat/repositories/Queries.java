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
        INSERT INTO flag_records (sender_id, recipient_id, message_id, original_text, rephrase_suggested, flag_category, flagged_at) 
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

    public final static String SQL_SELECT_FLAGRECORDS_BY_ID = """
        SELECT * FROM flag_records WHERE sender_id = ?        
    """;
}
