package developers.sd.notes.database;

public class NoteDbSchema {
    public  static final class NoteTable {
        public static final String NAME = "notes";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String IMAGE = "image";
            public static final String COLOR = "color";
            public static final String DELETED = "deleted";
            public static final String SUBJECT = "subject";
        }
    }
}
