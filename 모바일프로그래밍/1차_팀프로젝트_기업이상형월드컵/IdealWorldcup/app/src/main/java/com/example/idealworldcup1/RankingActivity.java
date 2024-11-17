package com.example.idealworldcup1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class RankingActivity extends Activity {
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking);

        //앞의 페이지에서 전달한 HowManyChecked, imageResources 배열들을 받는다.
        Intent intent = getIntent();
        int[] HowManyChecked = intent.getIntArrayExtra("HowManyChecked");
        int[] imageResources = intent.getIntArrayExtra("imageResources");

        TextView TextV3;
        TextV3 = (TextView) findViewById(R.id.TextV3);

        ImageView RankingImg1,RankingImg2,RankingImg3,RankingImg4,RankingImg5,RankingImg6,RankingImg7,RankingImg8;
        RankingImg1 = (ImageView) findViewById(R.id.RankingImg1);

        Button btnSave = (Button) findViewById(R.id.btnSave);

        int temp1=0,temp2=0;  //4강 전에서 선택받지 못한 4개의 이미지, 2강 전에서 선택받지 못한 2개의 이미지를 이미지뷰에 구현할 때, 이미 이미지가 들어간 이미지뷰에 새로

        int j =0;
        // 이미지를 넣을 수 도 있으므로 넣어둠 // 나중에 사용할 수도 있으니 일단 킵
        for(int i =0;i<8;i++)   //8번 돌리며 이미지를 채워 넣기
        {
            if(HowManyChecked[i]==3)        //HowManyChecked가 3인 이미지, 즉, 3번 선택 받은 이미지, 1등인 이미지이다.
            {
                RankingImg1.setImageResource(imageResources[i]);
                j=i+1;//이미지 뷰에 구현
            }

        }

        int finalJ = j;
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

                // 이미지 순서 값을 가져오기
                int currentRanking1 = sharedPreferences.getInt("rankingimg1", 0);
                int currentRanking2 = sharedPreferences.getInt("rankingimg2", 0);
                int currentRanking3 = sharedPreferences.getInt("rankingimg3", 0);
                int currentRanking4 = sharedPreferences.getInt("rankingimg4", 0);
                int currentRanking5 = sharedPreferences.getInt("rankingimg5", 0);
                int currentRanking6 = sharedPreferences.getInt("rankingimg6", 0);
                int currentRanking7 = sharedPreferences.getInt("rankingimg7", 0);
                int currentRanking8 = sharedPreferences.getInt("rankingimg8", 0);



                if (finalJ == 1)
                {
                    currentRanking1++;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("rankingimg1", currentRanking1);
                    editor.apply();
                }
                if (finalJ == 2)
                {
                    currentRanking2++;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("rankingimg2", currentRanking2);
                    editor.apply();
                }
                if (finalJ == 3)
                {
                    currentRanking3++;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("rankingimg3", currentRanking3);
                    editor.apply();
                }
                if (finalJ == 4)
                {
                    currentRanking4++;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("rankingimg4", currentRanking4);
                    editor.apply();
                }
                if (finalJ == 5)
                {
                    currentRanking5++;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("rankingimg5", currentRanking5);
                    editor.apply();
                }
                if (finalJ == 6)
                {
                    currentRanking6++;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("rankingimg6", currentRanking6);
                    editor.apply();
                }
                if (finalJ == 7)
                {
                    currentRanking7++;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("rankingimg7", currentRanking7);
                    editor.apply();
                }
                if (finalJ == 8)
                {
                    currentRanking8++;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("rankingimg8", currentRanking8);
                    editor.apply();
                }



                // 처음 화면으로 이동 (MainActivity.class는 실제로 사용하는 화면으로 변경해야 함)
                Intent intent = new Intent(RankingActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // 현재 화면 종료
            }
        });

    }
}
