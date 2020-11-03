package dev.victorman.wolframautomata;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutomataGridView extends View {

    private static final String TAG = AutomataGridView.class.getCanonicalName();
    private final Paint fillPaint;
    private final Paint emptyPaint;
    private final Rule rule;
    private float cellsPerRow = 31f;
    private float gridSide;
    private int gen;
//    private int width;
    private List<byte[]> patterns;
    private List<Integer> generation;
    private final int ONE_MASK = 0x1;
    private final int TWO_MASK = 0x6;
    private final int THREE_MASK = 0x7;

    public AutomataGridView(Context context, Rule rule) {
        super(context);
//        gen = 0;
        fillPaint = new Paint();
        emptyPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        fillPaint.setColor(Color.BLACK);
        emptyPaint.setStyle(Paint.Style.STROKE);
        emptyPaint.setStrokeWidth(5f);
        emptyPaint.setColor(Color.BLACK);
        this.rule = rule;

        patterns = new ArrayList<byte[]>(100);
        generation = new ArrayList<Integer>(100);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        gridSide = getWidth() / cellsPerRow;

        double maxCellsHeight = Math.ceil((double)getHeight() / (double)gridSide);
        if(patterns.size() >= maxCellsHeight) {
            patterns.remove(0);
            generation.remove(0);
        }

        for(int i = 0; i < patterns.size(); i++) {
            int k = generation.get(i);
            int width = k * 2 + 1;
            int diff = width - (int) cellsPerRow; // number of cells outside the screen for this gen
            // a positive number...
            // it should be even, right? so if 2 then diff/2
            // a negative number say 20. then begin at diff/2 * gridside inside the screen;


            if(diff < 0) {
                float offset = -diff/2f * gridSide;

                drawRow(canvas, offset, i, 0);


            } else {
                //begin j at diff/2 and offset is 0;
                drawRow(canvas,0f, i, diff/2);

//                if(i == patterns.size()-1)
//                    Log.i(TAG, k+" "+diff/2);
            }
        }
    }

    private void drawRow(Canvas canvas, float offset, int i, int startcell) {

        byte[] row = patterns.get(i);
        int k = generation.get(i);
        int width = k * 2 + 1;
        for(int j = startcell; j < width-startcell; j++) {
            int byteIndex = j/8;
            int bit = j%8;

            int cell = (row[byteIndex] >> bit) & ONE_MASK;

            int left = (int) (offset + (j-startcell) * gridSide);
            int top = (int) (i * gridSide);
            if(cell == 1) {
                canvas.drawRect(new Rect(left, top, (int) (left + gridSide), (int) (top + gridSide)), fillPaint);
            } else {
                canvas.drawRect(new Rect(left, top, (int) (left + gridSide), (int) (top + gridSide)), emptyPaint);
            }
        }
    }

    public void nextGen() {
        generation.add(gen);
        patterns.add(nextPattern());
        invalidate();
        gen++;
    }

    private byte[] nextPattern() {
        if (gen == 0) {
            byte[] out = new byte[1];
            out[0] = 1;
            return out;
        }
        byte[] prevGenPattern = patterns.get(patterns.size()-1);
//        int prevGen = generation.get(patterns.size()-1);


        int width = gen * 2 + 1;
        int bytes = (int)Math.ceil(width / 8.0);
        byte[] nextGenPattern = new byte[bytes];
        Arrays.fill(nextGenPattern, (byte) 0);

        int val = 0;
        for(int j=0;j<width; j++) {
            int byteIndex = j/8;
            int bit = j%8;

            int rightIndex = j;
            int rightVal = 0;
            if(byteIndex < prevGenPattern.length)
                rightVal = (prevGenPattern[rightIndex/8]>>(rightIndex%8)) & ONE_MASK;

            val = val << 1;
            val = val | rightVal;
            val = val & THREE_MASK;
            byte outcome = rule.getOutcome((byte)val);

            nextGenPattern[byteIndex] = (byte)(nextGenPattern[byteIndex] | (outcome << (bit)));
        }
        return nextGenPattern;
    }
}
