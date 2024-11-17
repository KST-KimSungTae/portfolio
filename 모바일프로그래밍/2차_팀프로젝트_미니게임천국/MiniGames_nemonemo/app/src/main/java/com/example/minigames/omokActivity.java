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

public class omokActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private TextView blackText, whiteText;
    private Button passButton, passButton2;
    private boolean isBlackTurn = true; // true면 흑돌 차례, false면 백돌 차례
    private int[][] cellType; // 각 셀의 상태를 나타내는 배열 (0: 비어 있음, 1: 흑돌, 2: 백돌)
    private static final int WINNING_COUNT = 5; // 승리를 위해 필요한 돌의 개수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.omok_game);

        gridLayout = findViewById(R.id.gridLayout);
        blackText = findViewById(R.id.blacktext);
        whiteText = findViewById(R.id.whitetext);
        passButton = findViewById(R.id.passButton);
        passButton2 = findViewById(R.id.passButton2);

        // cellType 배열 초기화
        cellType = new int[15][15];

        // 모든 버튼에 대한 클릭 리스너 등록
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View child = gridLayout.getChildAt(i);
            if (child instanceof Button) {
                final Button button = (Button) child;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onCellClick(button);
                    }
                });

                button.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        onCellLongClick(button);
                        return true; // 이벤트 소비
                    }
                });
            }
        }

        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBlackTurn == false) {
                    onPassButtonClick();
                }
            }
        });

        passButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBlackTurn == true) {
                    onPassButtonClick();
                }
            }
        });
        blackText.setVisibility(View.VISIBLE);
        whiteText.setVisibility(View.INVISIBLE);
    }
    private boolean checkWin(int row, int col) {
        int currentPlayer = cellType[row][col];

        // 가로 방향 체크
        if (checkDirection(row, col, 0, 1, currentPlayer)) {
            return true;
        }

        // 세로 방향 체크
        if (checkDirection(row, col, 1, 0, currentPlayer)) {
            return true;
        }

        // 대각선 방향 체크 (왼쪽 상단에서 오른쪽 하단으로)
        if (checkDirection(row, col, 1, 1, currentPlayer)) {
            return true;
        }

        // 대각선 방향 체크 (오른쪽 상단에서 왼쪽 하단으로)
        if (checkDirection(row, col, 1, -1, currentPlayer)) {
            return true;
        }

        return false;
    }

    private boolean checkDirection(int row, int col, int rowIncrement, int colIncrement, int currentPlayer) {
        int count = 0;

        // 현재 위치에서부터 주어진 방향으로 진행하면서 돌의 개수를 셈
        for (int i = -WINNING_COUNT + 1; i < WINNING_COUNT; i++) {
            int newRow = row + i * rowIncrement;
            int newCol = col + i * colIncrement;

            if (isValidPosition(newRow, newCol) && cellType[newRow][newCol] == currentPlayer) {
                count++;
            } else {
                count = 0;
            }

            if (count == WINNING_COUNT) {
                return true; // 승리 조건 충족
            }
        }

        return false;
    }

    private boolean isValidPosition(int row, int col) {
        // 주어진 행과 열이 유효한지 확인
        return row >= 0 && row < cellType.length && col >= 0 && col < cellType[0].length;
    }

    private void onCellClick(Button button) {
        int row = getRowIndex(button);
        int col = getColumnIndex(button);

        if (cellType[row][col] == 0) {
            // 버튼이 비어 있을 때만 돌을 놓을 수 있도록 함
            if (isBlackTurn) {
                button.setText("●"); // 흑돌 표시
                cellType[row][col] = 1; // 흑돌 표시
                blackText.setVisibility(View.INVISIBLE);
                whiteText.setVisibility(View.VISIBLE);
            } else {
                button.setText("●");
                button.setTextColor(getResources().getColor(android.R.color.white)); // 흑돌을 색을 변경하여 백돌 표시
                cellType[row][col] = 2; // 백돌 표시
                blackText.setVisibility(View.VISIBLE);
                whiteText.setVisibility(View.INVISIBLE);
            }

            if (checkWin(row, col)) {
                final Dialog dialog = new Dialog(omokActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_victory);

                TextView winnerText = dialog.findViewById(R.id.winnerText);
                String winner = isBlackTurn ? "흑돌" : "백돌";
                winnerText.setText(winner + "이(가) 승리했습니다!");

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
                        Intent intent = new Intent(omokActivity.this, omokActivity.class);
                        startActivity(intent);
                        finish(); // 현재의 게임 액티비티를 종료하여 새로운 게임을 시작합니다.
                    }
                });

                dialog.show();
            }
            else {
                isBlackTurn = !isBlackTurn; // 턴 변경
            }

        }
    }

    private void onCellLongClick(Button button) {
        // 해당 셀 길게 클릭 시 돌을 없애는 로직 추가
        int row = getRowIndex(button);
        int col = getColumnIndex(button);

        button.setText("");
        cellType[row][col] = 0; // 셀 비우기
    }

    private void onPassButtonClick() {
        // 턴을 넘기는 로직 추가
        isBlackTurn = !isBlackTurn; // 턴 변경
        updateTurnIndicator();
    }

    private void updateTurnIndicator() {
        if (isBlackTurn) {
            blackText.setVisibility(View.VISIBLE);
            whiteText.setVisibility(View.INVISIBLE);
        } else {
            blackText.setVisibility(View.INVISIBLE);
            whiteText.setVisibility(View.VISIBLE);
        }
    }

    private int getRowIndex(Button button) {
        // 버튼이 속한 행 인덱스를 반환
        int index = gridLayout.indexOfChild(button);
        return index / gridLayout.getColumnCount();
    }

    private int getColumnIndex(Button button) {
        // 버튼이 속한 열 인덱스를 반환
        int index = gridLayout.indexOfChild(button);
        return index % gridLayout.getColumnCount();
    }
}