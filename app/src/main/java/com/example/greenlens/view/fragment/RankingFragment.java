package com.example.greenlens.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.greenlens.databinding.FragmentRankingBinding;
import com.example.greenlens.view.adapter.RankingAdapter;
import com.example.greenlens.model.User;

import java.util.ArrayList;
import java.util.List;

public class RankingFragment extends Fragment {
    private FragmentRankingBinding binding;
    private RankingAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRankingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // RecyclerView 초기화
        binding.recyclerViewRanking.setLayoutManager(new LinearLayoutManager(getContext()));

        // 어댑터 생성 및 설정
        adapter = new RankingAdapter();
        binding.recyclerViewRanking.setAdapter(adapter);

        // 테스트용 데이터 로드
        loadDummyData();
    }

    private void loadDummyData() {
        // 테스트용 더미 데이터 생성
        List<User> userList = new ArrayList<>();

        User user1 = new User();
        user1.setUsername("에코맘");
        user1.setRecycleCount(247);
        userList.add(user1);

        User user2 = new User();
        user2.setUsername("지구지키미");
        user2.setRecycleCount(235);
        userList.add(user2);

        User user3 = new User();
        user3.setUsername("그린워커");
        user3.setRecycleCount(218);
        userList.add(user3);

        User user4 = new User();
        user4.setUsername("환경나무");
        user4.setRecycleCount(199);
        userList.add(user4);

        User user5 = new User();
        user5.setUsername("제로웨이스터");
        user5.setRecycleCount(187);
        userList.add(user5);

        User user6 = new User();
        user6.setUsername("분리수거왕");
        user6.setRecycleCount(176);
        userList.add(user6);

        User user7 = new User();
        user7.setUsername("친환경삶");
        user7.setRecycleCount(164);
        userList.add(user7);

        User user8 = new User();
        user8.setUsername("그린라이프");
        user8.setRecycleCount(159);
        userList.add(user8);

        User user9 = new User();
        user9.setUsername("제로웨이스트");
        user9.setRecycleCount(148);
        userList.add(user9);

        User user10 = new User();
        user10.setUsername("에코프렌즈");
        user10.setRecycleCount(137);
        userList.add(user10);

        // 어댑터에 데이터 설정
        adapter.setUserList(userList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}