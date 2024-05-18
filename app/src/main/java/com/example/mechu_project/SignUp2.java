package com.example.mechu_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Random;



//이메일 인증부분
public class SignUp2 extends AppCompatActivity {


    EditText editTextId, editPasswd, editEmail; // 내가 작성한 아이디, 패스워드, 이메일 저장 edittext
    EditText validEmail; // 인증 코드를 입력받을 EditText
    String verificationCode; // 인증코드 저장
    String userInputCode;//사용자가 입력한 인증 코드를 가져옵니다.
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        dbHelper = new DatabaseHelper(this);

        editTextId = findViewById(R.id.editTextId);
        editPasswd = findViewById(R.id.editPasswd);

        Button signupNext = findViewById(R.id.signupNext);
        editEmail = findViewById(R.id.editEmail); // 사용자의 이메일을 입력받는 필드
        validEmail = findViewById(R.id.validEmail); // 사용자가 인증 코드를 입력하는 필드

        // Sign Up 버튼에 클릭 리스너 설정
        if (signupNext != null) {
            signupNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String user_id = editTextId.getText().toString();
                    String password = editPasswd.getText().toString();
                    String email = editEmail.getText().toString();

                    //모든 조건이 충족되면 페이지 singUp3로
                    if (!user_id.isEmpty() && !password.isEmpty() /*&& verificationCode != null && verificationCode.equals(userInputCode)*/) {
                        //DB에 데이터 삽입
                        insertUser(user_id, password /*, email*/);

                        // SignUp3로
                        Intent intent = new Intent(SignUp2.this, SignUp3.class);
                        intent.putExtra("user_id", user_id);
                        startActivity(intent);
                    } else {
                        showMessage("모든 필드를 입력해주세요.");
                    }
                }
            });
        }

        // 뒤로 가기 버튼 클릭 리스너 설정
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 이메일 확인 버튼 클릭 이벤트
        findViewById(R.id.buttonEmailCheck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipientEmail = editEmail.getText().toString(); // 사용자가 입력한 이메일 주소를 가져옵니다.

                // 인증번호 생성
                verificationCode = generateVerificationCode();

                // 이메일 발송
                new SendMail().execute(recipientEmail, verificationCode);
            }
        });

        // 인증번호 확인 버튼 클릭 이벤트
        findViewById(R.id.buttonValidEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userInputCode = validEmail.getText().toString(); // 사용자가 입력한 인증 코드를 가져옵니다.
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

    // AsyncTask 로 이메일 발송
    private class SendMail extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            final String user_id = "hdidvrnd@naver.com"; // 이메일 계정
            final String password = "sss"; // 이메일 계정의 비밀번호

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.naver.com");      //이메일 서버 호스트 설정
            props.put("mail.smtp.socketFactory.port", "465");       //smtp서버에서 사용할 소켓 팩토리 포트
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //소켓 팩토리 클래스 설정
            props.put("mail.smtp.auth", "true");     //smtp인증
            props.put("mail.smtp.port", "465");         //smtp 서버 포트 설정

            //이메일 전송에 사용되는 세션
            Session session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(user_id, password);
                        }
                    });

            try {
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(user_id, "MECHU Company")); //발신자이름 설정
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(params[0])); //수신자 설정
                message.setSubject("인증번호");
                message.setText("해당 인증번호는 " + params[1] + "입니다.");

                Transport.send(message);
                return true;
            } catch (MessagingException | UnsupportedEncodingException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                showMessage("인증 이메일을 전송했습니다.");
            } else {
                showMessage("이메일 전송에 실패했습니다. 다시 시도해주세요.");
            }
        }
    }

    //아이디,비번,이메일 DB에 삽입하는 메서드
    private void insertUser(String user_id, String password/*, String email*/) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", user_id);
        values.put("password", password);
        //values.put("email", email);

        // 'user' 테이블에 회원 정보 삽입
        long result = db.insert("user", null, values);
        db.close();

        if (result != -1) {
            showMessage("데이터가 성공적으로 입력되었습니다.");
        } else {
            showMessage("데이터 입력에 실패했습니다.");
        }
    }
}
