package com.example.missiledefender;

import android.app.Activity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PlayerDBInserter implements Runnable {
    private final ScoresActivity context;
    private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
    // jdbc:mysql://christopherhield.com:3306/chri5558_missile_defense
    private static Connection conn;
    private static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String STUDENT_TABLE = "AppScores";
    private final static String dbName = "chri5558_missile_defense";
    private final static String dbURL = "jdbc:mysql://christopherhield.com:3306/" + dbName;
    private final static String dbUser = "chri5558_student";
    private final static String dbPass = "ABC.123";
    private final int score;
    private final String initial;
    private final int level;

    public PlayerDBInserter(ScoresActivity context, int score, String initial, int level) {
        this.context = context;
        this.score = score;
        this.initial = initial;
        this.level = level;
    }


    @Override
    public void run() {

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
            Statement stmt = conn.createStatement();
            StringBuilder sb = new StringBuilder();
            String sql = "INSERT INTO " + STUDENT_TABLE + " VALUES (" +
                    System.currentTimeMillis() + ", '" + initial + "', " + score + ", " +
                     level + ")";

            int result = stmt.executeUpdate(sql);

            stmt.close();
            conn.close();



        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    }


