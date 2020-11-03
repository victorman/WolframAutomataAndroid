package dev.victorman.wolframautomata;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {

    private String TAG = "hex";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        final AutomataGridView gridView = new AutomataGridView(context, new Rule30());

        gridView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        Runnable automataRunnable = new Runnable() {
            long elapsedTime = 0L;
            long fps = 4L;
            long frameDuration = 1000L / fps;
            long lastTime = 0L;
            @Override
            public void run() {
                while(true) {
                    elapsedTime = System.currentTimeMillis() - lastTime;
                    if (elapsedTime > frameDuration) {
                        gridView.nextGen();
                        lastTime = System.currentTimeMillis();
                    }
                }
            }
        };

        Thread automataThread = new Thread(automataRunnable);
        setContentView(gridView);


        automataThread.start();
    }
}