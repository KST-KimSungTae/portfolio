package com.example.minigames;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class oshelloActivity extends AppCompatActivity {

    // 0: Empty, 1: Black, 2: White
    private int[][] board = new int[8][8];
    private int currentPlayer = 1; // 초기 플레이어는 흑돌(Black)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oshello_game);

        // GridLayout에서 버튼들을 찾아오기
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String buttonID = "cell_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                Button button = findViewById(resID);
                button.setOnClickListener(new CellClickListener(i, j));
            }
        }

        Button passButton = findViewById(R.id.passButton);
        Button passButton2 = findViewById(R.id.passButton2);


        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 패스 버튼을 눌렀을 때의 동작 구현
                if (!hasValidMove()) {
                    switchPlayer();
                    updateBoard();
                }
            }
        });

        passButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 패스 버튼을 눌렀을 때의 동작 구현
                if (!hasValidMove()) {
                    switchPlayer();
                    updateBoard();
                }
            }
        });

        TextView whiteView = findViewById((R.id.whitetext));
        TextView blackView = findViewById((R.id.blacktext));

        whiteView.setVisibility(View.INVISIBLE);
        blackView.setVisibility(View.VISIBLE);


        // 초기 오셀로 판 설정
        initializeBoard();
        updateBoard();
    }

    private boolean isGameOver() {
        // 모든 셀이 채워졌는지 확인
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 0) {
                    return false; // 빈 셀이 하나라도 있으면 게임 종료 X
                }
            }
        }
        return true; // 모든 셀이 채워졌으면 게임 종료 O
    }
    private void determineWinner() {
        // 승자를 결정하고 결과를 표시
        int countBlack = 0;
        int countWhite = 0;

        // 돌의 개수를 세기
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 1) {
                    countBlack++;
                } else if (board[i][j] == 2) {
                    countWhite++;
                }
            }
        }


        String winner;
        if (countWhite > countBlack) {
            winner = "백돌";
        } else if (countWhite < countBlack) {
            winner = "흑돌";
        } else {
            winner = "무승부";
        }

        // 승리 시 다이얼로그 띄우기
        final Dialog dialog = new Dialog(oshelloActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_victory);

        TextView winnerText = dialog.findViewById(R.id.winnerText);
        if(winner =="무승부")
        {
            winnerText.setText(winner + "하였습니다!");
        }
        else {
            winnerText.setText(winner + "이(가) 승리했습니다!");
        }

        Button mainButton = dialog.findViewById(R.id.mainButton);
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });

        Button retryButton = dialog.findViewById(R.id.retryButton);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                // 새로운 게임을 시작하는 인텐트 생성
                Intent intent = new Intent(oshelloActivity.this, oshelloActivity.class);
                startActivity(intent);
                finish(); // 현재의 게임 액티비티를 종료하여 새로운 게임을 시작합니다.
            }
        });

        dialog.show();
    }


    private void initializeBoard() {
        // 초기 상태를 설정
        board[3][3] = 1; // Black
        board[3][4] = 2; // White
        board[4][3] = 2; // White
        board[4][4] = 1; // Black
    }

    private void updateBoard() {
        // 오셀로 판을 그리드 레이아웃에 반영
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String buttonID = "cell_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                Button button = findViewById(resID);

                // 상태에 따라 버튼 색상 변경
                if (board[i][j] == 0) {
                    button.setBackgroundResource(R.drawable.g2_board);
                } else if (board[i][j] == 1) {
                    button.setBackgroundResource(R.drawable.black_g2);
                } else {
                    button.setBackgroundResource(R.drawable.white_g2);
                }
            }
        }
        // 게임이 종료되었는지 확인하고 승자 결정
        if (isGameOver()) {
            determineWinner();
        }
    }




    private boolean hasValidMove() {
        // 현재 플레이어에게 놓을 자리가 있는지 확인
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isValidMove(i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    private class CellClickListener implements View.OnClickListener {
        private int row;
        private int col;

        CellClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void onClick(View view) {
            // 셀을 클릭했을 때의 동작 구현
            if (board[row][col] == 0) { // 클릭한 셀이 빈 공간인 경우에만 동작
                if (isValidMove(row, col)) {
                    flipDots(row, col);
                    board[row][col] = currentPlayer;
                    switchPlayer();
                    updateBoard();
                }
            }
        }

        private void flipDots(int row, int col) {
            // 돌을 놓은 후 상대방 돌을 뒤집는 로직

            // 주변 8방향을 확인하여 뒤집을 돌이 있는지 확인하고 뒤집기
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0) {
                        continue; // 현재 위치는 제외
                    }

                    flipDirection(row, col, i, j);
                }
            }
        }

        private void flipDirection(int row, int col, int dirX, int dirY) {
            // 주어진 방향으로 뒤집을 돌이 있는지 확인하고 뒤집기
            int opponent = (currentPlayer == 1) ? 2 : 1;

            int x = row + dirX;
            int y = col + dirY;

            // 인덱스가 범위를 벗어나면 뒤집을 돌이 없음
            if (x < 0 || x >= 8 || y < 0 || y >= 8 || board[x][y] != opponent) {
                return;
            }

            // 상대방 돌을 찾았다면 계속해서 그 방향으로 뒤집기
            while (board[x][y] == opponent) {
                x += dirX;
                y += dirY;

                // 인덱스가 범위를 벗어나면 뒤집을 돌이 없음
                if (x < 0 || x >= 8 || y < 0 || y >= 8) {
                    return;
                }

                // 현재 플레이어의 돌이 있는 경우 뒤집기
                if (board[x][y] == currentPlayer) {
                    // 현재 위치에서 역방향으로 되돌아가면서 뒤집기
                    x -= dirX;
                    y -= dirY;

                    while (board[x][y] == opponent) {
                        board[x][y] = currentPlayer;
                        x -= dirX;
                        y -= dirY;
                    }

                    return;
                }
            }
        }

    }
    private boolean isValidMove(int row, int col) {
        // 클릭한 위치에 돌을 놓을 수 있는지 여부를 확인하는 로직

        // 현재 위치에 이미 돌이 놓여있으면 놓을 수 없음
        if (board[row][col] != 0) {
            return false;
        }

        // 주변 8방향을 확인하여 상대방 돌을 뒤집을 수 있는지 확인
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue; // 현재 위치는 제외
                }

                if (isValidDirection(row, col, i, j)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isValidDirection(int row, int col, int dirX, int dirY) {
        // 주어진 방향으로 뒤집을 돌이 있는지 확인
        int opponent = (currentPlayer == 1) ? 2 : 1;

        int x = row + dirX;
        int y = col + dirY;

        // 인덱스가 범위를 벗어나면 뒤집을 돌이 없음
        if (x < 0 || x >= 8 || y < 0 || y >= 8 || board[x][y] != opponent) {
            return false;
        }

        // 상대방 돌을 찾았다면 계속해서 그 방향으로 확인
        while (board[x][y] == opponent) {
            x += dirX;
            y += dirY;

            // 인덱스가 범위를 벗어나면 뒤집을 돌이 없음
            if (x < 0 || x >= 8 || y < 0 || y >= 8) {
                return false;
            }

            // 현재 플레이어의 돌이 있는 경우 뒤집을 수 있음
            if (board[x][y] == currentPlayer) {
                return true;
            }
        }

        return false;
    }
    private void switchPlayer() {
        // 플레이어를 변경하는 로직
        currentPlayer = (currentPlayer == 1) ? 2 : 1;

        // 현재 어느 색의 턴인지 실시간으로 업데이트
        updateTurnIndicator();
    }

    private void updateTurnIndicator() {
        // 현재 어느색의 턴인지 시각적으로 표현
        TextView whiteView = findViewById((R.id.whitetext));
        TextView blackView = findViewById((R.id.blacktext));

        if (currentPlayer == 1) {
            whiteView.setVisibility(View.INVISIBLE);
            blackView.setVisibility(View.VISIBLE);
        } else if (currentPlayer == 2) {
            whiteView.setVisibility(View.VISIBLE);
            blackView.setVisibility(View.INVISIBLE);
        }
    }
}

