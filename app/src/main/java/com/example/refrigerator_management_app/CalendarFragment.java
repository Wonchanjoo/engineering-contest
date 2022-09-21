package com.example.refrigerator_management_app;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment {
    private ArrayList<Item> allItems; // 모든 아이템들이 들어있는 ArrayList
    private ArrayList<Item> items;
    private MutableLiveData<ArrayList<Item>> itemsLivedata; // 유통기한이 0일 ~ day 남은 아이템들이 들어있는
    private CalendarItemAdapter adapter;
    private Context context;
    private String notificationText;

    static final String CHANNEL_ID = "channelId";
    static final int notificationId = 1;
    private NotificationManager notificationManager;
    private CalendarView calendarView;

    private int day = 4;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CalendarFragment() {
        // Required empty public constructor
    }
    public CalendarFragment(Context context) {
        this.context = context;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalendarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        String textTitle = "Notification Title";
        String textContent = "Lorem ipsum dolor sit.";
        // 채널 생성
        createNotificationChannel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allItems = ((ItemActivity)getActivity()).getAllItems();
        items = new ArrayList<>();
        itemsLivedata = new MutableLiveData<>();
        adapter = new CalendarItemAdapter(getContext(), items);
        ListView listView = getView().findViewById(R.id.calendarListView);
        listView.setAdapter(adapter);

        calendarView = getView().findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                // allItems 안에 들어있는 것 중에 선택된 날짜와 유통기한이 4일 이하로 차이나는 아이템들을 viewModel에 넣기
                items.clear();
                itemsLivedata.setValue(items);
                try {
                    getItemOnVergeOfExpiration(selectedDate);


                    notificationText = "";
                    for (int i =0; i<items.size();i++){
                        long diff = getExpirationDateDifference(items.get(i).getExpirationDate(), selectedDate); // 유통기한 - 선택한 날짜
                        String diffDate = String.valueOf(diff);
                        Log.e("onSelectedDayChange",diffDate+"남음");
                        String text = items.get(i).getName() + "이(가) " + diffDate + "일 남았습니다!\n";
                        notificationText += text;
                    }
                    sendNotification(notificationText);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }
        });

        String todayDate = getCurrentDate();
        Log.e("CalendarFragment", "오늘 날짜는 " + todayDate);
        try {
            getItemOnVergeOfExpiration(todayDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getCurrentDate() {
        LocalDate now = LocalDate.now();
        String todayDate = now.toString();
        return todayDate;
    }

    // 유통기한1 - 유통기한2의 차를 반환하는 함수
    private long getExpirationDateDifference(String date1, String date2) throws ParseException {
        Date format1 = new SimpleDateFormat("yyyy-MM-dd").parse(date1);
        Date format2 = new SimpleDateFormat("yyyy-MM-dd").parse(date2);

        long diffSec = (format1.getTime() - format2.getTime()) / 1000;
        long diffDays = diffSec / (24 * 60 * 60);

        return diffDays;
    }

    // 유통기한이 인자로 받은 날짜와 days 이하로 차이나는 아이템들을 viewModel에 넣는 함수
    private void getItemOnVergeOfExpiration(String selectedDate) throws ParseException {
        for(int i=0; i<allItems.size(); i++) {
            Item item = allItems.get(i);
            String itemDate = item.getExpirationDate(); // 아이템의 유통기한

            long diff = getExpirationDateDifference(itemDate, selectedDate); // 유통기한 - 선택한 날짜
            // 유통기한이 0 ~ day일 남은 경우
            if(diff <= day && diff >= 0) {
                if(!items.contains(item)) {
                    item.setRemainDays((int) diff);
                    items.add(item);
                    itemsLivedata.setValue(items);
                    Log.e("CalendarFragment", "items size = " + items.size());
                }
            }
        }
    }

    private void createNotificationChannel() {
        // Android8.0 이상인지 확인
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e("createNotificationChannel","createNotificationChannel");
            // 채널에 필요한 정보 제공
            String name = "name";
            String description = "description";

            // 중요도 설정, Android7.1 이하는 다른 방식으로 지원한다.(위에서 설명)
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            // 채널 생성
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
    }
    // Notification Builder를 만드는 메소드
    private NotificationCompat.Builder getNotificationBuilder(String text) {
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("[내 손 안의 냉장고] 유통기한 알림")
                .setContentText(text)
                .setSmallIcon(R.drawable.calendar_icon);
        return notifyBuilder;
    }

    // Notification을 보내는 메소드
    public void sendNotification(String text){
        Log.e("sendNotification","sendNotification");
        // Builder 생성
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder(text);
        // Manager를 통해 notification 디바이스로 전달
        notificationManager.notify(notificationId,notifyBuilder.build());
    }
}