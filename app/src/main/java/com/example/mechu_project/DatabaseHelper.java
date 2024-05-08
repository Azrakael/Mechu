package com.example.mechu_project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static String NAME = "0501_latest.db";
    public static int VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        println("onCreate 호출됨");


        db.execSQL("CREATE TABLE IF NOT EXISTS meal_log ( " +
                "user_id TEXT, " +
                "meal_date TEXT NOT NULL, " +
                "meal_time TEXT NOT NULL, " +
                "food_num INTEGER, " +
                "FOREIGN KEY(user_id) REFERENCES user(user_id), " +
                "FOREIGN KEY(food_num) REFERENCES food(food_num));");

        db.execSQL("CREATE TABLE IF NOT EXISTS food ( " +
                "food_num INTEGER PRIMARY KEY AUTOINCREMENT , " +
                "food_name TEXT NOT NULL UNIQUE, " +
                "food_img BLOB, " +
                "calorie REAL NOT NULL, " +
                "carbs REAL NOT NULL, " +
                "protein REAL NOT NULL, " +
                "fat REAL NOT NULL, " +
                "category_name TEXT NOT NULL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS review ( " +
                "user_id TEXT, " +
                "review_date TEXT, " +
                "review_like INTEGER, " +
                "review_content TEXT, " +
                "FOREIGN KEY(user_id) REFERENCES user(user_id));");

        db.execSQL("CREATE TABLE IF NOT EXISTS search ( " +
                "user_id TEXT, " +
                "search_num INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "search_term TEXT NOT NULL, " +
                "search_date TEXT NOT NULL, " +
                "FOREIGN KEY(user_id) REFERENCES user(user_id));");

        db.execSQL("CREATE TABLE IF NOT EXISTS user ( " +
                "user_id TEXT, " +
                "user_name TEXT NOT NULL, " +
                "email TEXT NOT NULL, " +
                "password TEXT NOT NULL, " +
                "login_check TEXT, " +
                "sex TEXT NOT NULL, " +
                "exercise_type TEXT NOT NULL, " +
                "height REAL NOT NULL, " +
                "weight REAL NOT NULL, " +
                "profile_img BLOB, " +
                "target_weight REAL NOT NULL, " +
                "daily_calorie REAL, " +
                "daily_carbs REAL, " +
                "daily_protein REAL, " +
                "daily_fat REAL, " +
                "PRIMARY KEY(user_id));");

        db.execSQL("INSERT INTO food VALUES " +
                "(1,'닭갈비',NULL,290.0,0.0,30.0,18.0,'육류'), " +
                "(2,'갈비찜',NULL,250.0,0.0,25.0,16.0,'육류'), " +
                "(3,'꽃게탕',NULL,89.0,2.3,20.0,0.9,'해산물'), " +
                "(4,'삼치조림',NULL,140.0,0.0,30.0,2.8,'해산물'), " +
                "(5,'비빔밥',NULL,101.0,21.0,4.0,0.3,'곡류'), " +
                "(6,'냉면',NULL,99.0,21.0,3.5,0.5,'곡류'), " +
                "(7,'김치',NULL,16.0,3.4,0.7,0.1,'채소'), " +
                "(8,'부추전',NULL,49.0,9.0,4.3,0.9,'채소'), " +
                "(9,'한라봉에이드',NULL,52.0,13.0,1.0,0.2,'과일'), " +
                "(10,'수박',NULL,60.0,15.0,0.8,0.4,'과일'), " +
                "(11,'호두죽',NULL,673.0,13.0,13.0,68.0,'견과류'), " +
                "(12,'호두아이스크림',NULL,562.0,28.0,20.0,45.0,'견과류'), " +
                "(13,'두유',NULL,17.0,1.3,0.5,1.3,'유제품'), " +
                "(14,'두부김치',NULL,224.0,9.0,22.0,11.0,'유제품'), " +
                "(15,'요거트',NULL,98.0,5.0,1.0,8.0,'유제품'), " +
                "(16,'삼겹살',NULL,518.0,0.0,11.0,55.0,'육류'), " +
                "(17,'소고기국',NULL,321.0,0.0,19.0,27.0,'육류'), " +
                "(18,'오징어볶음',NULL,105.0,8.0,15.0,2.5,'해산물'), " +
                "(19,'홍어무침',NULL,96.0,0.0,21.0,2.3,'해산물'), " +
                "(20,'밀면',NULL,155.0,33.0,5.7,1.0,'곡류'), " +
                "(21,'호박전',NULL,79.0,15.0,3.0,0.7,'곡류'), " +
                "(22,'열무김치',NULL,28.0,6.0,0.9,0.1,'채소'), " +
                "(23,'깍두기',NULL,43.0,9.0,3.4,0.3,'채소'), " +
                "(24,'사과',NULL,57.0,15.0,0.4,0.1,'과일'), " +
                "(25,'배',NULL,34.0,8.2,0.8,0.2,'과일'), " +
                "(26,'땅콩',NULL,628.0,17.0,15.0,61.0,'견과류'), " +
                "(27,'호두',NULL,718.0,14.0,7.9,75.0,'견과류'), " +
                "(28,'아몬드 라떼',NULL,25.0,2.0,1.0,1.5,'유제품'), " +
                "(29,'치즈라면',NULL,300.0,4.0,21.0,24.0,'유제품'), " +
                "(30,'요거트 아이스크림',NULL,80.0,8.0,1.0,4.0,'유제품'), " +
                "(31,'소세지볶음',NULL,301.0,2.0,13.0,26.0,'육류'), " +
                "(32,'오리불고기',NULL,299.0,0.0,28.0,20.0,'육류'), " +
                "(33,'오징어숙회',NULL,92.0,3.0,18.0,1.4,'해산물'), " +
                "(34,'북어채무침',NULL,94.0,0.0,21.0,1.3,'해산물'), " +
                "(35,'쌀국수',NULL,83.0,18.0,3.0,0.2,'곡류'), " +
                "(36,'옥수수면',NULL,198.0,33.0,5.0,4.0,'곡류'), " +
                "(37,'시금치무침',NULL,43.0,10.0,1.6,0.2,'채소'), " +
                "(38,'쌈무',NULL,25.0,6.0,1.3,0.1,'채소'), " +
                "(39,'사과맛탕',NULL,52.0,14.0,0.3,0.2,'과일'), " +
                "(40,'파파야샐러드',NULL,43.0,11.0,0.5,0.3,'과일'), " +
                "(41,'호두푸딩',NULL,656.0,12.0,14.0,66.0,'견과류'), " +
                "(42,'감자탕',NULL,245.0,52.0,2.0,2.2,'견과류'), " +
                "(43,'아몬드라떼',NULL,73.0,1.5,1.5,6.0,'유제품'), " +
                "(44,'치즈피자',NULL,360.0,2.0,22.0,30.0,'유제품'), " +
                "(45,'코코넛 아이스크림',NULL,175.0,22.0,1.0,9.0,'유제품'), " +
                "(46,'햄버거',NULL,455.0,1.9,20.0,39.0,'육류'), " +
                "(47,'설렁탕',NULL,158.0,0.0,22.0,7.0,'육류');");

    }

    public void onOpen(SQLiteDatabase db) {
        println("onOpen 호출됨");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        println("onUpgrade 호출됨 : " + oldVersion + " -> " + newVersion);

        if (newVersion > 1) {
            db.execSQL("CREATE TABLE IF NOT EXISTS test(" +
                    "a INTEGER);");
        }
    }

    public void println(String data) {
        Log.d("DatabaseHelper", data);
    }
}
