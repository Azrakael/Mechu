package com.example.mechu_project;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_WRITE_STORAGE = 2;

    LinearLayout calender, description;
    Button modify_myprofile, logout;
    TextView mytype, mytype1, myname, loveitCount, viewLikedFoods;
    private String userId;
    private Spinner goalSpinner;
    ImageView backbutton, logoImage;
    CircleImageView profileImage;
    LinearLayout linearLayoutContainer;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        modify_myprofile = findViewById(R.id.modify_myprofile);
        //calender = findViewById(R.id.calender);
        logout = findViewById(R.id.logout);
        description = findViewById(R.id.description);
        viewLikedFoods = findViewById(R.id.loveit);
        mytype = findViewById(R.id.mytype);
        mytype1 = findViewById(R.id.mytype1);
        myname = findViewById(R.id.myname);
        loveitCount = findViewById(R.id.loveit_count);
        logoImage = findViewById(R.id.logoImage);
        backbutton = findViewById(R.id.backButton);
        profileImage = findViewById(R.id.profileImage);
        linearLayoutContainer = findViewById(R.id.linearLayoutContainer);
        dbHelper = new DatabaseHelper(this);

        logoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Profile.this, MainActivity.class);
                startActivity(it);
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Profile.this, MainActivity.class);
                startActivity(it);
            }
        });

        // SharedPreferences에서 사용자 ID
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = preferences.getString("user_id", null);

        // SharedPreferences에서 사용자 이름
        String userName = preferences.getString("user_name", "아아아");
        myname.setText(userName);

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT exercise_type, profile_img FROM user WHERE user_id = ?",
                new String[]{userId});

        if (cursor.moveToFirst()) {
            int exerciseTypeIndex = cursor.getColumnIndex("exercise_type");
            int profileImgIndex = cursor.getColumnIndex("profile_img");
            String exerciseType = cursor.getString(exerciseTypeIndex);
            String profileImgPath = cursor.getString(profileImgIndex);

            mytype.setText(exerciseType);
            if (exerciseType.equals("일반")) {
                mytype1.setVisibility(View.GONE);
            } else {
                mytype1.setVisibility(View.VISIBLE);
            }

            if (profileImgPath != null && !profileImgPath.isEmpty()) {
                File imgFile = new File(profileImgPath);
                if (imgFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    profileImage.setImageBitmap(bitmap);
                }
            }
        }
        cursor.close();

         updateLikeCount();

        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Profile.this, Description.class);
                startActivity(it);
            }
        });

        modify_myprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Profile.this, ModifyProfile.class);
                startActivity(it);
            }
        });

 //       calender.setOnClickListener(new View.OnClickListener() {
 //                @Override
 //           public void onClick(View view) {
 //               Intent it = new Intent(Profile.this, Calender.class);
 //               startActivity(it);
 //           }
 //       });

        Button logoutButton = findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("user_id");
                editor.putBoolean("logged_in", false);
                editor.apply();

                Toast.makeText(Profile.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Profile.this, Introductory.class);
                startActivity(intent);
                finish();
            }
        });

        // 이미지 클릭 시 사진 설정
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 외부 저장소 쓰기 권한이 있는지 확인
                if (ContextCompat.checkSelfPermission(Profile.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // 권한 요청
                    ActivityCompat.requestPermissions(Profile.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_WRITE_STORAGE);
                } else {
                    // 권한이 이미 부여된 경우
                    openImageChooser();
                }
            }
        });

        // 좋아요된 음식 데이터 가져오기 및 다른 액티비티로 전송
        viewLikedFoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor likedFoodsCursor = dbHelper.getLikedFoods(userId);
                ArrayList<String> likedFoods = new ArrayList<>();

                while (likedFoodsCursor.moveToNext()) {
                    String foodName = likedFoodsCursor.getString(likedFoodsCursor.getColumnIndex("food_name"));
                    likedFoods.add(foodName);
                }
                likedFoodsCursor.close();

                Intent intent = new Intent(Profile.this, LikeList.class);
                intent.putStringArrayListExtra("liked_foods", likedFoods);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 좋아요 수 업데이트
        updateLikeCount();
    }

    private void updateLikeCount() {
        int likeCount = dbHelper.getLikeCount(userId);
        loveitCount.setText(String.valueOf(likeCount));
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            String imagePath = saveImageToExternalStorage(imageUri);

            if (imagePath != null) {
                saveProfileImagePath(imagePath);

                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                profileImage.setImageBitmap(bitmap);

                // 이미지가 추가되었음을 알리는 토스트 메시지
                Toast.makeText(this, "이미지가 추가되었습니다!", Toast.LENGTH_SHORT).show();

                // Profile 액티비티를 다시 시작하여 변경 사항을 반영
                Intent intent = new Intent(Profile.this, Profile.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            }
        }
    }

    private String saveImageToExternalStorage(Uri imageUri) {
        String imagePath = null;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "MechuImages");

            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }

            String fileName = "profile_image_" + System.currentTimeMillis() + ".jpg";
            File file = new File(storageDir, fileName);

            try (OutputStream out = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                imagePath = file.getAbsolutePath();
            }

        } catch (IOException e) {
            Log.e("Profile", "Error saving image to external storage", e);
        }

        return imagePath;
    }

    private void saveProfileImagePath(String imagePath) {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userId = preferences.getString("user_id", null);

        if (userId != null) {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.updateUserProfileImage(userId, imagePath);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageChooser();
            } else {
                Toast.makeText(this, "외부 저장소 쓰기 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
