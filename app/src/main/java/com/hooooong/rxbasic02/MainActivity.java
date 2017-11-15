package com.hooooong.rxbasic02;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    Observable<String> observable;
    Observable<String> observableZip;
    RecyclerView recyclerView;
    CustomAdapter customAdapter;

    // 데이터 저장 변수
    List<String> dataList = new ArrayList<>();

    String monthString[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        customAdapter = new CustomAdapter();
        recyclerView.setAdapter(customAdapter);

        // 0. 데이터 셋팅
        DateFormatSymbols dfs = new DateFormatSymbols();
        monthString = dfs.getMonths();

        // 1. 데이터 발행 ( Emitter 가 Observable 생성 )
        // 무조건 Sub Thread 에서 돌아간다.
        observable = Observable.create(e -> {
            try {
                for (String month : monthString) {
                    e.onNext(month);
                    Thread.sleep(1000);
                }
                e.onComplete();
            } catch (Exception ex) {
                throw ex;
            }
        });

        observableZip = Observable.zip(
                Observable.just("BeWHY", "Curry"),
                Observable.just("Singer", "Basketball Player"),
                (item1, item2) -> "jop : ".concat(item1).concat(", Name : ").concat(item2)
        );
    }

    // 2-1. 데이터 구독 ( Consumer 가 Subscriber 함 )
    // subscribeOn(Schedulers.io()) : Observable 에 Thread 설정
    // Schedulers.io() - 동기 I/O 를 별도로 처리시켜 비동기 효율을 얻기 위한 스케쥴러
    //                   자체적인 ThreadPool 을 사용하고, API 호출 등 네트워크를 사용한 호출 시 사용
    // observeOn(AndroidSchedulers.mainThread()) : Observer 에 Thread 설정
    // AndroidSchedulers.mainThread() : 안드로이드의 UI 스레드에서 동작


    // map : 데이터의 변형을 하기 위해 사용된다.
    public void doMap(View view) {
        // 2. 데이터 구독 ( Consumer 가 subscriber 함)
        // filter( new Predicate ) : 데이터를 거를 수 있다.
        // map( new Function ) : 데이터의 변형을 할 수 있다.

        dataList.clear();

        observable
                .filter(str ->
                        !str.equals("3월")
                )
                .map(str -> "[" + str + "]")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(str -> {
                    dataList.add(str);
                    customAdapter.setDataAndRefresh(dataList);
                }, throwable -> {
                });
    }

    // FlatMap : 데이터를 여러개로 가공하기 위해 사용된다.
    // { 1월 } ->  { name : 1월, [1월] }
    public void doFlatMap(View view) {
        dataList.clear();

        observable
                //.filter(str -> !str.equals("10월"))
                .flatMap(item ->
                        Observable.fromArray("name : " + item, "[" + item + "]")
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(str -> {
                    dataList.add(str);
                    customAdapter.setDataAndRefresh(dataList);
                }, throwable -> {
                }, () -> Log.e(TAG, "doFlatMap: " + dataList.size()));


    }

    // Zip : Observarble 을 하나로 묶어준다.
    public void doZip(View view) {
        dataList.clear();

        observableZip
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(str -> {
                    dataList.add(str);
                    customAdapter.setDataAndRefresh(dataList);
                }, throwable -> {
                }, () -> Log.e(TAG, "doFlatMap: " + dataList.size()));

    }
}

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    List<String> data = new ArrayList<>();

    public void setDataAndRefresh(List<String> dataAndNotify) {
        this.data = dataAndNotify;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.text1.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView text1;

        public ViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
        }
    }
}

