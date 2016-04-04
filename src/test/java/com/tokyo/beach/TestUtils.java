package com.tokyo.beach;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

public class TestUtils {
    public static DataSource buildDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost/osusume-test");
        return dataSource;
    }
}
