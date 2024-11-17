package com.example.idealworldcup1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Random;

import androidx.annotation.Nullable;

public class subActivity extends Activity {


    private int currentRound = 1;
    private int totalRounds = 7; // 이상형 월드컵의 총 라운드 수
    private  int imageIndex1,imageIndex2;

    private int[] imageResources = {
            R.drawable.celeb8, R.drawable.celeb2, R.drawable.celeb3, R.drawable.celeb4,
            R.drawable.celeb5, R.drawable.celeb6, R.drawable.celeb7, R.drawable.celeb8
    };

    private int[] AlreadyChecked = {0,0,0,0,0,0,0,0};
    private int[] HowManyChecked = {0,0,0,0,0,0,0,0};
    //이미지가 화면에 뜨면 뜬 2개의 이미지들의 imageResources 배열에서의 위치와 같은 위치의 AlreadyChecked의 수가 1 오른다 == 이를 통해 해당 번호의 이미지가 몇번 화면에 등장했는지 기억한다.
    //이미지가 선택되면 선택된 이미지의 imageResources 배열에서의 위치와 같은 위치의 HowManyChecked의 수가 1 오른다 == 이를 통해 해당 번호의 이미지가 몇번 선택되었는지 알 수 있다.

    ImageButton ImageBtn1,ImageBtn2;
    TextView TextV2;
    SeekBar SeekBar1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);

        ImageBtn1 = (ImageButton) findViewById(R.id.imageButton1);
        ImageBtn2 = (ImageButton) findViewById(R.id.imageButton2);

        SeekBar1 = (SeekBar) findViewById(R.id.SeekBar1);

        TextV2 = (TextView) findViewById(R.id.textV2);
        //현재 몇 강의 몇 라운드인지 표시
        TextV2.setText("이상형 월드컵 8강 "+currentRound+"/4");

        ImageBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleUserChoice(imageIndex1);
            }
        });
        ImageBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleUserChoice(imageIndex2);
            }
        });
        // 초기 이미지 설정
        setImageForCurrentRound();
    }

    private void handleUserChoice(int selectedImage) {
        // 여기에서 사용자의 선택을 처리할 로직을 추가
        // 예를 들어, 사용자가 선택한 이미지를 기록하거나 결과를 저장

        if (currentRound < totalRounds) {
            // 다음 라운드로 이동
            currentRound++;
            //이미 월드컵에 나온 imageIndex1,imageIndex2가 이미 나왔음을 표시한다.
            AlreadyChecked[imageIndex1]++;
            AlreadyChecked[imageIndex2]++;
            //선택된 이미지가 몇 번 선택되었는지 확인하기 위해 HowManyChecked에 현재 선택된 이미지와 같은 위치의 수에 1을 더한다.
            HowManyChecked[selectedImage] = HowManyChecked[selectedImage]+1;

            int RoundsTemp = 4,currentTemp=0;  //몇 강, 몇 라운드인지 표시하기 위한 변수 (그냥 totalRounds, currentRounds로 하면은 라운드의 반복에서 꼬임)
            switch (currentRound)
            {
                case 1:
                case 2:
                case 3:
                case 4:
                    RoundsTemp=4;       //현재 4강 진행 중.. 표시
                    currentTemp=currentRound;  //현재 몇 번째 라운드인지 표시
                    break;
                case 5:
                    currentTemp=0;          //4강의 4에서 2강으로 넘어왔으니 라운드 초기화
                    //AlreadyChecked에서 선택을 받은 이미지만 골라 다음 라운드로 넘긴다.
                    for (int i=0;i<8;i++)
                    {
                        if(HowManyChecked[i]==1)        //HowManyChecked ==1 만약 이 순서의 이미지가 선택을 받았다면 1, 아니면 0이기에 구분한다.
                        {
                            AlreadyChecked[i]=0;        //선택 받은 이미지의 AlreadyChecked 즉, 이 순서의 이미지가 중복 혹은 이미 탈락이 아닌 사용해야 하는 이미지인지 구분하는 배열에서
                                                        //이 이미지는 사용해야 하는 이미지임을 0으로 표시한다.
                        }
                    }
                case 6:
                    RoundsTemp=2;           //현재 2강 진행 중... 표시
                    currentTemp=currentRound-4;     //현재 라운드 표시
                    break;
                case 7:
                    RoundsTemp=1;           //현재 1강 진행 중 표시
                    currentTemp=currentRound-6;     //현재 라운드 표시
                    //2강에서 선택된 이미지만 사용할 수 있게 2강에서 선택된 이미지들에 사용 가능 표시를 함
                    for (int i=0;i<8;i++)
                    {
                        if(HowManyChecked[i]==2)
                        {
                            AlreadyChecked[i]=0;
                        }
                    }
                    break;
            }
            //SeekBar 조작
            SeekBar1.setProgress(currentRound);

            //현재 라운드 표시
            TextV2.setText("이상형 월드컵 "+RoundsTemp*2+"강 "+currentTemp+"/"+RoundsTemp);
            if(RoundsTemp==1)
            {
                TextV2.setText("이상형 월드컵 결승전"+currentTemp+"/"+RoundsTemp);
            }
            //이미지 넣기
            setImageForCurrentRound();

        } else {
            //이미 월드컵에 나온 imageIndex1,imageIndex2가 이미 나왔음을 표시한다.
            AlreadyChecked[imageIndex1]++;
            AlreadyChecked[imageIndex2]++;
            //선택된 이미지가 몇 번 선택되었는지 확인하기 위해 HowManyChecked에 현재 선택된 이미지와 같은 위치의 수에 1을 더한다.
            HowManyChecked[selectedImage] = HowManyChecked[selectedImage]+1;

            // 모든 라운드가 끝나면 결과를 표시하거나 알림을 표시
            // 원하는 대회 종료 로직을 구현
            //랭킹 페이지로 이동하는 intent 선언
            Intent intent2 = new Intent(getApplicationContext(),RankingActivity.class);
            //랭킹 페이지에서 사용할 HowManyChecked와 imageResources를 intent에 포함 시켜 다음 페이지에서도 사용할 수 있게하기
            intent2.putExtra("HowManyChecked",HowManyChecked);
            intent2.putExtra("imageResources",imageResources);
            //랭킹 페이지 실행
            startActivity(intent2);

        }
    }

    private void setImageForCurrentRound() {
        // 8개 중에서 무작위로 2개의 이미지 선택
        Random random = new Random();
        imageIndex1 = random.nextInt(imageResources.length);
        imageIndex2 = random.nextInt(imageResources.length);

        // 같은 이미지가 선택되는 것을 방지
        while (true) {
            if(AlreadyChecked[imageIndex1]==1) //imageIndex1의 순서의 이미지가 사용해도 되는 이미지인지 확인한다.
            {                                   //만약 1이라면 이 순서의 이미지는 이미 사용한 중복되는 이미지이거나 탈락된 이미지이다.
                imageIndex1 = random.nextInt(imageResources.length);
            }
            else if(AlreadyChecked[imageIndex2]==1)
            {
                imageIndex2 = random.nextInt(imageResources.length);
            }
            else if(imageIndex1==imageIndex2) {
                imageIndex2 = random.nextInt(imageResources.length);
            }
            else {
                break;
            }
        }

        ImageBtn1.setImageResource(imageResources[imageIndex1]);
        ImageBtn2.setImageResource(imageResources[imageIndex2]);
    }
}

