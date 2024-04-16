package com.example.mechu_project;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // 하트 클릭시 색이 채워지는 에니메이션 추가.효과
    ScaleAnimation scaleAnimation;
    BounceInterpolator bounceInterpolator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 클릭시 하트가 채워지는 부분 지속시간,
        scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator); //바운스 효과
    }

    // 각 버튼의 클릭 이벤트 처리
    public void onFavoriteButtonClick(View view) {
        // 클릭한 버튼에 애니메이션 적용
        view.startAnimation(scaleAnimation);
    }
}
