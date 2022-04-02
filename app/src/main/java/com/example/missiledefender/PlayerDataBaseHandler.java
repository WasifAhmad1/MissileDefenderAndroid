package com.example.missiledefender;

import android.app.Activity;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class PlayerDataBaseHandler implements Runnable {
    private final Activity context;
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

    public PlayerDataBaseHandler(Activity context, int score) {
        this.context =  context;
        this.score = score;
    }

    @Override
    public void run() {

        String className = context.getClass().toString();
        String [] classSplit = className.split("[ .]+");
        String theClass = classSplit[4];

        if(theClass.equals("MainActivity")){
            try {
                checkIfTopTen((MainActivity) this.context, this.score);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }


        if(theClass.equals("ScoresActivity")){
            try {
                getAllTopTen();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }


    public void checkIfTopTen(MainActivity mainActivity, int score) throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
        Statement stmt = conn.createStatement();

        String sql = "SELECT * FROM AppScores ORDER BY Score DESC LIMIT 10";

        StringBuilder sb = new StringBuilder();
        int min = score;
        boolean isGreater = false;

        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            int checkScore = rs.getInt(3);
            if(score>=checkScore){
                isGreater=true;
            }
        }
        if(isGreater==true) {
            mainActivity.genDialogBox(score);
        }
        else if (isGreater==false) {
            mainActivity.jumpToActivity();
        }

        rs.close();
        stmt.close();

    }

    public void getAllTopTen() throws SQLException, ClassNotFoundException {
        Class.forName(JDBC_DRIVER);
        conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
        Statement stmt = conn.createStatement();

        String sql = "SELECT * FROM AppScores ORDER BY Score DESC LIMIT 10";

        StringBuilder sb = new StringBuilder();

        ResultSet rs = stmt.executeQuery(sql);
        int count = 0;

        while (rs.next()) {
            count++;
            String padding = "";
            Long millis = rs.getLong(1);
            String init = rs.getString(2);
            int score = rs.getInt(3);
            int level = rs.getInt(4);
            if(init.equals("")) {
                init = "AAA";
            }
            if(count==10) {
                sb.append(String.format(Locale.getDefault(),
                        "%-3s %-10d %-10s %-10d %-24d %12s%n", padding, count, init, score, level, sdf.format(new Date(millis))));
            } else {
            sb.append(String.format(Locale.getDefault(),
                    " %-2s %-10d %-10s %-10d %-24d %12s%n",padding, count, init, score, level, sdf.format(new Date(millis)))); }

        }
        rs.close();
        stmt.close();

        ScoresActivity.displayScores(sb.toString());
    }


}
