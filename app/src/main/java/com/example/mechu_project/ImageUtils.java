package com.example.mechu_project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {
    private static final String TAG = "ImageUtils";

    public static File getImageFile(Context context, String imageName) {
        File directory = new File(context.getFilesDir(), "images");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return new File(directory, imageName);
    }

    public static boolean saveBitmapToFile(Context context, Bitmap bitmap, String imageName) {
        File file = getImageFile(context, imageName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, fos); // 압축 품질 조절
            Log.d(TAG, "이미지가 성공적으로 저장되었습니다: " + imageName);
            return true;
        } catch (IOException e) {
            Log.e(TAG, "이미지 저장 실패: " + imageName, e);
            return false;
        }
    }

    public static Bitmap loadScaledBitmapFromFile(Context context, String imageName, int reqWidth, int reqHeight) {
        File file = getImageFile(context, imageName);
        if (!file.exists()) {
            Log.e(TAG, "이미지 파일이 존재하지 않습니다: " + imageName);
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        if (bitmap != null) {
            Log.d(TAG, "이미지가 성공적으로 축소되어 로드되었습니다: " + imageName);
        } else {
            Log.e(TAG, "이미지 축소 로드 실패: " + imageName);
        }
        return bitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

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