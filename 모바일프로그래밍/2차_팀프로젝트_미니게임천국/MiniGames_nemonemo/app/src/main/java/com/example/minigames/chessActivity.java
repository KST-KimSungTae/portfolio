package com.example.minigames;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class chessActivity extends Activity {

    //이미지 버튼의 선언을 저장할 8x8크기의 변수 선언
    private ImageButton[][] ChessBtn = new ImageButton[8][8];
    //버튼의 아이디들이 들어간 int 배열
    Integer[][] R_btn={
            {R.id.btn00,R.id.btn10,R.id.btn20,R.id.btn30,R.id.btn40,R.id.btn50,R.id.btn60,R.id.btn70},
            {R.id.btn01,R.id.btn11,R.id.btn21,R.id.btn31,R.id.btn41,R.id.btn51,R.id.btn61,R.id.btn71},
            {R.id.btn02,R.id.btn12,R.id.btn22,R.id.btn32,R.id.btn42,R.id.btn52,R.id.btn62,R.id.btn72},
            {R.id.btn03,R.id.btn13,R.id.btn23,R.id.btn33,R.id.btn43,R.id.btn53,R.id.btn63,R.id.btn73},
            {R.id.btn04,R.id.btn14,R.id.btn24,R.id.btn34,R.id.btn44,R.id.btn54,R.id.btn64,R.id.btn74},
            {R.id.btn05,R.id.btn15,R.id.btn25,R.id.btn35,R.id.btn45,R.id.btn55,R.id.btn65,R.id.btn75},
            {R.id.btn06,R.id.btn16,R.id.btn26,R.id.btn36,R.id.btn46,R.id.btn56,R.id.btn66,R.id.btn76},
            {R.id.btn07,R.id.btn17,R.id.btn27,R.id.btn37,R.id.btn47,R.id.btn57,R.id.btn67,R.id.btn77},
    };
    //체스판 위에서의 기물들의 종류(없음, 폰, 룩, 나이트, 비숍, 퀸, 킹)와 위치를 나타낼 8x8 크기의 Char 배열
    //흰색 기물 앞에는 W, 검은 기물은 B가 붙고, 기물이 없으면 O가 들어간다. 두번째 글자가 기물의 종류를 나타낸다.(폰:P, 룩:R, 나이트:N, 비숍:B, 퀸:Q, 킹:K)
    //세번쨰 글자는 그 칸이 색칠되었는지(C) 아닌지(U)를 나타낸다. 색칠되었다면 그 칸은 어떤 기물이 이동할 수 있는 장소임을 나타낸다.
    //필요에 따라 게임 중간에 그만두고 다시 하게 될 수도 있으니 초기 체스 기물 위치를 저장한다. ORIGIN은 바꾸지 않는다
    String[][] ORIGIN_ChessPieceMap = {
            {"BRU","BNU","BBU","BKU","BQU","BBU","BNU","BRU"},
            {"BPU","BPU","BPU","BPU","BPU","BPU","BPU","BPU"},
            {"OOU","OOU","OOU","OOU","OOU","OOU","OOU","OOU"},
            {"OOU","OOU","OOU","OOU","OOU","OOU","OOU","OOU"},
            {"OOU","OOU","OOU","OOU","OOU","OOU","OOU","OOU"},
            {"OOU","OOU","OOU","OOU","OOU","OOU","OOU","OOU"},
            {"WPU","WPU","WPU","WPU","WPU","WPU","WPU","WPU"},
            {"WRU","WNU","WBU","WQU","WKU","WBU","WNU","WRU"}
    };
    //ChessPieceMap 에서 현재 진행 중인 게임의 기물에 따라 값을 변경하고 현재 플레이되는 게임을 다루기 위해 사용한다.
    public String[][] ChessPieceMap= ORIGIN_ChessPieceMap;
    //전역변수로 선언



    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chess_game);
        for(int i=0;i<8;i++)    //줄
        {
            for(int j=0;j<8;j++)    //열
            {
                ChessBtn[j][i]=(ImageButton) findViewById(R_btn[j][i]);
                //버튼들에 대한 클릭리스너 생성  (모든 버튼에 대한 이벤트를 만들면 너무 길어지기 때문에 사용)
                //출처: https://isntyet.tistory.com/85
                //버튼에 대한 이벤트가 발생하면 전부 btnListener로  처리된다.
                ChessBtn[j][i].setOnClickListener(btnListener);
            }
        }

        //ChessMap 생성 ChessPieceMap의 내용에 따라 맵을 구현한다.
        ChessMapMaking();


    }

    // 버튼이 눌렸을 때  4가지 상황으로 나눠서 구분한다.  0: 움직일 흰색 기물이 선택되는 턴 1: 선택된 기물이 움직일 위치를 선택하는 턴
    // 2: 움직일 검은 기물이 선택되는 턴 3: 선택된 기물이 움직일 위치를 선택하는 턴
    //이 상태를 표현하는 변수를 WhichTurn으로 선언한다.
    private  int WhichTurn=0;
    private int ChosenChessPieceRow=0,ChosenChessPieceColum=0; //선언시 한번만 0으로 초기화 되도록 전역변수로 선언한다.
    private final View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            //현재 클린된 버튼이 어느 위치의 버튼인지 확인하기 위한 코드
            int colum = 0, row = 0;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (view.getId() == R_btn[j][i])     //얻은 ID가 R_btn의 아이디와 같다면
                    {
                        colum = i;      //열 : 세로
                        row = j;        //행 : 가로   저장
                    }
                }
            }

            //ChosenChessPieceRow,ChosenChessPieceColum 사용 //말을 움직일 때 움직여야할 말을 기억하는데 사용한다.
            char IsCorrectColor=ChessPieceMap[row][colum].charAt(0);
            if(WhichTurn==0&&IsCorrectColor=='W')        //움직일 흰색 기물이 클릭되었다.
            {
                ChosenChessPieceRow=row;
                ChosenChessPieceColum=colum;
                ChessPieceChosen(row,colum,ChessPieceMap);    //선택된 기물이 움직일 수 있는 위치를 ChessPieceMap에 표시한다.
            }
            else if(WhichTurn==1)       //흰색 기물이 움직일 위치가 클릭되었다.
            {
                ChessPieceMove(row,colum,ChosenChessPieceRow,ChosenChessPieceColum,ChessPieceMap);
            }
            else if(WhichTurn==2&&IsCorrectColor=='B')       //움직일 검은색 기물이 클릭되었다.
            {
                ChosenChessPieceRow=row;
                ChosenChessPieceColum=colum;
                ChessPieceChosen(row,colum,ChessPieceMap);
            }
            else if(WhichTurn==3)       //검은색 기물이 움직일 위치가 클릭되었다.
            {
                ChessPieceMove(row,colum,ChosenChessPieceRow,ChosenChessPieceColum,ChessPieceMap);
            }
            else
            {
                //잘못 클릭했을 경우
                if(WhichTurn>0)
                {
                    WhichTurn--;
                }
                else
                {
                    WhichTurn=3;
                }
            }
            if(WhichTurn<3)
            {
                WhichTurn++;
            }
            else
            {
                WhichTurn=0;
            }

            //현재 맵 상태를 나타낸 ChessPieceMap에 따라 맵을 구현한다.
            //background 설정하기
            ChessMapMaking();
        }
    };

    private String[][] ChessPieceMove(int row,int colum, int ChosenRow, int ChosenColum,String[][] ChessPieceMap)  //Chosen이 붙은 것은 움직여야 할 기물의 위치이다. 붙지 않은 것은 움직여야할 위치이다.
    {
        char IsColored = ChessPieceMap[row][colum].charAt(2); //움직일 위치가 움직일 수 있는 위치인지(색칠이 되어 있는지) 확인한다.
        if(IsColored=='C')
        {
            //승리 확인
            Character KingCheck,WhoWin;
            KingCheck=ChessPieceMap[row][colum].charAt(1);
            if(KingCheck=='K')
            {
                WhoWin=ChessPieceMap[ChosenRow][ChosenColum].charAt(0);
                Victory(WhoWin);
            }

            //프로모션
            Character PColor,IsP;
            IsP = ChessPieceMap[ChosenRow][ChosenColum].charAt(1);
            PColor=ChessPieceMap[ChosenRow][ChosenColum].charAt(0);
            if(IsP=='P')        //이동한 기물이 폰인지 확인한다
            {
                if(PColor=='W')
                {
                    if(row==0)    //흰색 폰이 반대편 끝에 도달했다.
                    {
                        Promotion(row,colum,PColor);
                    }
                }
                else if(PColor=='B')
                {
                    if(row==7)    //검은 폰이 반대편 끝에 도달했다.
                    {
                        Promotion(row,colum,PColor);
                    }
                }
            }

            //움직일 기물의 색과 종류 받고
            Character PieceColor = ChessPieceMap[ChosenRow][ChosenColum].charAt(0), PieceJob = ChessPieceMap[ChosenRow][ChosenColum].charAt(1);
            //움직일 위치에 기물 배치하고
            ChessPieceMap[row][colum]=String.valueOf(PieceColor)+String.valueOf(PieceJob)+"U";
            //원래 위치에 기물 없음 표시하기
            ChessPieceMap[ChosenRow][ChosenColum]=String.valueOf("OOU");

            //기물 선택이 끝났으니 움직일 위치 (background에 칠해진 색) 초기화하기
            char temp1,temp2;
            String tempSum;
            for(int Si=0;Si<8;Si++)
            {
                for(int Sj=0;Sj<8;Sj++)
                {
                    temp1 = ChessPieceMap[Sj][Si].charAt(0);
                    temp2 = ChessPieceMap[Sj][Si].charAt(1);
                    //ChessPieceMap[Sj][Si]=String.valueOf(temp1)+String.valueOf(temp2)+"U";
                    //이상하게 그냥 String.valueOf(temp1)+String.valueOf(temp2)+"U"로 넣으면 오류 나서 대체
                    if(temp1=='W')
                    {
                        if (temp2 == 'P')
                        {
                            ChessPieceMap[Sj][Si]=String.valueOf("WPU");
                        }
                        else if(temp2=='R')
                        {
                            ChessPieceMap[Sj][Si]=String.valueOf("WRU");
                        }
                        else if(temp2=='B')
                        {
                            ChessPieceMap[Sj][Si]=String.valueOf("WBU");
                        }
                        else if(temp2=='N')
                        {
                            ChessPieceMap[Sj][Si]=String.valueOf("WNU");
                        }
                        else if(temp2=='Q')
                        {
                            ChessPieceMap[Sj][Si]=String.valueOf("WQU");
                        }
                        else if(temp2=='K')
                        {
                            ChessPieceMap[Sj][Si]=String.valueOf("WKU");
                        }
                    }
                    else if(temp1=='B')
                    {
                        if (temp2 == 'P')
                        {
                            ChessPieceMap[Sj][Si]=String.valueOf("BPU");
                        }
                        else if(temp2=='R')
                        {
                            ChessPieceMap[Sj][Si]=String.valueOf("BRU");
                        }
                        else if(temp2=='B')
                        {
                            ChessPieceMap[Sj][Si]=String.valueOf("BBU");
                        }
                        else if(temp2=='N')
                        {
                            ChessPieceMap[Sj][Si]=String.valueOf("BNU");
                        }
                        else if(temp2=='Q')
                        {
                            ChessPieceMap[Sj][Si]=String.valueOf("BQU");
                        }
                        else if(temp2=='K')
                        {
                            ChessPieceMap[Sj][Si]=String.valueOf("BKU");
                        }
                    }
                    else
                    {
                        ChessPieceMap[Sj][Si]=String.valueOf("OOU");
                    }
                }
            }

        }
        else
        {
            //토스트 메세지 띄우기
            WhichTurn--;
        }

        return ChessPieceMap;
    };

    private  void ChessMapMaking()
    {
        //background 설정
        //ChessPieceMap의 2번째 자리 즉 U(색칠 안됨) C(색칠됨)에 따라 background의 색깔을 칠한다.
        char temp3;
        for(int i=0;i<8;i++)
        {
            for(int j=0;j<8;j++)
            {
                temp3=ChessPieceMap[j][i].charAt(2);
                if(temp3 == 'C')       //만약 칸이 색칠된 칸이라면
                {
                    ChessBtn[j][i].setBackgroundResource(R.color.brown);    //그 칸을 색칠한다.
                }
                if(temp3=='U')
                {
                    if(i%2==0)  //0,2,4,6 번째 줄
                    {
                        if(j%2==0)  //하얀색 칸
                        {
                            ChessBtn[j][i].setBackgroundResource(R.color.white);
                        }
                        else        //검은색 칸
                        {
                            ChessBtn[j][i].setBackgroundResource(R.color.black);
                        }
                    }
                    else if(i%2==1) //1,3,5,7 번째 줄
                    {
                        if(j%2==0)  //검은색 칸
                        {
                            ChessBtn[j][i].setBackgroundResource(R.color.black);
                        }
                        else        //하얀색 칸
                        {
                            ChessBtn[j][i].setBackgroundResource(R.color.white);
                        }
                    }
                }
            }
        }


        //말 생성
        char temp1,temp2;  //temp1은 기물의 색깔이나 유무, temp2는 기물의 종류
        for(int i=0;i<=7;i++)
        {
            for(int j=0;j<=7;j++)
            {
                //초기 체스 기물 위치에서 기물의 색과 종류를 가져온다.
                temp1 = ChessPieceMap[j][i].charAt(0);
                temp2 = ChessPieceMap[j][i].charAt(1);

                if(temp1=='W')
                {
                    switch (temp2)
                    {
                        case 'P':
                            ChessBtn[j][i].setImageResource(R.drawable.whitechess_p);
                            break;
                        case 'R':
                            ChessBtn[j][i].setImageResource(R.drawable.whitechess_r);
                            break;
                        case 'N':
                            ChessBtn[j][i].setImageResource(R.drawable.whitechess_n);
                            break;
                        case 'B':
                            ChessBtn[j][i].setImageResource(R.drawable.whitechess_b);
                            break;
                        case 'Q':
                            ChessBtn[j][i].setImageResource(R.drawable.whitechess_q);
                            break;
                        case 'K':
                            ChessBtn[j][i].setImageResource(R.drawable.whitechess_k);
                            break;
                    }
                }
                else if(temp1=='B')
                {
                    switch (temp2)
                    {
                        case 'P':
                            ChessBtn[j][i].setImageResource(R.drawable.blackchess_p);
                            break;
                        case 'R':
                            ChessBtn[j][i].setImageResource(R.drawable.blackchess_r);
                            break;
                        case 'N':
                            ChessBtn[j][i].setImageResource(R.drawable.blackchess_n);
                            break;
                        case 'B':
                            ChessBtn[j][i].setImageResource(R.drawable.blackchess_b);
                            break;
                        case 'Q':
                            ChessBtn[j][i].setImageResource(R.drawable.blackchess_q);
                            break;
                        case 'K':
                            ChessBtn[j][i].setImageResource(R.drawable.blackchess_k);
                            break;
                    }
                }
                else
                {
                    ChessBtn[j][i].setImageResource(0);
                }
            }
        }
    }


    private String[][] ChessPieceChosen(int row,int colum,String[][] ChessPieceMap)
    {
        //현재 위치의 기물이 어떤 색인지 (PieceColor), 어떤 기물인지 (PieceJob) charAt()으로 0번째 글자, 1번째 글자를 잘라 각각의 변수에 저장한다.
        Character PieceColor = ChessPieceMap[row][colum].charAt(0), PieceJob = ChessPieceMap[row][colum].charAt(1), IsBackColored = ChessPieceMap[row][colum].charAt(2);
        //기물이 어떤 종류의 기물인지에 따라 다르게 맵에 표시한다.

        char temp1=0 ,temp2=0;       //반복 중 어떤 말을 선택 중인지 저장하기 위한 변수
        switch (PieceJob)
        {
            case 'P':
                if(PieceColor=='W')
                {
                    if(row==6)      //흰색 폰이 초기 시작하는 위치에서 움직이기 시작한다.
                    {
                        PMove(row,colum,PieceColor,1);
                        PMove(row,colum,PieceColor,2);    //이때, 폰은 2칸 움직일 수 있다.
                    }
                    else
                    {
                        PMove(row,colum,PieceColor,1);    //일반 상황에서의 폰의 이동
                    }
                }
                else if(PieceColor=='B')
                {
                    if(row==1)
                    {
                        PMove(row,colum,PieceColor,1);
                        PMove(row,colum,PieceColor,2);
                    }
                    else
                    {
                        PMove(row,colum,PieceColor,1);    //일반 상황에서의 폰의 이동
                    }
                }

                break;

            case 'R':
                //RBMove==  상하좌우 직선 대각선 이동 경로 색칠
                //검은색이든 흰색이든 룩이기 떄문에 직선 방향으로 어디든 움직일 수 있음
                // 상하좌우 움직일 수 있는 경로를 색칠한다.
                //현재 클릭된 위치(row, colum) 현재 클릭된 기물의 색깔(PieceColor)(상대 말인지 아닌지 구분하고 잡을 수있는지 판별하는 기준이됨)
                //HowLongMove(4번째 숫자)== 해당 기물이 얼마나 길게 이동할 수 있는가 == 킹은 1칸, 퀸은 8칸, 비숍과 룩도 8칸
                //RD= RigthLeft 값이 =1이면 오른쪽 -1이면 왼쪽
                //UD = UpDown   값이 =1이면 아래쪽 -1이면 위쪽
                //직선 이동
                RBMove(row,colum,1,0,PieceColor,8);  //오른쪽 이동 가능 범위 색칠
                RBMove(row,colum,-1,0,PieceColor,8);  //왼쪽 이동 가능 범위 색칠
                RBMove(row,colum,0,1,PieceColor,8);  //아래쪽 이동 가능 범위 색칠
                RBMove(row,colum,0,-1,PieceColor,8); //위쪽 이동 가능 범위 색칠
                break;
            case 'N':
                //검은색, 흰색 상관 없이 가로1세로2 이동이나 가로2세로1 이동을 할 수 있음\N
                NMove(row,colum,PieceColor);

                break;
            case 'B':
                //검은색, 흰색 상관 없이 대각선으로 이동 가능
                // BMove(row,colum,PieceColor,8);
                RBMove(row,colum,1,1,PieceColor,8);  //오른쪽 아래 이동 가능 범위 색칠
                RBMove(row,colum,-1,-1,PieceColor,8);  //왼쪽 위 이동 가능 범위 색칠
                RBMove(row,colum,1,-1,PieceColor,8);  //오른쪽 위 이동 가능 범위 색칠
                RBMove(row,colum,-1,1,PieceColor,8); //왼쪽 위 이동 가능 범위 색칠
                break;
            case 'Q':
                //검은색, 흰색 상관 없이 직선,대각선 거리 제한 없이 이동 가능
                //직선 이동
                RBMove(row,colum,1,0,PieceColor,8);  //오른쪽 이동 가능 범위 색칠
                RBMove(row,colum,-1,0,PieceColor,8);  //왼쪽 이동 가능 범위 색칠
                RBMove(row,colum,0,1,PieceColor,8);  //아래쪽 이동 가능 범위 색칠
                RBMove(row,colum,0,-1,PieceColor,8); //위쪽 이동 가능 범위 색칠
                //대각선 이동
                RBMove(row,colum,1,1,PieceColor,8);  //오른쪽 아래 이동 가능 범위 색칠
                RBMove(row,colum,-1,-1,PieceColor,8);  //왼쪽 위 이동 가능 범위 색칠
                RBMove(row,colum,1,-1,PieceColor,8);  //오른쪽 위 이동 가능 범위 색칠
                RBMove(row,colum,-1,1,PieceColor,8); //왼쪽 위 이동 가능 범위 색칠
                break;
            case 'K':
                //검은색, 흰색 상관 없이 직선, 대각선으로 한 칸 이동 가능하다.
                //직선 이동
                RBMove(row,colum,1,0,PieceColor,1);  //오른쪽 이동 가능 범위 색칠
                RBMove(row,colum,-1,0,PieceColor,1);  //왼쪽 이동 가능 범위 색칠
                RBMove(row,colum,0,1,PieceColor,1);  //아래쪽 이동 가능 범위 색칠
                RBMove(row,colum,0,-1,PieceColor,1); //위쪽 이동 가능 범위 색칠
                //대각선 이동
                RBMove(row,colum,1,1,PieceColor,1);  //오른쪽 아래 이동 가능 범위 색칠
                RBMove(row,colum,-1,-1,PieceColor,1);  //왼쪽 위 이동 가능 범위 색칠
                RBMove(row,colum,1,-1,PieceColor,1);  //오른쪽 위 이동 가능 범위 색칠
                RBMove(row,colum,-1,1,PieceColor,1); //왼쪽 위 이동 가능 범위 색칠
                break;
        }
        return ChessPieceMap;
    };

    //폰의 일반 상황에서의 움직임 설정
    //row, colum, 현재 내 기물의 색, 폰이 얼마나 움직일지
    private void PMove(int row,int colum, char PieceColor,int howLongMove)
    {
        char temp1,temp2;
        char enemyColor='o';    //적의 말의 색깔
        int moveDirection=0;  //우리 기물이 움직일 방향

        if(PieceColor=='W')
        {
            enemyColor='B'; //적은 검은색
            moveDirection=-1*howLongMove;   //흰색의 폰이니까 현재 위치에서 하나 위로 이동할 수 있음
        }
        else if(PieceColor=='B')
        {
            enemyColor='W';
            moveDirection=1*howLongMove;    //검은 색 폰이니까 현재 위치에서 하나 아래로 이동할 수 있음
        }



        if(ChessPieceMap[row+moveDirection][colum]=="OOU")
        {
            //현재 위치의 색과 기물을 저장
            temp1=ChessPieceMap[row+moveDirection][colum].charAt(0);
            temp2=ChessPieceMap[row+moveDirection][colum].charAt(1);
            //색과 기물을 그대로 두고 background가 색칠됨을 알린다.
            ChessPieceMap[row+moveDirection][colum]=String.valueOf(temp1)+String.valueOf(temp2)+"C";
        }
        //폰이 상대 기물을 잡는 상황
        if(colum+1<8)
        {
            temp1 = ChessPieceMap[row +moveDirection][colum + 1].charAt(0);
            if (temp1 == enemyColor) {
                temp2 = ChessPieceMap[row + moveDirection][colum + 1].charAt(1);
                //색과 기물을 그대로 두고 background가 색칠됨을 알린다.
                ChessPieceMap[row +moveDirection][colum + 1] = String.valueOf(temp1) + String.valueOf(temp2) + "C";
            }
        }
        if(colum-1>=0)
        {
            temp1 = ChessPieceMap[row +moveDirection][colum - 1].charAt(0);
            if (temp1 == enemyColor) {
                temp2 = ChessPieceMap[row +moveDirection][colum - 1].charAt(1);
                //색과 기물을 그대로 두고 background가 색칠됨을 알린다.
                ChessPieceMap[row +moveDirection][colum - 1] = String.valueOf(temp1) + String.valueOf(temp2) + "C";
            }
        }
    }


    //나이트의 움직임 설정
    private void NMove(int row,int colum,char PieceColor)
    {
        char temp1,temp2;
        //나이트가 이동하려면 이동 경로에 말이 없어야 한다. 위쪽으로 이동하려 한다면 나이트의 한칸 위가 비어있어야하며, 비어있지 않다면 위쪽으로는 이동할 수 없다.
        //즉 나이트는 상하좌우의 칸에 말이 있냐 없냐에 따라 갈 수 있는 칸이 달라진다.
        /*
        if(row+2*RL<8||row+2*RL>=0) //나이트가 칸 밖으로 나가거나 잘못된 값이 들어가는 오류가 없게 if문을 건다.
        {
            if(colum+2*UD<8||colum+2*UD>=0)
            {
            //이 나이트는 상/하/좌/우 방향으로 이동할 수 있게 그 방향의 한 칸이 비어 있다.
                if(ChessPieceMap[row+RL][colum+UD]=="OOU")
                {
                    if (UD != 0)   //이동하면서 거쳐가는 칸이 위/아래 칸일 경우
                    {
                        if (row + 1 < 8)     //이 나이트는 위/아래 칸으로 이동 후 오른쪽으로 갈 수 있다.
                        {
                            if (ChessPieceMap[row + 1][colum + 2 * UD] == "OOU")
                            {
                                ChessPieceMap[row + 1][colum + 2 * UD] = "OOC";
                            }
                            else
                            {
                                temp1 = ChessPieceMap[row + 1][colum + 2 * UD].charAt(0);
                                temp2 = ChessPieceMap[row + 1][colum + 2 * UD].charAt(1);
                                if (PieceColor != temp1)  //기물이 다른 색일 경우
                                {
                                    //기물은 그 자리로 움직일 수 있다.( 그 기물을 잡을 수 있다.)
                                    ChessPieceMap[row + 1][colum + 2 * UD] = String.valueOf(temp1) + String.valueOf(temp2) + "C";
                                    //그 기물을 뛰어넘을 수 없으므로 이동은 이곳에서 끝이다.
                                }
                            }
                        }
                        if (row - 1 >= 0)       //이 나이트는 위/아래 칸으로 이동 후 왼쪽으로 갈 수 있다.
                        {
                            if (ChessPieceMap[row - 1][colum + 2 * UD] == "OOU")
                            {
                                ChessPieceMap[row - 1][colum + 2 * UD] = "OOC";
                            }
                            else
                            {
                                temp1 = ChessPieceMap[row - 1][colum + 2 * UD].charAt(0);
                                temp2 = ChessPieceMap[row - 1][colum + 2 * UD].charAt(1);
                                if (PieceColor != temp1)  //기물이 다른 색일 경우
                                {
                                    //기물은 그 자리로 움직일 수 있다.( 그 기물을 잡을 수 있다.)
                                    ChessPieceMap[row - 1][colum + 2 * UD] = String.valueOf(temp1) + String.valueOf(temp2) + "C";
                                    //그 기물을 뛰어넘을 수 없으므로 이동은 이곳에서 끝이다.
                                }
                            }
                        }
                    } else if (RL != 0)  //이동하면서 거쳐가는 칸이 좌/우 의 칸일 경우
                    {
                        if (colum + 1 < 8)     //이 나이트는 좌/우 칸으로 이동 후 아래쪽으로 갈 수 있다.
                        {
                            if (ChessPieceMap[row + 2 * RL][colum + 1] == "OOU")    //그 칸이 비어있을 경우
                            {
                                ChessPieceMap[row + 2 * RL][colum + 1] = "OOC";
                            }
                            else
                            {
                                temp1 = ChessPieceMap[row + 2 * RL][colum + 1].charAt(0);
                                temp2 = ChessPieceMap[row + 2 * RL][colum + 1].charAt(1);
                                if (PieceColor != temp1)  //룩과 기물이 다른 색일 경우
                                {
                                    //룩은 그 자리로 움직일 수 있다.( 그 기물을 잡을 수 있다.)
                                    ChessPieceMap[row + 2 * RL][colum + 1] = String.valueOf(temp1) + String.valueOf(temp2) + "C";
                                    //그 기물을 뛰어넘을 수 없으므로 오른쪽으로의 이동은 이곳에서 끝이다.
                                }
                            }
                        }
                        if (colum - 1 >= 0)  //이 나이트는 좌/우 칸으로 이동 후 위쪽으로 갈 수 있다.
                        {
                            if (ChessPieceMap[row + 2 * RL][colum - 1] == "OOU")    //그 칸이 비어있을 경우
                            {
                                ChessPieceMap[row + 2 * RL][colum - 1] = "OOC";
                            }
                            else
                            {
                                temp1 = ChessPieceMap[row + 2 * RL][colum - 1].charAt(0);
                                temp2 = ChessPieceMap[row + 2 * RL][colum - 1].charAt(1);
                                if (PieceColor != temp1)  //룩과 기물이 다른 색일 경우
                                {
                                    //룩은 그 자리로 움직일 수 있다.( 그 기물을 잡을 수 있다.)
                                    ChessPieceMap[row + 2 * RL][colum - 1] = String.valueOf(temp1) + String.valueOf(temp2) + "C";
                                    //그 기물을 뛰어넘을 수 없으므로 오른쪽으로의 이동은 이곳에서 끝이다.
                                }
                            }
                        }
                    }
                }
            }
        }

         */




        if(row+1+1<8) //나이트가 칸 밖으로 나가거나 잘못된 값이 들어가는 오류가 없게 if문을 건다.
        {
            //이 나이트는 오른쪽으로 이동할 수 있을 만큼 오른쪽에 칸이 있다.
            if (ChessPieceMap[row + 1][colum] == "OOU")  //나이트가 오른쪽으로 이동할 수 있는가?
            {
                //이 나이트는 오른쪽 한칸이 비었기에 오른쪽으로 이동할 수 있다.
                if (colum + 1 < 8)       //이 나이트는 오른쪽 아래로 이동할 수 있다.
                {
                    if (ChessPieceMap[row + 2][colum + 1] == "OOU")    //그 칸이 비어있을 경우
                    {
                        //temp1=ChessPieceMap[row+2][colum+1].charAt(0);
                        //temp2=ChessPieceMap[row+2][colum+1].charAt(1);    String.valueOf(temp1)+String.valueOf(temp2)+
                        ChessPieceMap[row + 2][colum + 1] = "OOC";
                    } else {
                        temp1 = ChessPieceMap[row + 2][colum + 1].charAt(0);
                        temp2 = ChessPieceMap[row + 2][colum + 1].charAt(1);
                        if (PieceColor != temp1)  //룩과 기물이 다른 색일 경우
                        {
                            //룩은 그 자리로 움직일 수 있다.( 그 기물을 잡을 수 있다.)
                            ChessPieceMap[row + 2][colum + 1] = String.valueOf(temp1) + String.valueOf(temp2) + "C";
                            //그 기물을 뛰어넘을 수 없으므로 오른쪽으로의 이동은 이곳에서 끝이다.
                        } else {
                            //같은 색이다.
                        }
                    }
                }

                if (colum - 1 >= 0) {
                    if (ChessPieceMap[row + 2][colum - 1] == "OOU") {
                        ChessPieceMap[row + 2][colum + 1] = "OOC";
                    } else {
                        temp1 = ChessPieceMap[row + 2][colum - 1].charAt(0);
                        temp2 = ChessPieceMap[row + 2][colum - 1].charAt(1);
                        if (PieceColor != temp1)  //룩과 기물이 다른 색일 경우
                        {
                            //룩은 그 자리로 움직일 수 있다.( 그 기물을 잡을 수 있다.)
                            ChessPieceMap[row + 2][colum - 1] = String.valueOf(temp1) + String.valueOf(temp2) + "C";
                            //그 기물을 뛰어넘을 수 없으므로 오른쪽으로의 이동은 이곳에서 끝이다.
                        } else {
                            //같은 색이다.
                        }
                    }
                }

            } else {
                //이 나이트는 오른쪽 한 칸이 비어있지 않아 그 기물에 진행을 방해당하여 오른쪽으로 이동할 수 없다.
            }
        }


        //이 나이트는 왼쪽으로 이동할 수 있을 만큼 왼쪽에 칸이 있다.
        if(row-2>=0)
        {
            //이 나이트는 왼쪽으로 이동할 수 있을 만큼 왼쪽에 칸이 있다.
            if(ChessPieceMap[row-1][colum]=="OOU")  //나이트가 왼쪽으로 이동할 수 있는가?
            {
                //이 나이트는 왼쪽 한칸이 비었기에 왼쪽으로 이동할 수 있다.
                if(colum+1<8)       //이 나이트는 왼쪽 아래로 이동할 수 있다.
                {
                    if(ChessPieceMap[row-2][colum+1]=="OOU")    //그 칸이 비어있을 경우
                    {
                        ChessPieceMap[row-2][colum+1]="OOC";
                    }
                    else
                    {
                        temp1=ChessPieceMap[row-2][colum+1].charAt(0);
                        temp2=ChessPieceMap[row-2][colum+1].charAt(1);
                        if (PieceColor!=temp1)  //룩과 기물이 다른 색일 경우
                        {
                            //룩은 그 자리로 움직일 수 있다.( 그 기물을 잡을 수 있다.)
                            ChessPieceMap[row-2][colum+1]=String.valueOf(temp1)+String.valueOf(temp2)+"C";
                            //그 기물을 뛰어넘을 수 없으므로 오른쪽으로의 이동은 이곳에서 끝이다.
                        }
                        else
                        {
                            //같은 색이다.
                        }
                    }
                }

                if(colum-1>=0)  //이 나이트는 왼쪽 위로 이동할 수 있는가
                {
                    if(ChessPieceMap[row-2][colum-1]=="OOU")
                    {
                        ChessPieceMap[row-2][colum-1]="OOC";
                    }
                    else
                    {
                        temp1=ChessPieceMap[row-2][colum-1].charAt(0);
                        temp2=ChessPieceMap[row-2][colum-1].charAt(1);
                        if (PieceColor!=temp1)  //기물이 다른 색일 경우
                        {
                            //기물은 그 자리로 움직일 수 있다.( 그 기물을 잡을 수 있다.)
                            ChessPieceMap[row-2][colum-1]=String.valueOf(temp1)+String.valueOf(temp2)+"C";
                            //그 기물을 뛰어넘을 수 없으므로 이동은 이곳에서 끝이다.
                        }
                        else
                        {
                            //같은 색이다.
                        }
                    }
                }
            }
            else
            {
                //이 나이트는 왼쪽 한 칸이 비어있지 않아 그 기물에 진행을 방해당하여 왼쪽으로 이동할 수 없다.
            }
        }



        //이 나이트는 위쪽으로 이동할 수 있을 만큼 위쪽에 칸이 있다.
        if(colum-2>=0)
        {
            if(ChessPieceMap[row][colum-1]=="OOU")  //나이트가 위쪽으로 이동할 수 있는가?
            {
                if(row+1<8)       //이 나이트는 위쪽 오른쪽으로 이동할 수 있다.
                {
                    if(ChessPieceMap[row+1][colum-2]=="OOU")    //그 칸이 비어있을 경우
                    {
                        ChessPieceMap[row+1][colum-2]="OOC";
                    }
                    else
                    {
                        temp1=ChessPieceMap[row+1][colum-2].charAt(0);
                        temp2=ChessPieceMap[row+1][colum-2].charAt(1);
                        if (PieceColor!=temp1)  // 기물이 다른 색일 경우
                        {
                            //룩은 그 자리로 움직일 수 있다.( 그 기물을 잡을 수 있다.)
                            ChessPieceMap[row+1][colum-2]=String.valueOf(temp1)+String.valueOf(temp2)+"C";
                            //그 기물을 뛰어넘을 수 없으므로 오른쪽으로의 이동은 이곳에서 끝이다.
                        }
                        else
                        {
                            //같은 색이다.
                        }
                    }
                }

                if(row-1>=0)  //이 나이트는 위쪽 왼쪽으로 이동할 수 있는가
                {
                    if(ChessPieceMap[row-1][colum-2]=="OOU")
                    {
                        ChessPieceMap[row-1][colum-2]="OOC";
                    }
                    else
                    {
                        temp1=ChessPieceMap[row-1][colum-2].charAt(0);
                        temp2=ChessPieceMap[row-1][colum-2].charAt(1);
                        if (PieceColor!=temp1)  //기물이 다른 색일 경우
                        {
                            //기물은 그 자리로 움직일 수 있다.( 그 기물을 잡을 수 있다.)
                            ChessPieceMap[row-1][colum-2]=String.valueOf(temp1)+String.valueOf(temp2)+"C";
                            //그 기물을 뛰어넘을 수 없으므로 이동은 이곳에서 끝이다.
                        }
                        else
                        {
                            //같은 색이다.
                        }
                    }
                }
            }
            else
            {
                //이 나이트는 왼쪽 한 칸이 비어있지 않아 그 기물에 진행을 방해당하여 왼쪽으로 이동할 수 없다.
            }
        }


        //이 나이트는 아래쪽으로 이동할 수 있을 만큼 아래쪽에 칸이 있다.
        if(colum+2<8)
        {
            if(ChessPieceMap[row][colum+1]=="OOU")  //나이트가 위쪽으로 이동할 수 있는가?
            {
                if(row+1<8)       //이 나이트는 위쪽 오른쪽으로 이동할 수 있다.
                {
                    if(ChessPieceMap[row+1][colum+2]=="OOU")    //그 칸이 비어있을 경우
                    {
                        ChessPieceMap[row+1][colum+2]="OOC";
                    }
                    else
                    {
                        temp1=ChessPieceMap[row+1][colum+2].charAt(0);
                        temp2=ChessPieceMap[row+1][colum+2].charAt(1);
                        if (PieceColor!=temp1)  // 기물이 다른 색일 경우
                        {
                            //룩은 그 자리로 움직일 수 있다.( 그 기물을 잡을 수 있다.)
                            ChessPieceMap[row+1][colum+2]=String.valueOf(temp1)+String.valueOf(temp2)+"C";
                            //그 기물을 뛰어넘을 수 없으므로 오른쪽으로의 이동은 이곳에서 끝이다.
                        }
                        else
                        {
                            //같은 색이다.
                        }
                    }
                }

                if(row-1>=0)  //이 나이트는 위쪽 왼쪽으로 이동할 수 있는가
                {
                    if(ChessPieceMap[row-1][colum+2]=="OOU")
                    {
                        ChessPieceMap[row-1][colum+2]="OOC";
                    }
                    else
                    {
                        temp1=ChessPieceMap[row-1][colum+2].charAt(0);
                        temp2=ChessPieceMap[row-1][colum+2].charAt(1);
                        if (PieceColor!=temp1)  //기물이 다른 색일 경우
                        {
                            //기물은 그 자리로 움직일 수 있다.( 그 기물을 잡을 수 있다.)
                            ChessPieceMap[row-1][colum+2]=String.valueOf(temp1)+String.valueOf(temp2)+"C";
                            //그 기물을 뛰어넘을 수 없으므로 이동은 이곳에서 끝이다.
                        }
                        else
                        {
                            //같은 색이다.
                        }
                    }
                }
            }
            else
            {
                //이 나이트는 왼쪽 한 칸이 비어있지 않아 그 기물에 진행을 방해당하여 왼쪽으로 이동할 수 없다.
            }
        }



    }


    private void RBMove(int row,int colum,int RL, int UD,char PieceColor,int HowLongMove)       //상하좌우 ,대각선 이동
    {
        //RL=RightLeft, UD=UpDown
        //RL 값이 양수 = 기물이 오른쪽으로 이동, 음수= 기물이 왼쪽으로 이동
        //UD 값이 양수 = 기물이 아래쪽으로 이동, 음수= 기물이 위쪽으로 이동
        char temp1,temp2;

        int i=1;
        while(row+RL<8&&row+RL>=0&&colum+UD<8&&colum+UD>=0)      //기물이 움직일 수 있는 범위가 체스판 안일 수 있도록 제한
        {
            if(ChessPieceMap[row+RL][colum+UD]=="OOU")  //기물이 움직일 수 있는 칸에 다른 기물이 없을 때
            {
                ChessPieceMap[row+RL][colum+UD]="OOC";
            }
            else    //기물이 움직일 수 있는 칸에 다른 기물이 있을 경우
            {
                temp1=ChessPieceMap[row+RL][colum+UD].charAt(0);
                temp2=ChessPieceMap[row+RL][colum+UD].charAt(1);
                if (PieceColor!=temp1)  //기물과 기물이 다른 색일 경우
                {
                    //기물은 그 자리로 움직일 수 있다.( 그 기물을 잡을 수 있다.)
                    ChessPieceMap[row+RL][colum+UD]=String.valueOf(temp1)+String.valueOf(temp2)+"C";
                    //그 기물을 뛰어넘을 수 없으므로 오른쪽으로의 이동은 이곳에서 끝이다. 그러니 반복문을 빠져나간다.
                    break;
                }
                else
                {
                    //같은 색으고, 뛰어넘을 수 없으니 반복문을 나간다.
                    break;
                }
            }

            if(HowLongMove==RL||HowLongMove==UD||HowLongMove==-RL||HowLongMove==-UD)  //만약 해당 기물이 이동할 수 있는 최대 칸 수에 도달 했다면 -> 이 반복문을 빠져나간다.
            {
                break;
            }
            //현재 기물이 움직일 방향인 RL,UD를 절댓값 1의 음수 양수로 되돌린다.
            RL=RL/i;
            UD=UD/i;
            i++;    //다음 칸을 탐사하기 위해 i(기물에서 몇 칸 떨어진 칸을 조사하는가에 대한 변수)의 크기를 하나 늘린다.
            RL=RL*i;    //RL,UD에 다음 음수,양수 * i를 통해 현재 칸에서 상하좌우 몇칸 떨어진 칸을 조사해야하는지 저장한다.
            UD=UD*i;

        }
    }

    private void Victory(Character WhoWin)
    {
        String winner;
        if (WhoWin == 'W')
        {
            winner = "White";
        }
        else
        {
            winner="Black";
        }



        // 승리 시 다이얼로그 띄우기
        final Dialog dialog2 = new Dialog(chessActivity.this);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.setContentView(R.layout.activity_victory);

        TextView winnerText = dialog2.findViewById(R.id.winnerText);
        if(winner =="무승부")
        {
            winnerText.setText(winner + "하였습니다!");
        }
        else {
            winnerText.setText(winner + "이(가) 승리했습니다!");
        }

        Button mainButton = dialog2.findViewById(R.id.mainButton);
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog2.dismiss();
                finish();
            }
        });

        Button retryButton = dialog2.findViewById(R.id.retryButton);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog2.dismiss();

                // 새로운 게임을 시작하는 인텐트 생성
                Intent intent = new Intent(chessActivity.this, chessActivity.class);
                startActivity(intent);
                finish(); // 현재의 게임 액티비티를 종료하여 새로운 게임을 시작합니다.
            }
        });

        dialog2.show();

    }


    final String[] PChange = {"룩","나이트","비숍","퀸"};
    private void Promotion(int row,int colum,Character PColor)
    {
        int[] PChoose={0};
        //대화상자 사용하기 위한 builder 선언
        AlertDialog.Builder Pbuilder = new AlertDialog.Builder(chessActivity.this);
        //대화상자의 제목 정하기
        Pbuilder.setTitle("프로모션");
        //대화상자 속 라디오버튼 만들기 위한 부분 (라디오버튼의 선택지가 될 문자열 배열, 초기선택번호(-1은 아무것도 선택 안 된 것), 선택에 반응할 리스너)
        Pbuilder.setSingleChoiceItems(PChange, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //라디오버튼의 선택이 바뀌면 현재 선택을 변수에 저장한다.
                PChoose[0]=i;
            }
        });
        //대화상자의 선택, 닫기라는 버튼을 추가한다.
        Pbuilder.setPositiveButton("닫기",null);
        //대화상자의 선택 버튼을 클릭했을 때, 현재 선택된 게임(GameChoice의 값)에 따라 새로운 창을 띄운다.
        Pbuilder.setNegativeButton("선택", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(PChoose[0]==0)    //룩 선택
                {
                    ChessPieceMap[row][colum]=String.valueOf(PColor)+"RU";
                }
                else if(PChoose[0]==1)   //나이트 선택
                {
                    ChessPieceMap[row][colum]=String.valueOf(PColor)+"NU";
                }
                else if(PChoose[0]==2)   //비숍 선택
                {
                    ChessPieceMap[row][colum]=String.valueOf(PColor)+"BU";
                }
                else if(PChoose[0]==3)   //퀸 선택
                {
                    ChessPieceMap[row][colum]=String.valueOf(PColor)+"QU";
                }
            }
        });
        //대화상자 생성하기
        AlertDialog dialog = Pbuilder.create();
        //대화상자 보여주기
        Pbuilder.show();
    }

}