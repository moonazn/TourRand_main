package com.tourbus.tourrand;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;
import com.tourbus.tourrand.R;

import java.util.Random;

public class HomeFragment2 extends Fragment {
    private MainActivity mainActivity;
    private Handler handler;
    String local, visited, color, result;
    int visitCnt; //방문 횟수 카운트 하는 거 서버에서 값 넘겨 주는 형식 확인 해야 함

    private String[] VFullPath;


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
        VectorChildFinder vector = new VectorChildFinder(getActivity(), R.drawable.map, mapImg);

        VectorDrawableCompat.VFullPath[] localVector = new VectorDrawableCompat.VFullPath[169];
        String[] pathNames = { "seoul",
                /*강원도 18*/   "kw_goseong","sokcho","yangyang","injae","yanggu",
                "gangneung","donghae","samcheok","taebeak","jeongseon",
                "pyeongchang","hongcheon","hoengseong","wonju","yeongwol",
                "hwacheon","cheorwon","chuncheon",
                /*경기도 31*/   "yeoncheon","pocheon", "gapyeong","yangpyeong","yeoju",
                "icheon","gwangju", "namyangju","hanam","guli",
                "uijeongbu","yangju", "dongducheon","paju","goyang",
                "gimpo","bucheon", "gwangmyeong","siheung","ansan",
                "gunpo","anyang", "uiwang","gwacheon","seongnam",
                "suwon","hwaseong", "pyeongtaeg","ansung","yongin",
                "osan",//0~49
                /*충북 13*/     "danyang", "jecheon","chungju","eumseong","jincheon",
                "goeseong", "jeungpyeong","cheongju","boeun","ogcheon",
                "youngdong", "daejeon","sejong",
                /*충남 16*/     "cheonan","asan","yesan", "dangjin","seosan",
                "taean1","taean2","hongseong","cheongyang","bolyeong",
                "buyeo","nonsan","geumsan", "seocheon","gyelyong",
                "gongju",
                /*경북 23*/      "uljin","bonghwa", "youngju","yecheon","mungyeong",
                "sangju","andong", "yeongyang","yeongdeog","cheongsong",
                "pohang","gyeongju", "yeongcheon","gunwi","uiseong",
                "gumi","gincheon", "chilgog","seongju","golyeong",
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
        visited = "busan";

        switch (visited){

            //서울1
            case "seoul":
                localVector[0].setFillColor(colorRC());
                break;

            //강원도
            case "kw_goseong" :
                localVector[1].setFillColor(colorRC());
                break;
            case "sokcho" :
                localVector[2].setFillColor(colorRC());
                break;
            case "yangyang":
                localVector[3].setFillColor(colorRC());
                break;

            case "injae":
                localVector[4].setFillColor(colorRC());
                break;
            case "yanggu" :
                localVector[5].setFillColor(colorRC());
                break;
            case "gangneung" :
                localVector[6].setFillColor(colorRC());
                break;
            case "donghae" :
                localVector[7].setFillColor(colorRC());
                break;
            case "samcheok" :
                localVector[8].setFillColor(colorRC());
                break;
            case "taebeak" :
                localVector[9].setFillColor(colorRC());
                break;
            case "jeongseon" :
                localVector[10].setFillColor(colorRC());
                break;
            case "pyeongchang" :
                localVector[11].setFillColor(colorRC());
                break;
            case "hongcheon" :
                localVector[12].setFillColor(colorRC());
                break;
            case "hoengseong" :
                localVector[13].setFillColor(colorRC());
                break;
            case "wonju" :
                localVector[14].setFillColor(colorRC());
                break;
            case "yeongwol" :
                localVector[15].setFillColor(colorRC());
                break;
            case "hwacheon" :
                localVector[16].setFillColor(colorRC());
                break;
            case "cheorwon" :
                localVector[17].setFillColor(colorRC());
                break;
            case "chuncheon" :
                localVector[18].setFillColor(colorRC());
                break;

            //경기도
            case "yeoncheon" :
                localVector[19].setFillColor(colorRC());
                break;
            case "pocheon" :
                localVector[20].setFillColor(colorRC());
                break;
            case "gapyeong" :
                localVector[21].setFillColor(colorRC());
                break;
            case "yangpyeong" :
                localVector[22].setFillColor(colorRC());
                break;
            case "yeoju" :
                localVector[23].setFillColor(colorRC());
                break;
            case "icheon" :
                localVector[24].setFillColor(colorRC());
                break;
            case "gwangju" :
                localVector[25].setFillColor(colorRC());
                break;
            case "namyangju" :
                localVector[26].setFillColor(colorRC());
                break;
            case "hanam" :
                localVector[27].setFillColor(colorRC());
                break;
            case "guli" :
                localVector[28].setFillColor(colorRC());
                break;
            case "uijeongbu" :
                localVector[29].setFillColor(colorRC());
                break;
            case "yangju" :
                localVector[30].setFillColor(colorRC());
                break;
            case "dongducheon" :
                localVector[31].setFillColor(colorRC());
                break;
            case "paju" :
                localVector[32].setFillColor(colorRC());
                break;
            case "goyang" :
                localVector[33].setFillColor(colorRC());
                break;
            case "gimpo" :
                localVector[34].setFillColor(colorRC());
                break;
            case "bucheon" :
                localVector[35].setFillColor(colorRC());
                break;
            case "gwangmyeong" :
                localVector[36].setFillColor(colorRC());
                break;
            case "siheung" :
                localVector[37].setFillColor(colorRC());
                break;
            case "ansan" :
                localVector[38].setFillColor(colorRC());
                break;
            case "gunpo" :
                localVector[39].setFillColor(colorRC());
                break;
            case "anyang" :
                localVector[40].setFillColor(colorRC());
                break;
            case "uiwang" :
                localVector[41].setFillColor(colorRC());
                break;
            case "gwacheon" :
                localVector[42].setFillColor(colorRC());
                break;
            case "seongnam" :
                localVector[43].setFillColor(colorRC());
                break;
            case "suwon" :
                localVector[44].setFillColor(colorRC());
                break;
            case "hwaseong" :
                localVector[45].setFillColor(colorRC());
                break;
            case "pyeongtaeg" :
                localVector[46].setFillColor(colorRC());
                break;
            case "ansung" :
                localVector[47].setFillColor(colorRC());
                break;
            case "yongin" :
                localVector[48].setFillColor(colorRC());
                break;
            case "osan" :
                localVector[49].setFillColor(colorRC());
                break;

            //충북
            case "danyang" :
                localVector[50].setFillColor(colorRC());
                break;
            case "jecheon" :
                localVector[51].setFillColor(colorRC());
                break;
            case "chungju" :
                localVector[52].setFillColor(colorRC());
                break;
            case "eumseong" :
                localVector[53].setFillColor(colorRC());
                break;
            case "jincheon" :
                localVector[54].setFillColor(colorRC());
                break;
            case "goeseong" :
                localVector[55].setFillColor(colorRC());
                break;
            case "jeungpyeong" :
                localVector[56].setFillColor(colorRC());
                break;
            case "cheongju" :
                localVector[57].setFillColor(colorRC());
                break;
            case "boeun" :
                localVector[58].setFillColor(colorRC());
                break;
            case "ogcheon" :
                localVector[59].setFillColor(colorRC());
                break;
            case "youngdong" :
                localVector[60].setFillColor(colorRC());
                break;
            case "daejeon" :
                localVector[61].setFillColor(colorRC());
                break;
            case "sejong" :
                localVector[62].setFillColor(colorRC());
                break;

            //충남
            case "cheonan" :
                localVector[63].setFillColor(colorRC());
                break;
            case "asan" :
                localVector[64].setFillColor(colorRC());
                break;
            case "yesan" :
                localVector[65].setFillColor(colorRC());
                break;
            case "dangjin" :
                localVector[66].setFillColor(colorRC());
                break;
            case "seosan" :
                localVector[67].setFillColor(colorRC());
                break;
            case "taean" :
                localVector[68].setFillColor(colorRC());
                localVector[69].setFillColor(colorRC());
                break;
            case "hongseong" :
                localVector[70].setFillColor(colorRC());
                break;
            case "cheongyang" :
                localVector[71].setFillColor(colorRC());
                break;
            case "bolyeong" :
                localVector[72].setFillColor(colorRC());
                break;
            case "buyeo" :
                localVector[73].setFillColor(colorRC());
                break;
            case "nonsan" :
                localVector[74].setFillColor(colorRC());
                break;
            case "geumsan" :
                localVector[75].setFillColor(colorRC());
                break;
            case "seocheon" :
                localVector[76].setFillColor(colorRC());
                break;
            case "gyelyong" :
                localVector[77].setFillColor(colorRC());
                break;
            case "gongju" :
                localVector[78].setFillColor(colorRC());
                break;

            //경북
            case "uljin" :
                localVector[79].setFillColor(colorRC());
                break;
            case "bonghwa" :
                localVector[80].setFillColor(colorRC());
                break;
            case "youngju" :
                localVector[81].setFillColor(colorRC());
                break;
            case "yecheon" :
                localVector[82].setFillColor(colorRC());
                break;
            case "mungyeong" :
                localVector[83].setFillColor(colorRC());
                break;
            case "sangju" :
                localVector[84].setFillColor(colorRC());
                break;
            case "andong" :
                localVector[85].setFillColor(colorRC());
                break;
            case "yeongyang" :
                localVector[86].setFillColor(colorRC());
                break;
            case "yeongdeog" :
                localVector[87].setFillColor(colorRC());
                break;
            case "cheongsong" :
                localVector[88].setFillColor(colorRC());
                break;
            case "pohang" :
                localVector[89].setFillColor(colorRC());
                break;
            case "gyeongju" :
                localVector[90].setFillColor(colorRC());
                break;
            case "yeongcheon" :
                localVector[91].setFillColor(colorRC());
                break;
            case "gunwi" :
                localVector[92].setFillColor(colorRC());
                break;
            case "uiseong" :
                localVector[93].setFillColor(colorRC());
                break;
            case "gumi" :
                localVector[94].setFillColor(colorRC());
                break;
            case "gincheon" :
                localVector[95].setFillColor(colorRC());
                break;
            case "chilgog" :
                localVector[96].setFillColor(colorRC());
                break;
            case "seongju" :
                localVector[97].setFillColor(colorRC());
                break;
            case "golyeong" :
                localVector[98].setFillColor(colorRC());
                break;
            case "daegu" :
                localVector[99].setFillColor(colorRC());
                break;
            case "gyeongsan" :
                localVector[100].setFillColor(colorRC());
                break;
            case "cheongdo" :
                localVector[101].setFillColor(colorRC());
                break;

            //경남
            case "geochang" :
                localVector[102].setFillColor(colorRC());
                break;
            case "hamyang" :
                localVector[103].setFillColor(colorRC());
                break;
            case "sancheong" :
                localVector[104].setFillColor(colorRC());
                break;
            case "habcheon" :
                localVector[105].setFillColor(colorRC());
                break;
            case "changnyeong" :
                localVector[106].setFillColor(colorRC());
                break;
            case "milyang" :
                localVector[107].setFillColor(colorRC());
                break;
            case "ulsan" :
                localVector[108].setFillColor(colorRC());
                break;
            case "uilyeong" :
                localVector[109].setFillColor(colorRC());
                break;
            case "haman" :
                localVector[110].setFillColor(colorRC());
                break;
            case "gimhae" :
                localVector[111].setFillColor(colorRC());
                break;
            case "busan" :
                localVector[112].setFillColor(colorRC());
                break;
            case "cheongwon" :
                localVector[113].setFillColor(colorRC());
                break;
            case "hadong" :
                localVector[114].setFillColor(colorRC());
                break;
            case "jinju" :
                localVector[115].setFillColor(colorRC());
                break;
            case "sacheon" :
                localVector[116].setFillColor(colorRC());
                break;
            case "kn_goseong" :
                localVector[117].setFillColor(colorRC());
                break;
            case "geoje" :
                localVector[118].setFillColor(colorRC());
                break;
            case "namhae" :
                localVector[119].setFillColor(colorRC());
                break;
            case "tongyeong" :
                localVector[120].setFillColor(colorRC());
                localVector[121].setFillColor(colorRC());
                localVector[122].setFillColor(colorRC());
                break;
            case "yangsan" :
                localVector[123].setFillColor(colorRC());
                break;

            //전북
            case "gunsan" :
                localVector[124].setFillColor(colorRC());
                break;
            case "igsan" :
                localVector[125].setFillColor(colorRC());
                break;
            case "wanju" :
                localVector[126].setFillColor(colorRC());
                localVector[127].setFillColor(colorRC());
                break;
            case "jinan" :
                localVector[128].setFillColor(colorRC());
                break;
            case "muju" :
                localVector[129].setFillColor(colorRC());
                break;
            case "jangsu" :
                localVector[130].setFillColor(colorRC());
                break;
            case "jeonju" :
                localVector[131].setFillColor(colorRC());
                break;
            case "gimjae" :
                localVector[132].setFillColor(colorRC());
                break;
            case "imsil" :
                localVector[133].setFillColor(colorRC());
                break;
            case "namwon" :
                localVector[134].setFillColor(colorRC());
                break;
            case "sunchang" :
                localVector[135].setFillColor(colorRC());
                break;
            case "jeongeub" :
                localVector[136].setFillColor(colorRC());
                break;
            case "buan" :
                localVector[137].setFillColor(colorRC());
                break;
            case "gochang" :
                localVector[138].setFillColor(colorRC());
                break;

            //전남
            case "yeonggwang" :
                localVector[139].setFillColor(colorRC());
                break;
            case "jangseong" :
                localVector[140].setFillColor(colorRC());
                break;
            case "damyang" :
                localVector[141].setFillColor(colorRC());
                break;
            case "gogseong" :
                localVector[142].setFillColor(colorRC());
                break;
            case "gulye" :
                localVector[143].setFillColor(colorRC());
                break;
            case "gwangyang" :
                localVector[144].setFillColor(colorRC());
                break;
            case "suncheon" :
                localVector[145].setFillColor(colorRC());
                break;
            case "yeosu" :
                localVector[146].setFillColor(colorRC());
                break;
            case "goheung" :
                localVector[147].setFillColor(colorRC());
                break;
            case "boseong" :
                localVector[148].setFillColor(colorRC());
                break;
            case "hwasun" :
                localVector[149].setFillColor(colorRC());
                break;
            case "biggwangju" :
                localVector[150].setFillColor(colorRC());
                break;
            case "hampyeong" :
                localVector[151].setFillColor(colorRC());
                break;
            case "naju" :
                localVector[152].setFillColor(colorRC());
                break;
            case "muan" :
                localVector[153].setFillColor(colorRC());
                break;
            case "sinan1" :
                localVector[154].setFillColor(colorRC());
                localVector[155].setFillColor(colorRC());
                break;
            case "mokpo" :
                localVector[156].setFillColor(colorRC());
                break;
            case "yeongam" :
                localVector[157].setFillColor(colorRC());
                break;
            case "jangheung" :
                localVector[158].setFillColor(colorRC());
                break;
            case "gangjin" :
                localVector[159].setFillColor(colorRC());
                break;
            case "haenam" :
                localVector[160].setFillColor(colorRC());
                break;
            case "wando" :
                localVector[161].setFillColor(colorRC());
                localVector[162].setFillColor(colorRC());
                localVector[163].setFillColor(colorRC());
                break;
            case "jindo" :
                localVector[164].setFillColor(colorRC());
                break;

            //제주
            case "jeju" :
                localVector[165].setFillColor(colorRC());
                break;

            case "incheon" :
                localVector[166].setFillColor(colorRC());
                break;
            case "kanghwado" :
                localVector[167].setFillColor(colorRC());
                break;
            case "yeongjongdo" :
                localVector[168].setFillColor(colorRC());
                break;
        }

        mapImg.invalidate();


        return view;
    }
    public int colorRC(){
        int [] colorArray = {R.color.pink, R.color.skyblue, R.color.purple, R.color.yellow};
        Random random = new Random();
        int randomColorIndex = random.nextInt(colorArray.length);
        int randomColorResId = colorArray[randomColorIndex];

        int randomColor = ContextCompat.getColor(getActivity(), randomColorResId);
        return randomColor;
    }




}
