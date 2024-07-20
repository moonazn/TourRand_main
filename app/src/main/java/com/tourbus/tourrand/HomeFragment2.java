package com.tourbus.tourrand;

import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;

public class HomeFragment2 extends Fragment {
    private MainActivity mainActivity;
    private Handler handler;
    String local, visited, color, result;
    int visitCnt,colorcode; //방문 횟수 카운트 하는 거 서버에서 값 넘겨 주는 형식 확인 해야 함

    private String[] VFullPath;
    TextView visitTxt;


    public HomeFragment2() {
        // Required empty public constructor
        //wkleh
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home2, container, false);

        // MainActivity 인스턴스 가져오기
       // mainActivity = (MainActivity) getActivity();

        ImageView mapImg = view.findViewById(R.id.map);
        visitTxt = view.findViewById(R.id.visitTxt);
        VectorChildFinder vector = new VectorChildFinder(getActivity(), R.drawable.map_home, mapImg);

        VectorDrawableCompat.VFullPath[] localVector = new VectorDrawableCompat.VFullPath[169];
        String[] pathNames = { "seoul",
                /*강원도 18*/   "kw_goseong","sokcho","yangyang","injae","yanggu",
                "gangneung","donghae","samcheok","taebeak","jeongseon",
                "pyeongchang","hongcheon","hoengseong","wonju","yeongwol",
                "hwacheon","cheorwon","chuncheon",
                /*경기도 31*/   "yeoncheon","pocheon", "gapyeong","yangpyeong","yeoju",
                "icheon","gwangju", "namyangju","hanam","guri",
                "uijeongbu","yangju", "dongducheon","paju","goyang",
                "gimpo","bucheon", "gwangmyeong","siheung","ansan",
                "gunpo","anyang", "uiwang","gwacheon","seongnam",
                "suwon","hwaseong", "pyeongtaeg","ansung","yongin",
                "osan",//0~49
                /*충북 13*/     "danyang", "jecheon","chungju","eumseong","jincheon",
                "goesan", "jeungpyeong","cheongju","boeun","ogcheon",
                "youngdong", "daejeon","sejong",
                /*충남 16*/     "cheonan","asan","yesan", "dangjin","seosan",
                "taean1","taean2","hongseong","cheongyang","bolyeong",
                "buyeo","nonsan","geumsan", "seocheon","gyelyong",
                "gongju",
                /*경북 23*/      "uljin","bonghwa", "youngju","yecheon","mungyeong",
                "sangju","andong", "yeongyang","yeongdeog","cheongsong",
                "pohang","gyeongju", "yeongcheon","gunwi","uiseong",
                "gumi","gincheon", "chilgog","seongju","goryeong",
                "daegu","gyeongsan", "cheongdo",
                /*경남 22*/      "geochang","hamyang","sancheong","habcheon", "changnyeong",
                "milyang","ulsan","uilyeong","haman", "gimhae",
                "busan","cheongwon","hadong","jinju", "sacheon",
                "kn_goseong","geoje","namhae","tongyeong1", "tongyeong2",
                "tongyeong3","yangsan",
                /*전북 15*/      "gunsan","igsan", "wanju1","wanju2","jinan",
                "muju","jangsu", "jeonju","gimjae","imsil",
                "namwon","sunchang", "jeongeub","buan","gochang",
                /*전남 26*/      "yeonggwang","jangseong", "damyang","gogseong","gulye",
                "gwangyang","suncheon", "yeosu","goheung","boseong",
                "hwasun","biggwangju", "hampyeong","naju","muan",
                "sinan1","sinan2", "mokpo","yeongam","jangheung",
                "gangjin","haenam", "wando1","wando2","wando3",
                "jindo",
                /*제주 1*/      "jeju",
                /*인천 3*/       "incheon","kanghwado","yeongjongdo"};


        for (int i = 0; i < localVector.length; i++) {
            localVector[i] = vector.findPathByName(pathNames[i]);
            //Log.d("제발",vector.findPathByName(pathNames[i]).toString());
        }
        mapImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float x = event.getX();
                    float y = event.getY();

