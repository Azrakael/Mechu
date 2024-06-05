package com.example.mechu_project;

import static java.sql.DriverManager.println;

import java.text.SimpleDateFormat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static String NAME = "0501_latest.db";
    public static int VERSION = 15;
    private Context context;
    private static final String TAG = "DatabaseHelper";

    public DatabaseHelper(Context context) {
        super(context, NAME, null, VERSION);
        this.context = context;


    }

    // 특정 음식 이름으로 데이터베이스에서 검색하는 메서드


    // 음식 정보를 가져오는 메서드
    public Cursor getFoodInfo(String searchText) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT food_name, food_img, calorie, carbs, protein, fat FROM food WHERE food_name LIKE ?";
        String[] selectionArgs = new String[]{"%" + searchText + "%"};
        return db.query("food", new String[]{"food_name", "food_img", "calorie", "carbs", "protein", "fat"},
                "food_name LIKE ?", selectionArgs, null, null, null);
    }




    //외래키 지정하려면 외래키 활성화해야함 >> But 현재 활성화하면 어플리케이션이 팅기는 문제 발생

    @Override
    public void onCreate(SQLiteDatabase db) {

        println("onCreate 호출됨");

        db.execSQL("CREATE TABLE IF NOT EXISTS meal_log ( " +
                "log_id INTEGER PRIMARY KEY AUTOINCREMENT, " + //자동증가
                "user_id TEXT, " +
                "meal_date TEXT NOT NULL, " +
                "meal_time TEXT NOT NULL, " +
                "food_num INTEGER, " +
                "FOREIGN KEY(user_id) REFERENCES user(user_id), " +
                "FOREIGN KEY(food_num) REFERENCES food(food_num));");


        //기분 : 우울함 | 기분좋음 | 화남 | 지루함
        //날씨 : 비 | 쾌청 | 바람 | 눈
        db.execSQL("CREATE TABLE IF NOT EXISTS food ( " +
                "food_num INTEGER PRIMARY KEY AUTOINCREMENT, " +  //자동증가
                "food_name TEXT NOT NULL UNIQUE, " +
                "food_img TEXT, " + // BLOB에서 TEXT로 변경
                "calorie REAL NOT NULL, " +
                "carbs REAL NOT NULL, " +
                "protein REAL NOT NULL, " +
                "fat REAL NOT NULL, " +
                "mood_tag TEXT NOT NULL, " +
                "weather_tag TEXT NOT NULL, " +
                "category_name TEXT NOT NULL, " +
                "is_low_carbon INTEGER DEFAULT 0);");

        db.execSQL("CREATE TABLE IF NOT EXISTS review ( " +
                "review_id INTEGER PRIMARY KEY AUTOINCREMENT, " +  //자동증가
                "user_id TEXT, " +
                "review_date TEXT, " +
                "review_like INTEGER, " +
                "review_content TEXT, " +
                "FOREIGN KEY(user_id) REFERENCES user(user_id));");

        db.execSQL("CREATE TABLE IF NOT EXISTS search ( " +
                "search_num INTEGER PRIMARY KEY AUTOINCREMENT, " +  //자동증가
                "user_id TEXT, " +
                "search_term TEXT NOT NULL, " +
                "search_date TEXT NOT NULL, " +
                "search_count INTEGER DEFAULT 0, " +
                "FOREIGN KEY(user_id) REFERENCES user(user_id));");

        db.execSQL("CREATE TABLE IF NOT EXISTS user ( " +
                "user_id TEXT PRIMARY KEY, " +
                "user_name TEXT NOT NULL, " +
                "email TEXT NOT NULL, " +
                "password TEXT NOT NULL, " +
                "login_check INTEGER DEFAULT 0, " +
                "sex TEXT NOT NULL, " +
                "exercise_type TEXT NOT NULL, " +
                "height REAL NOT NULL, " +
                "weight REAL NOT NULL, " +
                "age INTEGER NOT NULL, " +
                "profile_img BLOB, " +
                "target_weight REAL, " +
                "daily_calorie REAL, " +
                "daily_carbs REAL, " +
                "daily_protein REAL, " +
                "daily_fat REAL, " +
                "current_calorie REAL DEFAULT 0, " + // 현재까지 섭취한 칼로리
                "current_carbs REAL DEFAULT 0, " + // 현재까지 섭취한 탄수화물
                "current_protein REAL DEFAULT 0, " + // 현재까지 섭취한 단백질
                "current_fat REAL DEFAULT 0);"); // 현재까지 섭취한 지방

        String[] foods = new String[]{
                "('김치찌개', ?, 200.0, 10.0, 20.0, 5.0, '한식', '우울함', '비')",
                "('비빔밥', ?, 300.0, 50.0, 10.0, 10.0, '한식', '기분좋음', '쾌청')",
                "('불고기', ?, 250.0, 20.0, 25.0, 15.0, '한식', '기분좋음', '쾌청')",
                "('떡볶이', ?, 400.0, 60.0, 10.0, 10.0, '한식', '화남', '바람')",
                "('삼겹살', ?, 600.0, 0.0, 20.0, 50.0, '한식', '기분좋음', '쾌청')",
                "('된장찌개', ?, 150.0, 10.0, 10.0, 5.0, '한식', '우울함', '비')",
                "('갈비탕', ?, 350.0, 15.0, 30.0, 20.0, '한식', '우울함', '눈')",
                "('잡채', ?, 200.0, 30.0, 10.0, 5.0, '한식', '기분좋음', '쾌청')",
                "('김밥', ?, 300.0, 40.0, 10.0, 10.0, '한식', '지루함', '쾌청')",
                "('콩나물국', ?, 100.0, 5.0, 5.0, 2.0, '한식', '우울함', '비')",
                "('짜장면', ?, 500.0, 70.0, 15.0, 20.0, '중식', '지루함', '비')",
                "('짬뽕', ?, 450.0, 60.0, 20.0, 15.0, '중식', '화남', '바람')",
                "('탕수육', ?, 600.0, 80.0, 20.0, 25.0, '중식', '기분좋음', '쾌청')",
                "('깐풍기', ?, 550.0, 50.0, 30.0, 25.0, '중식', '화남', '바람')",
                "('양장피', ?, 400.0, 50.0, 20.0, 15.0, '중식', '기분좋음', '쾌청')",
                "('마파두부', ?, 300.0, 30.0, 20.0, 10.0, '중식', '우울함', '비')",
                "('볶음밥', ?, 450.0, 60.0, 15.0, 15.0, '중식', '지루함', '쾌청')",
                "('고추잡채', ?, 350.0, 40.0, 20.0, 15.0, '중식', '화남', '바람')",
                "('팔보채', ?, 500.0, 50.0, 30.0, 20.0, '중식', '기분좋음', '쾌청')",
                "('유린기', ?, 400.0, 40.0, 25.0, 15.0, '중식', '기분좋음', '쾌청')",
                "('햄버거', ?, 700.0, 50.0, 40.0, 30.0, '양식', '지루함', '쾌청')",
                "('스파게티', ?, 600.0, 80.0, 20.0, 25.0, '양식', '기분좋음', '쾌청')",
                "('피자', ?, 800.0, 90.0, 30.0, 35.0, '양식', '지루함', '쾌청')",
                "('스테이크', ?, 750.0, 10.0, 60.0, 50.0, '양식', '기분좋음', '쾌청')",
                "('샐러드', ?, 150.0, 20.0, 5.0, 10.0, '양식', '기분좋음', '쾌청')",
                "('리조또', ?, 500.0, 60.0, 20.0, 25.0, '양식', '기분좋음', '쾌청')",
                "('라자냐', ?, 600.0, 70.0, 25.0, 30.0, '양식', '지루함', '쾌청')",
                "('파스타', ?, 550.0, 70.0, 20.0, 25.0, '양식', '기분좋음', '쾌청')",
                "('그라탕', ?, 600.0, 50.0, 25.0, 35.0, '양식', '지루함', '쾌청')",
                "('바비큐', ?, 700.0, 20.0, 50.0, 40.0, '양식', '기분좋음', '쾌청')",
                "('초밥', ?, 300.0, 40.0, 15.0, 10.0, '일식', '기분좋음', '쾌청')",
                "('라멘', ?, 500.0, 60.0, 20.0, 20.0, '일식', '우울함', '비')",
                "('우동', ?, 400.0, 50.0, 15.0, 15.0, '일식', '우울함', '비')",
                "('덴푸라', ?, 350.0, 40.0, 10.0, 20.0, '일식', '기분좋음', '쾌청')",
                "('타코야키', ?, 200.0, 30.0, 10.0, 10.0, '일식', '지루함', '쾌청')",
                "('가츠동', ?, 600.0, 70.0, 20.0, 25.0, '일식', '기분좋음', '쾌청')",
                "('오니기리', ?, 250.0, 35.0, 5.0, 10.0, '일식', '지루함', '쾌청')",
                "('미소시루', ?, 100.0, 10.0, 5.0, 2.0, '일식', '우울함', '비')",
                "('야키소바', ?, 450.0, 55.0, 15.0, 20.0, '일식', '지루함', '쾌청')",
                "('오코노미야키', ?, 500.0, 60.0, 20.0, 25.0, '일식', '지루함', '쾌청')",
                "('아메리카노', ?, 5.0, 1.0, 0.0, 0.0, '카페', '기분좋음', '쾌청')",
                "('카페라떼', ?, 150.0, 15.0, 5.0, 5.0, '카페', '기분좋음', '쾌청')",
                "('카푸치노', ?, 120.0, 10.0, 5.0, 5.0, '카페', '기분좋음', '쾌청')",
                "('에스프레소', ?, 10.0, 2.0, 0.0, 0.0, '카페', '기분좋음', '쾌청')",
                "('모카', ?, 200.0, 30.0, 5.0, 10.0, '카페', '기분좋음', '쾌청')",
                "('아이스티', ?, 100.0, 25.0, 0.0, 0.0, '카페', '기분좋음', '쾌청')",
                "('밀크티', ?, 250.0, 30.0, 5.0, 10.0, '카페', '기분좋음', '쾌청')",
                "('스무디', ?, 200.0, 40.0, 5.0, 5.0, '카페', '기분좋음', '쾌청')",
                "('프라푸치노', ?, 300.0, 50.0, 5.0, 10.0, '카페', '기분좋음', '쾌청')",
                "('핫초코', ?, 350.0, 40.0, 5.0, 15.0, '카페', '우울함', '눈')",
                "('닭갈비', ?, 290.0, 15.0, 25.0, 10.0, '한식', '기분좋음', '쾌청')",
                "('떡국', ?, 350.0, 50.0, 10.0, 10.0, '한식', '우울함', '눈')",
                "('청국장', ?, 200.0, 15.0, 15.0, 5.0, '한식', '우울함', '비')",
                "('간장게장', ?, 180.0, 10.0, 20.0, 5.0, '한식', '기분좋음', '쾌청')",
                "('순두부찌개', ?, 150.0, 10.0, 10.0, 5.0, '한식', '우울함', '비')",
                "('돼지불백', ?, 450.0, 20.0, 25.0, 30.0, '한식', '기분좋음', '쾌청')",
                "('북엇국', ?, 100.0, 5.0, 10.0, 2.0, '한식', '우울함', '비')",
                "('도토리묵', ?, 50.0, 10.0, 5.0, 1.0, '한식', '기분좋음', '쾌청')",
                "('해물파전', ?, 300.0, 30.0, 15.0, 10.0, '한식', '기분좋음', '쾌청')",
                "('갈치조림', ?, 250.0, 10.0, 20.0, 15.0, '한식', '기분좋음', '쾌청')",
                "('마라탕', ?, 500.0, 40.0, 20.0, 30.0, '중식', '화남', '바람')",
                "('꿔바로우', ?, 600.0, 60.0, 20.0, 30.0, '중식', '화남', '바람')",
                "('홍합탕', ?, 150.0, 10.0, 15.0, 5.0, '중식', '우울함', '비')",
                "('고량주', ?, 200.0, 20.0, 10.0, 10.0, '중식', '기분좋음', '쾌청')",
                "('동파육', ?, 700.0, 50.0, 30.0, 40.0, '중식', '기분좋음', '쾌청')",
                "('취두부', ?, 100.0, 10.0, 10.0, 5.0, '중식', '지루함', '쾌청')",
                "('광둥식 오리', ?, 400.0, 20.0, 25.0, 20.0, '중식', '기분좋음', '쾌청')",
                "('샤오롱바오', ?, 300.0, 40.0, 10.0, 10.0, '중식', '기분좋음', '쾌청')",
                "('탄탄면', ?, 450.0, 50.0, 15.0, 20.0, '중식', '화남', '바람')",
                "('사천탕수육', ?, 500.0, 60.0, 20.0, 25.0, '중식', '화남', '바람')",
                "('파스타 알프레도', ?, 700.0, 90.0, 20.0, 30.0, '양식', '기분좋음', '쾌청')",
                "('클램 차우더', ?, 350.0, 40.0, 15.0, 20.0, '양식', '우울함', '비')",
                "('치즈버거', ?, 800.0, 50.0, 40.0, 35.0, '양식', '지루함', '쾌청')",
                "('필레미뇽', ?, 600.0, 5.0, 60.0, 30.0, '양식', '기분좋음', '쾌청')",
                "('프렌치 토스트', ?, 400.0, 60.0, 10.0, 15.0, '양식', '기분좋음', '쾌청')",
                "('갈릭 브레드', ?, 200.0, 30.0, 5.0, 10.0, '양식', '기분좋음', '쾌청')",
                "('미트볼', ?, 450.0, 20.0, 25.0, 30.0, '양식', '기분좋음', '쾌청')",
                "('크림 스프', ?, 300.0, 20.0, 10.0, 20.0, '양식', '우울함', '비')",
                "('에그 베네딕트', ?, 500.0, 30.0, 20.0, 30.0, '양식', '기분좋음', '쾌청')",
                "('리코타 치즈 샐러드', ?, 350.0, 20.0, 15.0, 20.0, '양식', '기분좋음', '쾌청')",
                "('데리야키 치킨', ?, 450.0, 40.0, 20.0, 15.0, '일식', '기분좋음', '쾌청')",
                "('돈코츠 라멘', ?, 550.0, 60.0, 20.0, 25.0, '일식', '우울함', '비')",
                "('카레라이스', ?, 600.0, 80.0, 15.0, 20.0, '일식', '지루함', '쾌청')",
                "('가라아게', ?, 400.0, 30.0, 15.0, 20.0, '일식', '기분좋음', '쾌청')",
                "('야키토리', ?, 250.0, 10.0, 20.0, 15.0, '일식', '기분좋음', '쾌청')",
                "('오뎅', ?, 200.0, 20.0, 10.0, 5.0, '일식', '우울함', '비')",
                "('스시롤', ?, 350.0, 50.0, 10.0, 10.0, '일식', '기분좋음', '쾌청')",
                "('샤부샤부', ?, 500.0, 30.0, 20.0, 20.0, '일식', '기분좋음', '쾌청')",
                "('나베', ?, 400.0, 40.0, 15.0, 10.0, '일식', '우울함', '비')",
                "('모찌', ?, 150.0, 30.0, 5.0, 2.0, '일식', '지루함', '쾌청')",
                "('핫아메리카노', ?, 5.0, 1.0, 0.0, 0.0, '카페', '기분좋음', '쾌청')",
                "('아포가토', ?, 250.0, 30.0, 5.0, 10.0, '카페', '기분좋음', '쾌청')",
                "('카페모카', ?, 300.0, 40.0, 10.0, 15.0, '카페', '기분좋음', '쾌청')",
                "('아이스바닐라라떼', ?, 200.0, 30.0, 5.0, 5.0, '카페', '기분좋음', '쾌청')",
                "('콜드브루', ?, 15.0, 3.0, 0.0, 0.0, '카페', '기분좋음', '쾌청')",
                "('더치커피', ?, 10.0, 2.0, 0.0, 0.0, '카페', '기분좋음', '쾌청')",
                "('레몬에이드', ?, 120.0, 30.0, 0.0, 0.0, '카페', '기분좋음', '쾌청')",
                "('플랫화이트', ?, 100.0, 10.0, 5.0, 5.0, '카페', '기분좋음', '쾌청')",
                "('블루베리스무디', ?, 180.0, 35.0, 5.0, 2.0, '카페', '기분좋음', '쾌청')",
                "('그린티라떼', ?, 220.0, 30.0, 10.0, 10.0, '카페', '기분좋음', '쾌청')",
                "('닭한마리', ?, 400.0, 20.0, 30.0, 15.0, '한식', '기분좋음', '쾌청')",
                "('김치볶음밥', ?, 500.0, 70.0, 15.0, 20.0, '한식', '기분좋음', '쾌청')",
                "('쭈꾸미볶음', ?, 350.0, 20.0, 30.0, 10.0, '한식', '기분좋음', '쾌청')",
                "('도토리묵무침', ?, 100.0, 15.0, 5.0, 1.0, '한식', '기분좋음', '쾌청')",
                "('꼬리곰탕', ?, 250.0, 10.0, 20.0, 15.0, '한식', '우울함', '눈')",
                "('부대찌개', ?, 400.0, 30.0, 20.0, 20.0, '한식', '기분좋음', '쾌청')",
                "('설렁탕', ?, 300.0, 5.0, 25.0, 10.0, '한식', '우울함', '눈')",
                "('해물순두부', ?, 350.0, 20.0, 20.0, 15.0, '한식', '기분좋음', '쾌청')",
                "('콩국수', ?, 500.0, 50.0, 20.0, 25.0, '한식', '기분좋음', '쾌청')",
                "('매운탕', ?, 150.0, 10.0, 20.0, 5.0, '한식', '화남', '바람')",
                "('난자완스', ?, 450.0, 40.0, 20.0, 20.0, '중식', '기분좋음', '쾌청')",
                "('짬뽕밥', ?, 500.0, 60.0, 20.0, 20.0, '중식', '화남', '바람')",
                "('잡채밥', ?, 600.0, 80.0, 20.0, 25.0, '중식', '기분좋음', '쾌청')",
                "('멸치볶음', ?, 150.0, 10.0, 20.0, 5.0, '중식', '기분좋음', '쾌청')",
                "('새우튀김', ?, 300.0, 30.0, 10.0, 15.0, '중식', '기분좋음', '쾌청')",
                "('칠리새우', ?, 500.0, 40.0, 20.0, 20.0, '중식', '기분좋음', '쾌청')",
                "('깐쇼새우', ?, 550.0, 50.0, 20.0, 25.0, '중식', '기분좋음', '쾌청')",
                "('고추잡채밥', ?, 600.0, 70.0, 20.0, 30.0, '중식', '화남', '바람')",
                "('군만두', ?, 400.0, 40.0, 15.0, 20.0, '중식', '기분좋음', '쾌청')",
                "('마라샹궈', ?, 500.0, 40.0, 20.0, 30.0, '중식', '화남', '바람')",
                "('베이컨 치즈버거', ?, 800.0, 50.0, 40.0, 35.0, '양식', '지루함', '쾌청')",
                "('카프레제 샐러드', ?, 200.0, 10.0, 10.0, 15.0, '양식', '기분좋음', '쾌청')",
                "('로스트 치킨', ?, 500.0, 20.0, 35.0, 20.0, '양식', '기분좋음', '쾌청')",
                "('비프 스튜', ?, 600.0, 40.0, 35.0, 25.0, '양식', '기분좋음', '쾌청')",
                "('시저 샐러드', ?, 300.0, 20.0, 10.0, 20.0, '양식', '기분좋음', '쾌청')",
                "('마르게리타 피자', ?, 600.0, 80.0, 20.0, 25.0, '양식', '지루함', '쾌청')",
                "('칠리 콘 카르네', ?, 400.0, 30.0, 20.0, 15.0, '양식', '기분좋음', '쾌청')",
                "('펜네 알라 보드카', ?, 700.0, 90.0, 20.0, 30.0, '양식', '기분좋음', '쾌청')",
                "('비프 타코', ?, 450.0, 50.0, 20.0, 20.0, '양식', '기분좋음', '쾌청')",
                "('햄 앤 치즈 샌드위치', ?, 500.0, 40.0, 20.0, 25.0, '양식', '기분좋음', '쾌청')",
                "('타코스', ?, 300.0, 30.0, 15.0, 10.0, '일식', '기분좋음', '쾌청')",
                "('참치마요 덮밥', ?, 450.0, 50.0, 20.0, 15.0, '일식', '기분좋음', '쾌청')",
                "('규동', ?, 500.0, 60.0, 20.0, 20.0, '일식', '기분좋음', '쾌청')",
                "('에비후라이', ?, 400.0, 30.0, 15.0, 20.0, '일식', '기분좋음', '쾌청')",
                "('야채튀김', ?, 300.0, 40.0, 10.0, 15.0, '일식', '기분좋음', '쾌청')",
                "('아게다시도후', ?, 250.0, 20.0, 10.0, 10.0, '일식', '기분좋음', '쾌청')",
                "('쇼유라멘', ?, 500.0, 60.0, 20.0, 20.0, '일식', '우울함', '비')",
                "('비프야끼', ?, 450.0, 50.0, 20.0, 15.0, '일식', '기분좋음', '쾌청')",
                "('사시미', ?, 200.0, 10.0, 25.0, 5.0, '일식', '기분좋음', '쾌청')",
                "('다코야키', ?, 300.0, 40.0, 10.0, 15.0, '일식', '기분좋음', '쾌청')",
                "('더치라떼', ?, 150.0, 20.0, 5.0, 5.0, '카페', '기분좋음', '쾌청')",
                "('카라멜 마키아토', ?, 300.0, 40.0, 10.0, 10.0, '카페', '기분좋음', '쾌청')",
                "('아이스 카페모카', ?, 250.0, 30.0, 5.0, 10.0, '카페', '기분좋음', '쾌청')",
                "('코코아', ?, 200.0, 30.0, 5.0, 5.0, '카페', '우울함', '눈')",
                "('아이스 드립 커피', ?, 10.0, 2.0, 0.0, 0.0, '카페', '기분좋음', '쾌청')",
                "('망고 주스', ?, 180.0, 40.0, 0.0, 0.0, '카페', '기분좋음', '쾌청')",
                "('딸기 스무디', ?, 200.0, 45.0, 5.0, 2.0, '카페', '기분좋음', '쾌청')",
                "('초콜릿 프라푸치노', ?, 400.0, 60.0, 5.0, 15.0, '카페', '기분좋음', '쾌청')",
                "('바닐라 셰이크', ?, 300.0, 50.0, 5.0, 10.0, '카페', '기분좋음', '쾌청')",
                "('낙지볶음', ?, 300.0, 20.0, 25.0, 10.0, '한식', '기분좋음', '쾌청')",
                "('콩나물밥', ?, 400.0, 60.0, 10.0, 5.0, '한식', '기분좋음', '쾌청')",
                "('호박죽', ?, 200.0, 45.0, 5.0, 2.0, '한식', '우울함', '비')",
                "('감자전', ?, 250.0, 40.0, 5.0, 10.0, '한식', '기분좋음', '쾌청')",
                "('홍어찜', ?, 150.0, 10.0, 20.0, 5.0, '한식', '기분좋음', '쾌청')",
                "('두부조림', ?, 300.0, 20.0, 20.0, 10.0, '한식', '기분좋음', '쾌청')",
                "('물냉면', ?, 350.0, 50.0, 10.0, 5.0, '한식', '기분좋음', '쾌청')",
                "('백김치', ?, 50.0, 10.0, 1.0, 0.5, '한식', '기분좋음', '쾌청')",
                "('제육볶음', ?, 500.0, 30.0, 30.0, 20.0, '한식', '기분좋음', '쾌청')",
                "('오징어볶음', ?, 400.0, 20.0, 25.0, 15.0, '한식', '기분좋음', '쾌청')",
                "('깐풍새우', ?, 350.0, 30.0, 20.0, 15.0, '중식', '기분좋음', '쾌청')",
                "('어향동고', ?, 300.0, 25.0, 15.0, 10.0, '중식', '기분좋음', '쾌청')",
                "('해물짬뽕', ?, 550.0, 70.0, 20.0, 15.0, '중식', '화남', '바람')",
                "('소고기쌀국수', ?, 400.0, 50.0, 20.0, 10.0, '중식', '기분좋음', '쾌청')",
                "('중국냉면', ?, 450.0, 60.0, 15.0, 20.0, '중식', '기분좋음', '쾌청')",
                "('베이징덕', ?, 600.0, 40.0, 30.0, 35.0, '중식', '기분좋음', '쾌청')",
                "('소고기탕면', ?, 500.0, 70.0, 20.0, 15.0, '중식', '기분좋음', '쾌청')",
                "('파인애플볶음밥', ?, 450.0, 80.0, 10.0, 15.0, '중식', '기분좋음', '쾌청')",
                "('마파두부덮밥', ?, 500.0, 60.0, 20.0, 20.0, '중식', '우울함', '비')",
                "('사천식볶음', ?, 550.0, 50.0, 25.0, 25.0, '중식', '화남', '바람')",
                "('크로크무슈', ?, 400.0, 50.0, 20.0, 20.0, '양식', '기분좋음', '쾌청')",
                "('치킨케밥', ?, 350.0, 30.0, 25.0, 15.0, '양식', '기분좋음', '쾌청')",
                "('치킨 알프레도', ?, 600.0, 70.0, 25.0, 30.0, '양식', '기분좋음', '쾌청')",
                "('블루치즈버거', ?, 700.0, 50.0, 35.0, 35.0, '양식', '지루함', '쾌청')",
                "('볼로네제 파스타', ?, 550.0, 70.0, 20.0, 25.0, '양식', '기분좋음', '쾌청')",
                "('무스 비프', ?, 500.0, 30.0, 35.0, 20.0, '양식', '기분좋음', '쾌청')",
                "('칠리 도그', ?, 450.0, 40.0, 15.0, 20.0, '양식', '지루함', '쾌청')",
                "('트리플 치즈 피자', ?, 800.0, 90.0, 30.0, 40.0, '양식', '지루함', '쾌청')",
                "('포크 찹', ?, 600.0, 20.0, 45.0, 30.0, '양식', '기분좋음', '쾌청')",
                "('구운 채소 샐러드', ?, 200.0, 20.0, 5.0, 10.0, '양식', '기분좋음', '쾌청')",
                "('연어롤', ?, 300.0, 40.0, 15.0, 10.0, '일식', '기분좋음', '쾌청')",
                "('우나기 덮밥', ?, 500.0, 60.0, 20.0, 20.0, '일식', '기분좋음', '쾌청')",
                "('치킨 가라아게', ?, 400.0, 30.0, 20.0, 20.0, '일식', '기분좋음', '쾌청')",
                "('니꾸자가', ?, 450.0, 40.0, 25.0, 15.0, '일식', '기분좋음', '쾌청')",
                "('에비카츠', ?, 350.0, 40.0, 15.0, 15.0, '일식', '기분좋음', '쾌청')",
                "('카키아게', ?, 300.0, 40.0, 10.0, 15.0, '일식', '기분좋음', '쾌청')",
                "('미소라멘', ?, 500.0, 60.0, 20.0, 20.0, '일식', '우울함', '비')",
                "('테판야끼', ?, 450.0, 50.0, 20.0, 20.0, '일식', '기분좋음', '쾌청')",
                "('고로케', ?, 400.0, 45.0, 10.0, 20.0, '일식', '기분좋음', '쾌청')",
                "('오차즈케', ?, 200.0, 30.0, 5.0, 2.0, '일식', '기분좋음', '쾌청')",
                "('아몬드 밀크', ?, 150.0, 15.0, 5.0, 10.0, '카페', '기분좋음', '쾌청')",
                "('헤이즐넛 라떼', ?, 250.0, 30.0, 5.0, 10.0, '카페', '기분좋음', '쾌청')",
                "('마차 라떼', ?, 200.0, 25.0, 5.0, 5.0, '카페', '기분좋음', '쾌청')",
                "('화이트 초콜릿 모카', ?, 350.0, 50.0, 5.0, 15.0, '카페', '기분좋음', '쾌청')",
                "('얼그레이 티', ?, 50.0, 10.0, 0.0, 0.0, '카페', '기분좋음', '쾌청')",
                "('핫 레몬차', ?, 100.0, 25.0, 0.0, 0.0, '카페', '기분좋음', '쾌청')",
                "('카라멜 프라푸치노', ?, 400.0, 60.0, 5.0, 15.0, '카페', '기분좋음', '쾌청')",
                "('복숭아 아이스티', ?, 120.0, 30.0, 0.0, 0.0, '카페', '기분좋음', '쾌청')",
                "('자몽 에이드', ?, 150.0, 35.0, 0.0, 0.0, '카페', '기분좋음', '쾌청')",
                "('요거트 스무디', ?, 180.0, 35.0, 5.0, 5.0, '카페', '기분좋음', '쾌청')",
                "('에너지 드링크', ?, 150.0, 40.0, 0.0, 0.0, '카페', '기분좋음', '쾌청')"
        };

        db.execSQL("INSERT INTO user (user_id, user_name, email, password, login_check, sex, exercise_type, height, weight, age, daily_calorie,target_weight,daily_carbs,daily_protein,daily_fat) VALUES " +
                "('root', '관리자', 'admin@test.com', '1111', 1, 'M', '일반', 175, 69, 23,3000,66,50,100,50);");

        new InsertDataAsyncTask(this.context, db, foods).execute();
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        println("onOpen 호출됨");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        println("onUpgrade 호출됨 : " + oldVersion + " -> " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS meal_log");
        db.execSQL("DROP TABLE IF EXISTS food");
        db.execSQL("DROP TABLE IF EXISTS review");
        db.execSQL("DROP TABLE IF EXISTS search");
        db.execSQL("DROP TABLE IF EXISTS user");
        onCreate(db);
    }

    public void println(String data) {
        Log.d(TAG, data);
    }

    public Cursor getFoodDetails(String foodName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT food_name, food_img, calorie, carbs, protein, fat FROM food WHERE food_name = ?", new String[]{foodName});
    }

    public void deleteDatabase(Context context) {
        boolean deleted = context.deleteDatabase(NAME);
        println("데이터베이스 삭제됨: " + deleted);
    }

    private static class InsertDataAsyncTask extends AsyncTask<Void, Integer, Void> {
        private Context context;
        private SQLiteDatabase db;
        private String[] foods;

        public InsertDataAsyncTask(Context context, SQLiteDatabase db, String[] foods) {
            this.context = context;
            this.db = db;
            this.foods = foods;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            Map<String, Integer> foodImageMap = new HashMap<>();
            foodImageMap.put("김치찌개", R.drawable.kimchi_stew);
            foodImageMap.put("비빔밥", R.drawable.bibimbap);
            foodImageMap.put("불고기", R.drawable.bulgogi);
            foodImageMap.put("떡볶이", R.drawable.tteokbokki);
            foodImageMap.put("삼겹살", R.drawable.samgyeopsal);
            foodImageMap.put("된장찌개", R.drawable.doenjang_jjigae);
            foodImageMap.put("갈비탕", R.drawable.galbitang);
            foodImageMap.put("잡채", R.drawable.japchae);
            foodImageMap.put("김밥", R.drawable.gimbap);
            foodImageMap.put("콩나물국", R.drawable.kongnamul_guk);
            foodImageMap.put("짜장면", R.drawable.jjajangmyeon);
            foodImageMap.put("짬뽕", R.drawable.jjambbong);
            foodImageMap.put("탕수육", R.drawable.tangsuyuk);
            foodImageMap.put("깐풍기", R.drawable.kkanpunggi);
            foodImageMap.put("양장피", R.drawable.yangjangpi);
            foodImageMap.put("마파두부", R.drawable.mapo_tofu);
            foodImageMap.put("볶음밥", R.drawable.bokkeumbap);
            foodImageMap.put("고추잡채", R.drawable.gochujapchae);
            foodImageMap.put("팔보채", R.drawable.palbochae);
            foodImageMap.put("유린기", R.drawable.yuringi);
            foodImageMap.put("햄버거", R.drawable.hamburger);
            foodImageMap.put("스파게티", R.drawable.spaghetti);
            foodImageMap.put("피자", R.drawable.pizza);
            foodImageMap.put("스테이크", R.drawable.steak);
            foodImageMap.put("샐러드", R.drawable.salad);
            foodImageMap.put("리조또", R.drawable.risotto);
            foodImageMap.put("라자냐", R.drawable.lasagna);
            foodImageMap.put("파스타", R.drawable.pasta);
            foodImageMap.put("그라탕", R.drawable.gratin);
            foodImageMap.put("바비큐", R.drawable.barbecue);
            foodImageMap.put("초밥", R.drawable.sushi);
            foodImageMap.put("라멘", R.drawable.ramen);
            foodImageMap.put("우동", R.drawable.udon);
            foodImageMap.put("덴푸라", R.drawable.tempura);
            foodImageMap.put("타코야키", R.drawable.takoyaki);
            foodImageMap.put("가츠동", R.drawable.katsudon);
            foodImageMap.put("오니기리", R.drawable.onigiri);
            foodImageMap.put("미소시루", R.drawable.miso_soup);
            foodImageMap.put("야키소바", R.drawable.yakisoba);
            foodImageMap.put("오코노미야키", R.drawable.okonomiyaki);
            foodImageMap.put("아메리카노", R.drawable.americano);
            foodImageMap.put("카페라떼", R.drawable.caffe_latte);
            foodImageMap.put("카푸치노", R.drawable.cappuccino);
            foodImageMap.put("에스프레소", R.drawable.espresso);
            foodImageMap.put("모카", R.drawable.mocha);
            foodImageMap.put("아이스티", R.drawable.iced_tea);
            foodImageMap.put("밀크티", R.drawable.milk_tea);
            foodImageMap.put("스무디", R.drawable.smoothie);
            foodImageMap.put("프라푸치노", R.drawable.frappuccino);
            foodImageMap.put("핫초코", R.drawable.hot_chocolate);
            foodImageMap.put("닭갈비", R.drawable.dakgalbi);
            foodImageMap.put("떡국", R.drawable.tteokguk);
            foodImageMap.put("청국장", R.drawable.cheonggukjang);
            foodImageMap.put("간장게장", R.drawable.ganjang_gejang);
            foodImageMap.put("순두부찌개", R.drawable.sundubu_jjigae);
            foodImageMap.put("돼지불백", R.drawable.dwaeji_bulbaek);
            foodImageMap.put("북엇국", R.drawable.bugeoguk);
            foodImageMap.put("도토리묵", R.drawable.dotori_muk);
            foodImageMap.put("해물파전", R.drawable.haemul_pajeon);
            foodImageMap.put("갈치조림", R.drawable.galchi_jorim);
            foodImageMap.put("마라탕", R.drawable.malatang);
            foodImageMap.put("꿔바로우", R.drawable.guobaorou);
            foodImageMap.put("홍합탕", R.drawable.honghap_tang);
            foodImageMap.put("고량주", R.drawable.gaoliangjiu);
            foodImageMap.put("동파육", R.drawable.dongpo_pork);
            foodImageMap.put("취두부", R.drawable.choudoufu);
            foodImageMap.put("광둥식 오리", R.drawable.cantonese_duck);
            foodImageMap.put("샤오롱바오", R.drawable.xiaolongbao);
            foodImageMap.put("탄탄면", R.drawable.dan_dan_noodles);
            foodImageMap.put("사천탕수육", R.drawable.sichuan_tangsuyuk);
            foodImageMap.put("파스타 알프레도", R.drawable.pasta_alfredo);
            foodImageMap.put("클램 차우더", R.drawable.clam_chowder);
            foodImageMap.put("치즈버거", R.drawable.cheeseburger);
            foodImageMap.put("필레미뇽", R.drawable.filet_mignon);
            foodImageMap.put("프렌치 토스트", R.drawable.french_toast);
            foodImageMap.put("갈릭 브레드", R.drawable.garlic_bread);
            foodImageMap.put("미트볼", R.drawable.meatball);
            foodImageMap.put("크림 스프", R.drawable.cream_soup);
            foodImageMap.put("에그 베네딕트", R.drawable.eggs_benedict);
            foodImageMap.put("리코타 치즈 샐러드", R.drawable.ricotta_cheese_salad);
            foodImageMap.put("데리야키 치킨", R.drawable.teriyaki_chicken);
            foodImageMap.put("돈코츠 라멘", R.drawable.tonkotsu_ramen);
            foodImageMap.put("카레라이스", R.drawable.curry_rice);
            foodImageMap.put("가라아게", R.drawable.karaage);
            foodImageMap.put("야키토리", R.drawable.yakitori);
            foodImageMap.put("오뎅", R.drawable.oden);
            foodImageMap.put("스시롤", R.drawable.sushi_roll);
            foodImageMap.put("샤부샤부", R.drawable.shabu_shabu);
            foodImageMap.put("나베", R.drawable.nabe);
            foodImageMap.put("모찌", R.drawable.mochi);
            foodImageMap.put("핫아메리카노", R.drawable.hot_americano);
            foodImageMap.put("아포가토", R.drawable.affogato);
            foodImageMap.put("카페모카", R.drawable.cafe_mocha);
            foodImageMap.put("아이스바닐라라떼", R.drawable.iced_vanilla_latte);
            foodImageMap.put("콜드브루", R.drawable.cold_brew);
            foodImageMap.put("더치커피", R.drawable.dutch_coffee);
            foodImageMap.put("레몬에이드", R.drawable.lemonade);
            foodImageMap.put("플랫화이트", R.drawable.flat_white);
            foodImageMap.put("블루베리스무디", R.drawable.blueberry_smoothie);
            foodImageMap.put("그린티라떼", R.drawable.green_tea_latte);
            foodImageMap.put("닭한마리", R.drawable.dak_hanmari);
            foodImageMap.put("김치볶음밥", R.drawable.kimchi_fried_rice);
            foodImageMap.put("쭈꾸미볶음", R.drawable.jjukkumi_bokkeum);
            foodImageMap.put("도토리묵무침", R.drawable.dotori_muk_muchim);
            foodImageMap.put("꼬리곰탕", R.drawable.kkori_gomtang);
            foodImageMap.put("부대찌개", R.drawable.budae_jjigae);
            foodImageMap.put("설렁탕", R.drawable.seolleongtang);
            foodImageMap.put("해물순두부", R.drawable.haemul_sundubu);
            foodImageMap.put("콩국수", R.drawable.kongguksu);
            foodImageMap.put("매운탕", R.drawable.maeuntang);
            foodImageMap.put("난자완스", R.drawable.nanjawanseu);
            foodImageMap.put("짬뽕밥", R.drawable.jjambbongbap);
            foodImageMap.put("잡채밥", R.drawable.japchaebap);
            foodImageMap.put("멸치볶음", R.drawable.myeolchi_bokkeum);
            foodImageMap.put("새우튀김", R.drawable.saewoo_twigim);
            foodImageMap.put("칠리새우", R.drawable.chili_shrimp);
            foodImageMap.put("깐쇼새우", R.drawable.gan_shao_shrimp);
            foodImageMap.put("고추잡채밥", R.drawable.gochu_japchae_rice);
            foodImageMap.put("군만두", R.drawable.gun_mandu);
            foodImageMap.put("마라샹궈", R.drawable.mala_xiangguo);
            foodImageMap.put("베이컨 치즈버거", R.drawable.bacon_cheeseburger);
            foodImageMap.put("카프레제 샐러드", R.drawable.caprese_salad);
            foodImageMap.put("로스트 치킨", R.drawable.roast_chicken);
            foodImageMap.put("비프 스튜", R.drawable.beef_stew);
            foodImageMap.put("시저 샐러드", R.drawable.caesar_salad);
            foodImageMap.put("마르게리타 피자", R.drawable.margherita_pizza);
            foodImageMap.put("칠리 콘 카르네", R.drawable.chili_con_carne);
            foodImageMap.put("펜네 알라 보드카", R.drawable.penne_alla_vodka);
            foodImageMap.put("비프 타코", R.drawable.beef_taco);
            foodImageMap.put("햄 앤 치즈 샌드위치", R.drawable.ham_and_cheese_sandwich);
            foodImageMap.put("타코스", R.drawable.tacos);
            foodImageMap.put("참치마요 덮밥", R.drawable.tuna_mayo_rice);
            foodImageMap.put("규동", R.drawable.gyudon);
            foodImageMap.put("에비후라이", R.drawable.ebi_fry);
            foodImageMap.put("야채튀김", R.drawable.vegetable_tempura);
            foodImageMap.put("아게다시도후", R.drawable.agedashi_tofu);
            foodImageMap.put("쇼유라멘", R.drawable.shoyu_ramen);
            foodImageMap.put("비프야끼", R.drawable.beef_yaki);
            foodImageMap.put("사시미", R.drawable.sashimi);
            foodImageMap.put("타코야키", R.drawable.takoyaki);
            foodImageMap.put("더치라떼", R.drawable.dutch_latte);
            foodImageMap.put("카라멜 마키아토", R.drawable.caramel_macchiato);
            foodImageMap.put("아이스 카페모카", R.drawable.iced_cafe_mocha);
            foodImageMap.put("코코아", R.drawable.cocoa);
            foodImageMap.put("아이스 드립 커피", R.drawable.iced_drip_coffee);
            foodImageMap.put("망고 주스", R.drawable.mango_juice);
            foodImageMap.put("딸기 스무디", R.drawable.strawberry_smoothie);
            foodImageMap.put("초콜릿 프라푸치노", R.drawable.chocolate_frappuccino);
            foodImageMap.put("바닐라 셰이크", R.drawable.vanilla_shake);
            foodImageMap.put("낙지볶음", R.drawable.nakji_bokkeum);
            foodImageMap.put("콩나물밥", R.drawable.kongnamul_bap);
            foodImageMap.put("호박죽", R.drawable.pumpkin_porridge);
            foodImageMap.put("감자전", R.drawable.potato_pancake);
            foodImageMap.put("홍어찜", R.drawable.hongeo_jjim);
            foodImageMap.put("두부조림", R.drawable.dubu_jorim);
            foodImageMap.put("물냉면", R.drawable.mul_naengmyeon);
            foodImageMap.put("백김치", R.drawable.baek_kimchi);
            foodImageMap.put("제육볶음", R.drawable.jeyuk_bokkeum);
            foodImageMap.put("오징어볶음", R.drawable.ojingeo_bokkeum);
            foodImageMap.put("깐풍새우", R.drawable.ganpung_saewoo);
            foodImageMap.put("어향동고", R.drawable.yuxiang_donggu);
            foodImageMap.put("해물짬뽕", R.drawable.haemul_jjambbong);
            foodImageMap.put("소고기쌀국수", R.drawable.beef_pho);
            foodImageMap.put("중국냉면", R.drawable.chinese_cold_noodles);
            foodImageMap.put("베이징덕", R.drawable.peking_duck);
            foodImageMap.put("소고기탕면", R.drawable.beef_tangmyeon);
            foodImageMap.put("파인애플볶음밥", R.drawable.pineapple_fried_rice);
            foodImageMap.put("마파두부덮밥", R.drawable.mapo_tofu_rice);
            foodImageMap.put("사천식볶음", R.drawable.sichuan_stir_fry);
            foodImageMap.put("크로크무슈", R.drawable.croque_monsieur);
            foodImageMap.put("치킨케밥", R.drawable.chicken_kebab);
            foodImageMap.put("치킨 알프레도", R.drawable.chicken_alfredo);
            foodImageMap.put("블루치즈버거", R.drawable.blue_cheese_burger);
            foodImageMap.put("볼로네제 파스타", R.drawable.bolognese_pasta);
            foodImageMap.put("무스 비프", R.drawable.mousse_beef);
            foodImageMap.put("칠리 도그", R.drawable.chili_dog);
            foodImageMap.put("트리플 치즈 피자", R.drawable.triple_cheese_pizza);
            foodImageMap.put("포크 찹", R.drawable.pork_chop);
            foodImageMap.put("구운 채소 샐러드", R.drawable.roasted_vegetable_salad);
            foodImageMap.put("연어롤", R.drawable.salmon_roll);
            foodImageMap.put("우나기 덮밥", R.drawable.unagi_don);
            foodImageMap.put("치킨 가라아게", R.drawable.chicken_karaage);
            foodImageMap.put("니꾸자가", R.drawable.nikujaga);
            foodImageMap.put("에비카츠", R.drawable.ebi_katsu);
            foodImageMap.put("카키아게", R.drawable.kakiage);
            foodImageMap.put("미소라멘", R.drawable.miso_ramen);
            foodImageMap.put("테판야끼", R.drawable.teppanyaki);
            foodImageMap.put("고로케", R.drawable.korokke);
            foodImageMap.put("오차즈케", R.drawable.ochazuke);
            foodImageMap.put("아몬드 밀크", R.drawable.almond_milk);
            foodImageMap.put("헤이즐넛 라떼", R.drawable.hazelnut_latte);
            foodImageMap.put("마차 라떼", R.drawable.matcha_latte);
            foodImageMap.put("화이트 초콜릿 모카", R.drawable.white_chocolate_mocha);
            foodImageMap.put("얼그레이 티", R.drawable.earl_grey_tea);
            foodImageMap.put("핫 레몬차", R.drawable.hot_lemon_tea);
            foodImageMap.put("카라멜 프라푸치노", R.drawable.caramel_frappuccino);
            foodImageMap.put("복숭아 아이스티", R.drawable.peach_iced_tea);
            foodImageMap.put("자몽 에이드", R.drawable.grapefruit_ade);
            foodImageMap.put("요거트 스무디", R.drawable.yogurt_smoothie);
            foodImageMap.put("에너지 드링크", R.drawable.energy_drink);

            db.beginTransaction();
            try {
                String sql = "INSERT INTO food (food_name, food_img, calorie, carbs, protein, fat, mood_tag, weather_tag, category_name, is_low_carbon) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                SQLiteStatement statement = db.compileStatement(sql);

                for (int i = 0; i < foods.length; i++) {
                    String food = foods[i];
                    String foodName = extractFoodName(food);
                    Integer imageResourceId = foodImageMap.get(foodName);

                    if (imageResourceId != null) {
                        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imageResourceId);
                        String imageName = foodName + ".png";
                        boolean isSaved = ImageUtils.saveBitmapToFile(context, bitmap, imageName);

                        statement.clearBindings();
                        statement.bindString(1, foodName);
                        statement.bindString(2, isSaved ? imageName : ""); // 이미지 파일 이름 저장

                        String[] parts = food.replace("(", "").replace(")", "").split(",");
                        statement.bindDouble(3, Double.parseDouble(parts[2].trim()));
                        statement.bindDouble(4, Double.parseDouble(parts[3].trim()));
                        statement.bindDouble(5, Double.parseDouble(parts[4].trim()));
                        statement.bindDouble(6, Double.parseDouble(parts[5].trim()));
                        statement.bindString(7, parts[7].trim().replace("'", "")); 
                        statement.bindString(8, parts[8].trim().replace("'", ""));
                        statement.bindString(9, parts[6].trim().replace("'", ""));

                        // 저탄소 메뉴 여부 설정
                        boolean isLowCarbon = determineIfLowCarbon(foodName);
                        statement.bindLong(10, isLowCarbon ? 1 : 0);

                        statement.executeInsert();
                    } else {
                        log("이미지 리소스를 찾을 수 없음: " + foodName);
                    }

                    // 진행 상황 업데이트
                    publishProgress((int) ((i / (float) foods.length) * 100));
                }

                db.setTransactionSuccessful();
            } catch (Exception e) {
                log("insertInitialData 중 오류 발생: " + e.getMessage());
            } finally {
                db.endTransaction();
            }
            return null;
        }

        private boolean determineIfLowCarbon(String foodName) {
            // 저탄소 메뉴 목록
            String[] lowCarbonFoods = new String[]{
                    "샐러드", "야채튀김", "야채", "과일", "콩나물국", "도토리묵", "김밥",
                    "콩국수", "나베", "백김치", "물냉면", "호박죽", "오이무침", "미역국",
                    "두부조림", "감자전", "비빔밥", "김치볶음밥", "매운탕", "청국장",
                    "북엇국", "파인애플볶음밥"
            };

            // 저탄소 메뉴 목록에 있는지 확인
            for (String lowCarbonFood : lowCarbonFoods) {
                if (foodName.contains(lowCarbonFood)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int progress = values[0];
            Toast.makeText(context, "진행 상황: " + progress + "% 완료", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(context, "데이터 삽입 완료", Toast.LENGTH_SHORT).show();
        }

        private String extractFoodName(String foodString) {
            return foodString.split("'")[1];
        }

        private void log(String data) {
            Log.d("InsertDataAsyncTask", data);
        }
    }

    public UserNutritionalInfo getLoggedInUserNutritionalInfo() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT exercise_type, daily_calorie, daily_carbs, daily_protein, daily_fat, current_calorie, current_carbs, current_protein, current_fat FROM user WHERE login_check = 1 LIMIT 1", null);
        UserNutritionalInfo userNutritionalInfo = null;
        if (cursor.moveToFirst()) {
            String exerciseType = cursor.getString(cursor.getColumnIndexOrThrow("exercise_type"));
            double dailyCalorie = cursor.getDouble(cursor.getColumnIndexOrThrow("daily_calorie"));
            double dailyCarbs = cursor.getDouble(cursor.getColumnIndexOrThrow("daily_carbs"));
            double dailyProtein = cursor.getDouble(cursor.getColumnIndexOrThrow("daily_protein"));
            double dailyFat = cursor.getDouble(cursor.getColumnIndexOrThrow("daily_fat"));
            double currentCalorie = cursor.getDouble(cursor.getColumnIndexOrThrow("current_calorie"));
            double currentCarbs = cursor.getDouble(cursor.getColumnIndexOrThrow("current_carbs"));
            double currentProtein = cursor.getDouble(cursor.getColumnIndexOrThrow("current_protein"));
            double currentFat = cursor.getDouble(cursor.getColumnIndexOrThrow("current_fat"));

            userNutritionalInfo = new UserNutritionalInfo(exerciseType, dailyCalorie, dailyCarbs, dailyProtein, dailyFat, currentCalorie, currentCarbs, currentProtein, currentFat);
        }
        cursor.close();
        return userNutritionalInfo;
    }

//    public Cursor getRandomFoodItems() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        return db.rawQuery("SELECT * FROM food ORDER BY RANDOM() LIMIT 7", null);
//    }

    //검색기록 추가 및 업데이트메서드
    public void insertOrUpdateSearchRecord(String userId, String searchTerm) {
        SQLiteDatabase db = this.getWritableDatabase();

        // 기존 검색 기록이 있는지 확인
        Cursor cursor = db.query("search",
                new String[] {"search_count"},
                "user_id=? AND search_term=?",
                new String[] {userId, searchTerm},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            // 기존 기록이 있는 경우 검색 횟수 업데이트
            int searchCount = cursor.getInt(cursor.getColumnIndex("search_count")) + 1;
            ContentValues values = new ContentValues();
            values.put("search_count", searchCount);
            values.put("search_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

            db.update("search", values, "user_id=? AND search_term=?", new String[]{userId, searchTerm});
        } else {
            // 기존 기록이 없는 경우 새 기록 추가
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("search_term", searchTerm);
            values.put("search_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
            values.put("search_count", 1);

            db.insert("search", null, values);
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();
    }


    private static final String[] CATEGORIES = {"한식", "중식", "일식", "양식"};
    // 네 개의 카테고리 중에서 두 개를 랜덤으로 선택하는 메서드
    public List<String> getRandomCategories() {
        List<String> categoryList = new ArrayList<>();
        Collections.addAll(categoryList, CATEGORIES);
        Collections.shuffle(categoryList);
        return categoryList.subList(0, 2);
    }

    // 특정 카테고리의 랜덤 음식 메뉴를 가져오는 메서드
    public Cursor getRandomFoodItemsByCategory(String categoryName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM food WHERE category_name = ? ORDER BY RANDOM() LIMIT 4", new String[]{categoryName});
    }

    // 두 개의 랜덤 카테고리를 선택하고, 각 카테고리에서 4개의 랜덤 음식을 가져오는 메서드
    public List<Cursor> getRandomFoodItems() {
        List<String> randomCategories = getRandomCategories();
        List<Cursor> randomFoodItems = new ArrayList<>();
        for (String category : randomCategories) {
            randomFoodItems.add(getRandomFoodItemsByCategory(category));
        }
        return randomFoodItems;
    }

    // 카페 카테고리에서 랜덤으로 4개의 항목을 가져오는 메서드
    public Cursor getRandomCafeItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM food WHERE category_name = '카페' ORDER BY RANDOM() LIMIT 4", null);
    }

    // 사용자 맞춤 메뉴 추천을 위한 메서드
    public List<FoodItem> getRecommendedFoodItems(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<FoodItem> recommendedFoodItems = new ArrayList<>();

        String userQuery = "SELECT daily_calorie, daily_carbs, daily_protein, daily_fat, " +
                "current_calorie, current_carbs, current_protein, current_fat " +
                "FROM user WHERE user_id = ?";
        Cursor userCursor = db.rawQuery(userQuery, new String[]{userId});

        if (userCursor != null && userCursor.moveToFirst()) {
            double dailyCalorie = userCursor.getDouble(userCursor.getColumnIndex("daily_calorie"));
            double dailyCarbs = userCursor.getDouble(userCursor.getColumnIndex("daily_carbs"));
            double dailyProtein = userCursor.getDouble(userCursor.getColumnIndex("daily_protein"));
            double dailyFat = userCursor.getDouble(userCursor.getColumnIndex("daily_fat"));

            double currentCalorie = userCursor.getDouble(userCursor.getColumnIndex("current_calorie"));
            double currentCarbs = userCursor.getDouble(userCursor.getColumnIndex("current_carbs"));
            double currentProtein = userCursor.getDouble(userCursor.getColumnIndex("current_protein"));
            double currentFat = userCursor.getDouble(userCursor.getColumnIndex("current_fat"));

            double remainingCalorie = dailyCalorie - currentCalorie;
            double remainingCarbs = dailyCarbs - currentCarbs;
            double remainingProtein = dailyProtein - currentProtein;
            double remainingFat = dailyFat - currentFat;

            userCursor.close();

            String foodQuery = "SELECT food_name, calorie, carbs, protein, fat, category_name " +
                    "FROM food " +
                    "ORDER BY ABS((calorie - ?)) + ABS((carbs - ?)) + ABS((protein - ?)) + ABS((fat - ?)) ASC " +
                    "LIMIT 4";
            Cursor foodCursor = db.rawQuery(foodQuery, new String[]{
                    String.valueOf(remainingCalorie),
                    String.valueOf(remainingCarbs),
                    String.valueOf(remainingProtein),
                    String.valueOf(remainingFat)
            });

            if (foodCursor != null && foodCursor.moveToFirst()) {
                do {
                    String foodName = foodCursor.getString(foodCursor.getColumnIndex("food_name"));
                    double calorie = foodCursor.getDouble(foodCursor.getColumnIndex("calorie"));
                    double carbs = foodCursor.getDouble(foodCursor.getColumnIndex("carbs"));
                    double protein = foodCursor.getDouble(foodCursor.getColumnIndex("protein"));
                    double fat = foodCursor.getDouble(foodCursor.getColumnIndex("fat"));
                    String categoryName = foodCursor.getString(foodCursor.getColumnIndex("category_name"));

                    recommendedFoodItems.add(new FoodItem(foodName, calorie, carbs, protein, fat, categoryName));
                } while (foodCursor.moveToNext());
                foodCursor.close();
            }
        }

        return recommendedFoodItems;
    }

    public void insertMealLog(SQLiteDatabase db, String userId, String mealDate, String mealTime, int foodNum) {
        String insertQuery = "INSERT INTO meal_log (user_id, meal_date, meal_time, food_num) VALUES (?, ?, ?, ?)";
        db.execSQL(insertQuery, new Object[]{userId, mealDate, mealTime, foodNum});
    }

    public void updateUserIntake(SQLiteDatabase db, String userId, int calorie, int carbs, int protein, int fat) {
        String updateQuery = "UPDATE user SET " +
                "current_calorie = current_calorie + ?, " +
                "current_carbs = current_carbs + ?, " +
                "current_protein = current_protein + ?, " +
                "current_fat = current_fat + ? " +
                "WHERE user_id = ?";
        db.execSQL(updateQuery, new Object[]{calorie, carbs, protein, fat, userId});
    }

    public Cursor getUserInfo(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM user WHERE user_id = ?";
        return db.rawQuery(query, new String[]{userId});
    }

    public Cursor getLowCarbonFoodItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM food WHERE is_low_carbon = 1 ORDER BY RANDOM() LIMIT 4";
        return db.rawQuery(query, null);
    }

    public Cursor getFoodByTag(String tagType, String tagValue) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        if ("mood".equals(tagType)) {
            cursor = db.rawQuery("SELECT * FROM food WHERE mood_tag = ? LIMIT 1", new String[]{tagValue});
        } else if ("weather".equals(tagType)) {
            cursor = db.rawQuery("SELECT * FROM food WHERE weather_tag = ? LIMIT 1", new String[]{tagValue});
        }

        return cursor;
    }

}