package com.example.idealworldcup1;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;


public class Total_rank extends Activity {
    ImageView firstPlaceImage;
    ImageView secondPlaceImage;
    ImageView thirdPlaceImage;
    TextView textView1;
    TextView textView2;
    TextView textView3;
    Button button1;
    Button button2;

    String[] imageNames = {
            "네이버", "당근", "라인", "배민", "카카오", "직방", "쿠팡", "토스"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.totalrank);

        // 이미지뷰와 텍스트뷰 초기화
        firstPlaceImage = findViewById(R.id.imageView1);
        secondPlaceImage = findViewById(R.id.imageView2);
        thirdPlaceImage = findViewById(R.id.imageView3);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);

        class ImageRank {
            private int imageResource;
            private int ranking;
            private String imageName;

            public ImageRank(int imageResource, int ranking, String imageName) {
                this.imageResource = imageResource;
                this.ranking = ranking;
                this.imageName = imageName;
            }

            public int getImageResource() {
                return imageResource;
            }

            public int getRanking() {
                return ranking;
            }

            public String getImageName() {
                return imageName;
            }
        }

        ArrayList<ImageRank> imageRanks = new ArrayList<>();

        // SharedPreferences에서 이미지 순서를 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        for (int i = 0; i < 8; i++) {
            int currentRanking = sharedPreferences.getInt("rankingimg" + (i + 1), 0);
            int imageResource = getResources().getIdentifier("celeb" + (i + 1), "drawable", getPackageName());
            String imageName = imageNames[i];
            imageRanks.add(new ImageRank(imageResource, currentRanking, imageName));
        }

        // 랭킹을 기준으로 이미지 정보를 내림차순으로 정렬
        Collections.sort(imageRanks, (rank1, rank2) -> Integer.compare(rank2.getRanking(), rank1.getRanking()));

        // 상위 3개 이미지 및 순위를 출력
        if (!imageRanks.isEmpty()) {
            firstPlaceImage.setImageResource(imageRanks.get(0).getImageResource());
            textView1.setText("1위: " + imageRanks.get(0).getImageName() + " - " + imageRanks.get(0).getRanking()+"번 선택함");
        }
        if (imageRanks.size() > 1) {
            secondPlaceImage.setImageResource(imageRanks.get(1).getImageResource());
            textView2.setText("2위: " + imageRanks.get(1).getImageName() + " - " + imageRanks.get(1).getRanking()+"번 선택함");
        }
        if (imageRanks.size() > 2) {
            thirdPlaceImage.setImageResource(imageRanks.get(2).getImageResource());
            textView3.setText("3위: " + imageRanks.get(2).getImageName() + " - " + imageRanks.get(2).getRanking()+"번 선택함");
        }

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // SharedPreferences 리셋
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // 모든 이미지의 랭킹을 0으로 리셋
                for (int i = 0; i < 8; i++) {
                    editor.putInt("rankingimg" + (i + 1), 0);
                }

                // 변경사항을 저장
                editor.apply();

                // 이미지뷰와 텍스트뷰도 리셋
                firstPlaceImage.setImageResource(0);
                secondPlaceImage.setImageResource(0);
                thirdPlaceImage.setImageResource(0);
                textView1.setText("");
                textView2.setText("");
                textView3.setText("");

                // 액티비티 종료
                finish();
            }
        });


    }
}