                    // ImageView의 실제 크기와 Drawable의 크기 가져오기
                    int imgViewWidth = mapImg.getWidth();
                    int imgViewHeight = mapImg.getHeight();
                    Drawable drawable = mapImg.getDrawable();
                    int drawableWidth = drawable.getIntrinsicWidth();
                    int drawableHeight = drawable.getIntrinsicHeight();

                    // ImageView의 scaleType이 FIT_CENTER인 경우 스케일 비율 계산
                    float scaledX = x * (float)0.18* (float) drawableWidth / imgViewWidth;
                    float scaledY = y *(float)0.18* (float) drawableHeight / imgViewHeight;

                    Log.d("TouchEvent", "Original X: " + x + ", Y: " + y);
                    Log.d("TouchEvent", "Scaled X: " + scaledX + ", Y: " + scaledY);

                    // 각 VFullPath 객체에 대해 터치된 위치가 그 객체 내부에 있는지 확인합니다.
                    for (int i = 0; i < localVector.length; i++) {
                        VectorDrawableCompat.VFullPath path = localVector[i];
                        if (path != null && isPointInPath(path, scaledX, scaledY)) {
                            // 클릭된 VFullPath 객체가 확인되면 처리합니다.
                            onPathClicked(path);
                            return true;
                        }
                    }
                }
                return true;
            }
        });


      /*  //값이 제대로 넘어가고 받을 수 있는지는 서버랑 확인해봐야함
        String url = "http://13.209.33.141:5000";
        String inputText = "map_userInfo";
        String data = "{ \"content\" : \""+inputText+"\" }";

        new Thread(() -> {
            String result = mainActivity.httpPostBodyConnection(url, data);
            // 처리 결과 확인
            handler.post(() -> mainActivity.seeNetworkResult(result));
        }).start();*/


       /* result = "apple,banana,cherry";
        String[] parts = result.split(",");

        String first = parts[0];
        String second = parts[1];
        String third = parts[2];*/
        visited = "서울";
        colorcode = 1;
        visitCnt = 2;

        switch (visited){

            //서울1
            case "서울":
                localVector[0].setFillColor(colorRC(colorcode,visitCnt));
                break;

            //강원도
            case "강_고성" :
                localVector[1].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "속초" :
                localVector[2].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "양양":
                localVector[3].setFillColor(colorRC(colorcode,visitCnt));
                break;

            case "인제":
                localVector[4].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "양구" :
                localVector[5].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "강릉" :
                localVector[6].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "동해" :
                localVector[7].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "삼척" :
                localVector[8].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "태백" :
                localVector[9].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "정선" :
                localVector[10].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "평창" :
                localVector[11].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "홍천" :
                localVector[12].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "횡성" :
                localVector[13].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "원주" :
                localVector[14].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "영월" :
                localVector[15].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "화천" :
                localVector[16].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "철원" :
                localVector[17].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "춘천" :
                localVector[18].setFillColor(colorRC(colorcode,visitCnt));
                break;

            //경기도
            case "연천" :
                localVector[19].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "포천" :
                localVector[20].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "가평" :
                localVector[21].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "양평" :
                localVector[22].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "여주" :
                localVector[23].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "이천" :
                localVector[24].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "광주" :
                localVector[25].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "남양주" :
                localVector[26].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "하남" :
                localVector[27].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "구리" :
                localVector[28].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "의정부" :
                localVector[29].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "양주" :
                localVector[30].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "동두천" :
                localVector[31].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "파주" :
                localVector[32].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "고양" :
                localVector[33].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "김포" :
                localVector[34].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "부천" :
                localVector[35].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "광명" :
                localVector[36].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "시흥" :
                localVector[37].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "안산" :
                localVector[38].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "군포" :
                localVector[39].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "안양" :
                localVector[40].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "의왕" :
                localVector[41].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "과천" :
                localVector[42].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "성남" :
                localVector[43].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "수원" :
                localVector[44].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "화성" :
                localVector[45].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "평택" :
                localVector[46].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "안성" :
                localVector[47].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "용인" :
                localVector[48].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "오산" :
                localVector[49].setFillColor(colorRC(colorcode,visitCnt));
                break;

            //충북
            case "단양" :
                localVector[50].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "제천" :
                localVector[51].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "충주" :
                localVector[52].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "음성" :
                localVector[53].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "진천" :
                localVector[54].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "괴산" :
                localVector[55].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "증평" :
                localVector[56].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "청주" :
                localVector[57].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "보은" :
                localVector[58].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "옥천" :
                localVector[59].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "영동" :
                localVector[60].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "대전" :
                localVector[61].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "세종" :
                localVector[62].setFillColor(colorRC(colorcode,visitCnt));
                break;

            //충남
            case "천안" :
                localVector[63].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "아산" :
                localVector[64].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "예산" :
                localVector[65].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "당진" :
                localVector[66].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "서산" :
                localVector[67].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "태안" :
                localVector[68].setFillColor(colorRC(colorcode,visitCnt));
                localVector[69].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "홍성" :
                localVector[70].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "청양" :
                localVector[71].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "보령" :
                localVector[72].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "부여" :
                localVector[73].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "논산" :
                localVector[74].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "금산" :
                localVector[75].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "서천" :
                localVector[76].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "계룡" :
                localVector[77].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "공주" :
                localVector[78].setFillColor(colorRC(colorcode,visitCnt));
                break;

            //경북
            case "울진" :
                localVector[79].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "봉화" :
                localVector[80].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "영주" :
                localVector[81].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "예천" :
                localVector[82].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "문경" :
                localVector[83].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "상주" :
                localVector[84].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "안동" :
                localVector[85].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "영양" :
                localVector[86].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "영덕" :
                localVector[87].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "청송" :
                localVector[88].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "포항" :
                localVector[89].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "경주" :
                localVector[90].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "영천" :
                localVector[91].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "군위" :
                localVector[92].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "의성" :
                localVector[93].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "구미" :
                localVector[94].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "김천" :
                localVector[95].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "칠곡" :
                localVector[96].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "성주" :
                localVector[97].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "고령" :
                localVector[98].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "대구" :
                localVector[99].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "경산" :
                localVector[100].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "청도" :
                localVector[101].setFillColor(colorRC(colorcode,visitCnt));
                break;

            //경남
            case "거창" :
                localVector[102].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "함양" :
                localVector[103].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "산청" :
                localVector[104].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "합천" :
                localVector[105].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "창녕" :
                localVector[106].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "밀양" :
                localVector[107].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "울산" :
                localVector[108].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "의령" :
                localVector[109].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "함안" :
                localVector[110].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "김해" :
                localVector[111].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "부산" :
                localVector[112].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "창원" :
                localVector[113].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "하동" :
                localVector[114].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "진주" :
                localVector[115].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "사천" :
                localVector[116].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "경_고성" :
                localVector[117].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "거제" :
                localVector[118].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "남해" :
                localVector[119].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "통영" :
                localVector[120].setFillColor(colorRC(colorcode,visitCnt));
                localVector[121].setFillColor(colorRC(colorcode,visitCnt));
                localVector[122].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "양산" :
                localVector[123].setFillColor(colorRC(colorcode,visitCnt));
                break;

            //전북
            case "군산" :
                localVector[124].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "익산" :
                localVector[125].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "완주" :
                localVector[126].setFillColor(colorRC(colorcode,visitCnt));
                localVector[127].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "진안" :
                localVector[128].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "무주" :
                localVector[129].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "장수" :
                localVector[130].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "전주" :
                localVector[131].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "김제" :
                localVector[132].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "임실" :
                localVector[133].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "남원" :
                localVector[134].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "순창" :
                localVector[135].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "정읍" :
                localVector[136].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "부안" :
                localVector[137].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "고창" :
                localVector[138].setFillColor(colorRC(colorcode,visitCnt));
                break;

            //전남
            case "영광" :
                localVector[139].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "장성" :
                localVector[140].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "담양" :
                localVector[141].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "곡성" :
                localVector[142].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "구례" :
                localVector[143].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "광양" :
                localVector[144].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "순천" :
                localVector[145].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "여수" :
                localVector[146].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "고흥" :
                localVector[147].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "보성" :
                localVector[148].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "화순" :
                localVector[149].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "광주광역시" :
                localVector[150].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "함평" :
                localVector[151].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "나주" :
                localVector[152].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "무안" :
                localVector[153].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "신안" :
                localVector[154].setFillColor(colorRC(colorcode,visitCnt));
                localVector[155].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "목포" :
                localVector[156].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "영암" :
                localVector[157].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "장흥" :
                localVector[158].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "강진" :
                localVector[159].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "해남" :
                localVector[160].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "완도" :
                localVector[161].setFillColor(colorRC(colorcode,visitCnt));
                localVector[162].setFillColor(colorRC(colorcode,visitCnt));
                localVector[163].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "진도" :
                localVector[164].setFillColor(colorRC(colorcode,visitCnt));
                break;

            //제주
            case "제주도" :
                localVector[165].setFillColor(colorRC(colorcode,visitCnt));
                break;

            case "인천" :
                localVector[166].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "강화도" :
                localVector[167].setFillColor(colorRC(colorcode,visitCnt));
                break;
            case "영종도" :
                localVector[168].setFillColor(colorRC(colorcode,visitCnt));
                break;
        }

        mapImg.invalidate();
        return view;
    }

    public int colorRC(int colorcode,int visitCount ){
        // 색상 배열
        int[][] colorArrays = {
                {R.color.pink, R.color.pink2, R.color.pink3, R.color.pink4, R.color.pink5},
                {R.color.skyblue, R.color.skyblue2, R.color.skyblue3, R.color.skyblue4, R.color.skyblue5},
                {R.color.purple, R.color.purple2, R.color.purple3, R.color.purple4, R.color.purple5},
                {R.color.yellow, R.color.yellow2, R.color.yellow3, R.color.yellow4, R.color.yellow5}
        };

       // Random random = new Random();
       // int randomColorIndex = random.nextInt(4);

        int randomColorResId;
        if(visitCount == 1) {
            randomColorResId = colorArrays[colorcode][0];
        } else if (visitCount == 2) {
            randomColorResId = colorArrays[colorcode][1];

        } else if (visitCount == 3) {
            randomColorResId = colorArrays[colorcode][2];

        } else if (visitCount == 4) {
            randomColorResId = colorArrays[colorcode][3];

        } else {
            randomColorResId = colorArrays[colorcode][4];
        }

        int randomColor = ContextCompat.getColor(getActivity(), randomColorResId);
        return randomColor;
    }
    // 터치된 위치가 VFullPath 객체 내부에 있는지 확인하는 함수
    private boolean isPointInPath(VectorDrawableCompat.VFullPath path, float x, float y) {
        Path pathObject = new Path();
        path.toPath(pathObject);
        RectF bounds = new RectF();
        pathObject.computeBounds(bounds, true);
        Region region = new Region();
        region.setPath(pathObject, new Region((int) bounds.left, (int) bounds.top, (int) bounds.right, (int) bounds.bottom));

        boolean contains = region.contains((int) x, (int) y);
        Log.d("isPointInPath", ", X: " + x + ", Y: " + y + ", Bounds: " + bounds);

        return contains;
    }

    // VFullPath 객체가 클릭되었을 때 호출되는 함수
    private void onPathClicked(VectorDrawableCompat.VFullPath path) {
        // 클릭 이벤트를 처리합니다.
        // 예: 토스트 메시지 출력
        visitTxt.setText("방문지 : " + visited + "\n방문 횟수 : "+visitCnt);
        Log.d("onPathClicked", "Path clicked: " + visited);
    }

}
