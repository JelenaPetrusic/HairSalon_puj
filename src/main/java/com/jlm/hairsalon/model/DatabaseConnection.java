package com.jlm.hairsalon.model;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;


@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RepetedEntity.class)
@interface Entity {
    String type();
    int size() default 0;
    boolean primary() default false;
    boolean isnull() default true;
}

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RepetedForeignKey.class)
@interface ForeignKey {
    String table();
    String attribute();
}

@Retention(RetentionPolicy.RUNTIME)
@interface RepetedEntity{
    Entity[] value();
}

@Retention(RetentionPolicy.RUNTIME)
@interface RepetedForeignKey{
    ForeignKey[] value();
}


public class DatabaseConnection {

    private static String hostname = "localhost";
    private static String user = "root";
    private static String password = "";
    private static String database = "hairsalondb";

    public static Connection CONNECTION = null;

    static {
        try {
            CONNECTION = DriverManager.getConnection(
                    "jdbc:mysql://"+hostname+":3306/"+database, user, password
            );
        } catch (SQLException e) {
            System.out.println("I did not connect to database: " + e.getMessage());
        }
    }
}