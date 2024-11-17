package com.example.minigames;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    Button btnStart, btnQuit;

    Button btnTest;
    ImageView test1;
    String TestStr = "O";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = (Button) findViewById(R.id.btnStart);
        btnQuit = (Button) findViewById(R.id.btnQuit);


        /////////////////////////////////////


        //플레이할 수 있는 게임의 목록
        final String[] Games = new String[]{"체스", "오목", "오셀로", "틱택토"};

        //게임 시작하기 (게임 선택 부터 시작)
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //선택한 게임이 뭔지 저장할 int 변수
                int[] GameChoice = {0};

                //대화상자 사용하기 위한 builder 선언
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                //대화상자의 제목 정하기
                builder.setTitle("게임 선택");
                builder.setIcon(R.mipmap.ic_launcher_ok);
                //대화상자 속 라디오버튼 만들기 위한 부분 (라디오버튼의 선택지가 될 문자열 배열, 초기선택번호(-1은 아무것도 선택 안 된 것), 선택에 반응할 리스너)
                builder.setSingleChoiceItems(Games, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //라디오버튼의 선택이 바뀌면 현재 선택을 변수에 저장한다.
                        GameChoice[0] = i;
                    }
                });

                //대화상자의 선택, 닫기라는 버튼을 추가한다.
                builder.setPositiveButton("닫기", null);
                //대화상자의 선택 버튼을 클릭했을 때, 현재 선택된 게임(GameChoice의 값)에 따라 새로운 창을 띄운다.
                builder.setNegativeButton("선택", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (GameChoice[0] == 0)    //체스 선택
                        {
                            Intent intent1 = new Intent(getApplicationContext(), chessActivity.class);
                            startActivity(intent1);
                        } else if (GameChoice[0] == 1)   //오목 선택
                        {
                            Intent intent2 = new Intent(getApplicationContext(), omokActivity.class);
                            startActivity(intent2);

                        } else if (GameChoice[0] == 2)   //오셀로 선택
                        {
                            Intent intent3 = new Intent(getApplicationContext(), oshelloActivity.class);
                            startActivity(intent3);

                        } else if (GameChoice[0] == 3)   //틱택토 선택
                        {
                            Intent intent4 = new Intent(getApplicationContext(), tictactoeActivity.class);
                            startActivity(intent4);
                        }
                    }
                });
                //대화상자 생성하기
                AlertDialog dialog = builder.create();
                //대화상자 보여주기
                builder.show();
            }
        });

        //게임 끄기
        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void testCheck() {
        if (TestStr == "O") {
            test1.setBackgroundResource(R.color.white);
            TestStr = "U";
        } else {
            test1.setBackgroundResource(R.color.black);
            TestStr = "O";
        }
    }
}