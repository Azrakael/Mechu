package com.example.mechu_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


//이메일 인증부분
public class SignUp2 extends AppCompatActivity {


    EditText editEmail; // 내가 작성한 이메일 저장 edittext
    EditText validEmail; // 인증 코드를 입력받을 EditText
    String verificationCode; // 인증코드 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        Button signupNext = findViewById(R.id.signupNext);
        editEmail = findViewById(R.id.editEmail); // 사용자의 이메일을 입력받는 필드
        validEmail = findViewById(R.id.validEmail); // 사용자가 인증 코드를 입력하는 필드

        // Sign Up 버튼에 클릭 리스너 설정
        if (signupNext != null) {
            signupNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // SignUp3로
                    Intent intent = new Intent(SignUp2.this, SignUp3.class);
                    startActivity(intent);
                }
            });
        }

        // 이메일 확인 버튼 클릭 이벤트
        findViewById(R.id.buttonEmailCheck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipientEmail = editEmail.getText().toString(); // 사용자가 입력한 이메일 주소를 가져옵니다.
                verificationCode = generateVerificationCode(); // 인증번호 생성
                sendEmail(recipientEmail, verificationCode); // 이메일 발송
            }
        });

        // 인증번호 확인 버튼 클릭 이벤트
        findViewById(R.id.buttonValidEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userInputCode = validEmail.getText().toString(); // 사용자가 입력한 인증 코드를 가져옵니다.
                if (verificationCode != null && verificationCode.equals(userInputCode)) {
                    // 인증 코드가 일치하는 경우
                    showMessage("인증이 성공했습니다.");
                } else {
                    // 인증 코드가 일치하지 않는 경우
                    showMessage("인증이 실패했습니다. 올바른 인증 코드를 입력하세요.");
                }
            }
        });
    }

    // 메시지를 보여주는 메서드
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // 인증번호 생성
    private String generateVerificationCode() {
        StringBuilder temp = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < 6; i++) { // 6자리 숫자 생성
            temp.append(rnd.nextInt(10)); // 0-9 사이의 숫자 추가
        }
        return temp.toString();
    }

    private ExecutorService emailExecutor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    private void sendEmail(String recipient, String verificationCode) {
        emailExecutor.submit(() -> {
            final String username = "hdidvrnd@naver.com"; // 이메일 계정
            final String password = "rightsene1313"; // 이메일 계정의 비밀번호

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.naver.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            try {
                MimeMessage message = new MimeMessage(session);
                try {
                    message.setFrom(new InternetAddress(username, "MECHU Company"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    postResult("발신자 주소 설정 중 에러가 발생했습니다: " + e.getMessage());
                    return;
                }
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
                message.setSubject("인증번호");
                message.setText("해당 인증번호는 " + verificationCode + "입니다.");

                Transport.send(message);
                postResult("인증 이메일을 전송했습니다.");
            } catch (MessagingException e) {
                e.printStackTrace();
                if (e instanceof AuthenticationFailedException) {
                    postResult("인증 오류: 계정 정보를 확인하세요. " + e.getMessage());
                } else if (e instanceof SendFailedException) {
                    postResult("이메일 전송 실패: 수신자 주소를 확인하세요. " + e.getMessage());
                } else {
                    postResult("이메일 전송에 실패했습니다. 오류: " + e.getMessage());
                }
            }
        });
    }

    private void postResult(String message) {
        // UI 업데이트를 위한 메서드 (예: 로그 출력 또는 토스트 메시지 표시)
        handler.post(() -> showMessage(message));
    }
}
