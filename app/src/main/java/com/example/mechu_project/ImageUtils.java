package com.example.mechu_project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import android.util.Log;

public class ImageUtils {
    private static final String TAG = "ImageUtils";

    /**
     * 지정된 이름의 이미지 파일을 나타내는 File 객체를 반환
     * 디렉터리가 존재하지 않는 경우, 디렉터리를 생성
     *
     * @param context 컨텍스트
     * @param imageName 이미지 파일 이름
     * @return File 객체
     */
    public static File getImageFile(Context context, String imageName) {
        // 앱의 파일 디렉터리 내에 "images"라는 디렉터리 생성
        File directory = new File(context.getFilesDir(), "images");
        // 디렉터리가 존재하지 않으면 생성
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // "images" 디렉터리 내에 지정된 이미지 이름을 가진 File 객체 반환
        return new File(directory, imageName);
    }

    /**
     * 비트맵을 지정된 이름의 파일로 저장
     *
     * @param context 컨텍스트
     * @param bitmap 저장할 비트맵
     * @param imageName 이미지 파일 이름
     * @return 비트맵이 성공적으로 저장되었으면 true, 그렇지 않으면 false
     */
    public static boolean saveBitmapToFile(Context context, Bitmap bitmap, String imageName) {
        // 이미지 파일 가져오기
        File file = getImageFile(context, imageName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            // 비트맵을 압축하여 파일에 쓰기
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, fos); // 압축 품질 조절
            Log.d(TAG, "이미지가 성공적으로 저장되었습니다: " + imageName);
            return true;
        } catch (IOException e) {
            // 이미지 저장에 실패한 경우 오류 메시지 로그 기록
            Log.e(TAG, "이미지 저장 실패: " + imageName, e);
            return false;
        }
    }

    /**
     * 지정된 이름의 파일에서 비트맵을 로드
     *
     * @param context 컨텍스트
     * @param imageName 이미지 파일 이름
     * @return 비트맵, 이미지를 로드할 수 없는 경우 null
     */
    public static Bitmap loadBitmapFromFile(Context context, String imageName) {
        // 이미지 파일 가져오기
        File file = getImageFile(context, imageName);
        // 파일이 존재하면, 파일을 비트맵으로 디코딩하여 반환
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            Log.d(TAG, "이미지가 성공적으로 로드되었습니다: " + imageName);
            return bitmap;
        }
        // 이미지를 로드할 수 없는 경우 오류 메시지 로그 기록
        Log.e(TAG, "이미지 로드 실패: " + imageName);
        return null;
    }
}
