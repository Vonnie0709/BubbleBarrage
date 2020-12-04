package android.vonnie.barrageview;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LongpingZou
 * @date 2019/3/29
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<String> bubbles = new ArrayList<>();
    private LinearLayout bubbleBarrageContainer;
    private BubbleBarrage<String> bubbleBarrage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initBubbleBarrage();
    }

    private void initView() {
        bubbleBarrageContainer = findViewById(R.id.bubble_barrage_container);

        findViewById(R.id.append_more).setOnClickListener(this);
        Button more = findViewById(R.id.append_more);
        Shader shader = new LinearGradient(0, 0, 0, 20, Color.BLACK, Color.GRAY, Shader.TileMode.CLAMP);

        more.getPaint().setShader(shader);
        findViewById(R.id.insert_to_next).setOnClickListener(this);
    }

    private void initBubbleBarrage() {
        bubbleBarrage = new BubbleBarrage<String>()
                .init(this, bubbleBarrageContainer, R.layout.layout_bubble_barrage_item)
                .setIntervalTime(1000)
                .setVisibleCount(3)
                .setItemMargin(20)
                .setOnBarrageLoadListener(new BubbleBarrage.OnBarrageLoadListener<String>() {
                    @Override
                    public void loadBarrage(View view, List<String> data, int index) {
                        view.findViewById(R.id.iv_head).setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.test_header));
                        ((TextView) view.findViewById(R.id.tv_content)).setText(data.get(index));
                    }
                })
                .start(bubbles, 1000);
    }

    private void initData() {
        for (int i = 0; i < 10; i++) {
            bubbles.add("item --> " + i);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.append_more:
                onAppendClick();
                break;
            case R.id.insert_to_next:
                onInsertClick();
                break;
            default:
        }
    }


    private void onInsertClick() {
        bubbleBarrage.insertToNext("this is insert item");
    }

    private void onAppendClick() {
        List<String> appends = new ArrayList<>();
        for (int i = 100; i < 110; i++) {
            appends.add("append item -->" + i);
        }
        bubbleBarrage.appendBarrages(appends);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bubbleBarrage.destroyView();
    }
}
