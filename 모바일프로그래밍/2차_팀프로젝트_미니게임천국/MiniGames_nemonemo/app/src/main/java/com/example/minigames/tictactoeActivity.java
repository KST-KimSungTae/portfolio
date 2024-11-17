package com.example.minigames;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class tictactoeActivity extends Activity {

    //이미지 버튼의 주소를 넣을 변수 선언
    private ImageButton[][] TicBtn = new ImageButton[3][3];

    Integer[][] T_btn={
            {R.id.Tbtn00,R.id.Tbtn10,R.id.Tbtn20},
            {R.id.Tbtn01,R.id.Tbtn11,R.id.Tbtn21},
            {R.id.Tbtn02,R.id.Tbtn12,R.id.Tbtn22},
    };
    //틱택토 상황을 나타낼 틱택토 맵  N= 이 칸에는 아무 것도 없다. O= 이 칸에는 O가 그려져 있다. X= 이 칸에는 X가 그려져 있다.
    String[][] TicTacToeMap ={
            {"N","N","N"},
            {"N","N","N"},
            {"N","N","N"}
    };
    //틱택토 이미지의 주소를 모아둔 변수
//0번=N 아무것도 없음 //1번=O O가 그려짐 // 2번=X X가 그려짐
    Integer[] TicTacToeImage={R.drawable.tictactoeboard,R.drawable.tictactoeboardo,R.drawable.tictactoeboardx};

    Integer PlayerTurn=0;   //0이면 O의 턴, 1이면 X의 턴이다.

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tictactoe_game);

//이미지 버튼들에 대한 이벤트 만들기
        for(int i=0;i<3;i++)
        {
            for(int j=0;j<3;j++)
            {
                TicBtn[j][i]=(ImageButton) findViewById(T_btn[j][i]);
                TicBtn[j][i].setOnClickListener(TbtnListener);
            }
        }

        MapUpdate();
    }

    //버튼이 눌렸을때 발생하는 이벤트
    private final View.OnClickListener TbtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            //현재 발생한 이벤트가 어느 버튼이 클릭되어 생긴지 탐색한다.
            int colum = 0, row = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (view.getId() == T_btn[j][i])     //얻은 ID가 T_btn의 아이디와 같다면
                    {
                        colum = i;      //열 : 세로
                        row = j;        //행 : 가로   저장
                    }
                }
            }

            if(TicTacToeMap[row][colum]=="N")   //이 칸이 빈칸일 경우에만
            {
                if(PlayerTurn==0)   //O의 차례일때
                {
                    TicTacToeMap[row][colum]="O";
                    PlayerTurn=1;
                }
                else                //X의 차례일때
                {
                    TicTacToeMap[row][colum]="X";
                    PlayerTurn=0;
                }
            }

            //TicTacToeMap의  저장된 맵에 따라 맵 구현
            MapUpdate();
            //3개 연속 같은 모양 (승리)이 있는지 확인
            VictoryCheck(row,colum);

        }
    };

    //TicTacToeMap의 내용에 따라 승자를 가린다.(3개 연속 이어지는 같은 모양)
    private void VictoryCheck(int row,int colum)    //모든 칸을 검사할 필요 없이 새로 모양이 추가된 칸에 의해 새로 만들어진 빙고가 있는지 검사한다.
    {
        int CellStack=0;    //이 숫자가 3이 되면 빙고
        String CellShape=TicTacToeMap[row][colum];  //현재 칸의 모양이 뭔지 저장한다.
//가로세로 빙고 검사
//가로 빙고 검사
        for(int i=0;i<3;i++)
        {
            if(TicTacToeMap[i][colum]==CellShape)
            {
                CellStack++;
            }
        }
        if(CellStack==3)
        {
//게임 승리
            Victory(CellShape);
        }
        else
        {
            CellStack=0;    //스택 초기화
        }

//세로 빙고 검사
        for(int i=0;i<3;i++)
        {
            if(TicTacToeMap[row][i]==CellShape)
            {
                CellStack++;
            }
        }
        if(CellStack==3)
        {
            //게임 승리
            Victory(CellShape);
        }
        else
        {
            CellStack=0;
        }

//대각선 빙고 검사
        int temp=row*10+colum;  //현재 칸의 위치를 하나의 int로 나타낼수 있게 하나에 10을 곱해 변수에 넣는다.
        if(temp==0||temp==20||temp==11||temp==2||temp==22)  //대각선을 만들 수 없는 10,01, 21, 12 을 제외한다.
        {
            //00에서 22로 가는 방향의 대각선 빙고 검사
            for(int i=0;i<3;i++)
            {
                if(TicTacToeMap[i][i]==CellShape)
                {
                    CellStack++;
                }
            }
            if(CellStack==3)
            {
                //게임 승리
                Victory(CellShape);
            }
            else
            {
                CellStack=0;
            }

            //02에서 20으로 가는 대각선 검사
            for(int i=0;i<3;i++)
            {
                if(TicTacToeMap[i][2-i]==CellShape)
                {
                    CellStack++;
                }
            }
            if(CellStack==3)
            {
                //게임 승리
                Victory(CellShape);
            }
            else
            {
                CellStack=0;
            }
        }
    }

    //게임 승리 명령어
    private void Victory(String winner)
    {
// 승리 시 다이얼로그 띄우기
        final Dialog dialog1 = new Dialog(tictactoeActivity.this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.activity_victory);

        TextView winnerText = dialog1.findViewById(R.id.winnerText);
        if(winner =="무승부")
        {
            winnerText.setText(winner + "하였습니다!");
        }
        else {
            winnerText.setText(winner + "이(가) 승리했습니다!");
        }

        Button mainButton = dialog1.findViewById(R.id.mainButton);
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
                finish();
            }
        });

        Button retryButton = dialog1.findViewById(R.id.retryButton);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();

                // 새로운 게임을 시작하는 인텐트 생성
                Intent intent = new Intent(tictactoeActivity.this, tictactoeActivity.class);
                startActivity(intent);
                finish(); // 현재의 게임 액티비티를 종료하여 새로운 게임을 시작합니다.
            }
        });

        dialog1.show();
    }

    //TicTacToeMap에 따라 이미지버튼의 그림을 업데이트 하는 명령어
    private void MapUpdate()
    {
        // 이미지 버튼 업데이트
        for(int i=0;i<3;i++)
        {
            for(int j=0;j<3;j++)
            {
                if(TicTacToeMap[j][i]=="N") //아무 것도 없는 경우
                {
                    TicBtn[j][i].setImageResource(TicTacToeImage[0]);
                }
                else if(TicTacToeMap[j][i]=="O")  //O가 있는 경우
                {
                    TicBtn[j][i].setImageResource(TicTacToeImage[1]);
                }
                else //X인 경우
                {
                    TicBtn[j][i].setImageResource(TicTacToeImage[2]);
                }
            }
        }

        // 플레이어의 차례에 따라 텍스트 업데이트
        TextView Otext = findViewById(R.id.Otext);
        TextView Xtext = findViewById(R.id.Xtext);

        if(PlayerTurn == 0) // O의 차례
        {
            Otext.setVisibility(View.VISIBLE);
            Xtext.setVisibility(View.INVISIBLE);
        }
        else // X의 차례
        {
            Xtext.setVisibility(View.VISIBLE);
            Otext.setVisibility(View.INVISIBLE);
        }
    }

}