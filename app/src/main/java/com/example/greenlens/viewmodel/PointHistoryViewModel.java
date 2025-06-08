package com.example.greenlens.viewmodel;

import com.example.greenlens.model.Point;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class PointHistoryViewModel extends ViewModel {
    private MutableLiveData<List<Point>> points = new MutableLiveData<>();

    public LiveData<List<Point>> getPoints() {
        return points;
    }

    public PointHistoryViewModel() {
        loadDummyData();
    }

    private void loadDummyData() {
        List<Point> dummyPoints = new ArrayList<>();
        dummyPoints.add(new Point("2024-11-21", "종이류", 10, 510));
        dummyPoints.add(new Point("2024-11-22", "비닐류", 15, 525));
        dummyPoints.add(new Point("2024-11-23", "유리류", 20, 545));
        points.setValue(dummyPoints);
    }
}
