package es.javautodidacta.criminalintent.database;

/**
 * This class defines the schema for the {@code Crime} database.
 *
 * @author Miguel Callejón Berenguer
 * @version 2018.01
 */

public class CrimeDbSchema {
    /**
     * This class defines the elements (names) of the table.
     */
    public static final class CrimeTable {
        public static final String NAME = "crimes";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
            public static final String PHONE_NUMBER = "phone_number";
        }
    }
}
