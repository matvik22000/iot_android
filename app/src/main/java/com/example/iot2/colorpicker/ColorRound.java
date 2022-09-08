package com.example.iot2.colorpicker;

import static com.example.iot2.colorpicker.MainActivity.doGet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;



import com.example.iot2.Iot;

public class ColorRound extends View implements View.OnTouchListener {

    RectF rectf, rectfC, rectfA, rectfB, rectfD, rectBR_A, rectBR_B;
    int x = 0, y = 0, length, t_color = 0;
    int[] colors;
    Point center, point_a1, point_b1, point_c1, point_a2, point_b2, point_c2, point_start, point_finish;
    String device_name = "led";
    com.example.iot2.colorpicker.Color[] color;
    double ug = 0;
    int event = -1;
    Context context;
    int width;
    int brightness;
    int px;
    int ug_1;
    float[][] hsvs;

    public ColorRound(Context context, int length, com.example.iot2.colorpicker.Color[] color, int width,
                      int brightness, String device_name, String current_color) {
        super(context);
        setOnTouchListener(this);
        this.context = context;
        this.width = width;
        this.brightness = brightness;
        this.length = length;
        this.color = color;
        this.device_name = device_name;

        ug_1 = (int) (220 * (double) brightness / 100);

        rectf = new RectF(280, 280, width - 280, width - 280);
        rectBR_A = new RectF(100, 100, width - 100, width - 100);
        rectBR_B = new RectF(rectBR_A);
        rectBR_B.inset(40, 40);
        rectfB = new RectF(rectf);
        rectfA = new RectF(rectf);
        rectfC = new RectF(rectf);
        rectfD = new RectF(rectf);
        rectfC.inset(80, 80);
        rectfA.inset(-45, -45);

        for (int i = 0; i < color.length; i++) {
            System.out.println(color[i].id);
            if (color[i].id.equals(current_color))
                t_color = i;
        }
        System.out.println(t_color);
        ug = ((double) 360 / color.length) * t_color + 1;
        ug -= 90 - 180 / color.length;
        if (ug < 0)
            ug += 360;
        System.out.println(ug);

        colors = new int[length];
        for (int i = 0; i < length; i++) {
            float[] hsv = new float[3];
            hsv[0] = color[i].getHsv().h;
            hsv[1] = (float) (color[i].getHsv().s * 0.01);
            hsv[2] = (float) (color[i].getHsv().v * 0.01);
            colors[i] = Color.HSVToColor(hsv);
        }
        float[] hsv = new float[3];
        hsv[0] = color[t_color].getHsv().h;
        hsv[1] = (float) (color[t_color].getHsv().s * 0.01);
        float d = hsv[1] / 19;
        hsv[2] = (float) (color[t_color].getHsv().v * 0.01);
        hsvs = new float[20][3];
        for (int i = 0; i < hsvs.length; i++) {
            hsvs[i][0] = hsv[0];
            hsvs[i][1] = d * i;
            hsvs[i][2] = hsv[2];

        }

        px = (int) ((rectBR_A.right - rectBR_B.right) / 2);
        center = new Point((int) (rectf.centerX()), (int) (rectf.centerY()));
        createSlider();
        point_start = getPoint(0, px);
        point_finish = getPoint(220, px);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int back_color = Color.argb(255, 18, 18, 18);
        canvas.drawColor(back_color);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(30);
        float r = 360f / length;

        for (int i = 0; i < 20; i++) {
            paint.setColor(Color.HSVToColor(hsvs[i]));
            canvas.drawArc(rectBR_A, 160f + i * 220f / 20, 220f / 20, true, paint);
            if (i == 0)
                canvas.drawCircle(point_start.x, point_start.y, px, paint);
            if (i == 19)
                canvas.drawCircle(point_finish.x, point_finish.y, px, paint);
        }

        paint.setColor(back_color);
        canvas.drawArc(rectBR_B, 160, 220, true, paint);

        for (int i = 0; i < length; i++) {
            paint.setColor(colors[i]);
            canvas.drawArc(rectf, 270f - r / 2 + r * i, r, true, paint);
        }

        paint.setColor(Color.HSVToColor(hsvs[(brightness / 5 == 20 ? 19 : brightness / 5)]));
        canvas.drawOval(rectfC, paint);
        if (event == 1) {

            canvas.drawArc(rectfD, 270f - r / 2 + r * t_color - 5, r + 10f, true, paint);
            if (rectfA.left < rectfD.left)
                rectfD.inset(-4, -4);

        }
        Path path1 = new Path();
        path1.reset();
        path1.moveTo(point_a1.x, point_a1.y);
        path1.quadTo(point_c1.x, point_c1.y, point_b1.x, point_b1.y);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path1, paint);
        Path path2 = new Path();
        path2.reset();
        path2.moveTo(point_a2.x, point_a2.y);
        path2.quadTo(point_c2.x, point_c2.y, point_b2.x, point_b2.y);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path2, paint);

//        if (event != 1) {
//            paint.setTextAlign(Paint.Align.CENTER);
//            paint.setTextSize(110);
//            float hsv[] = Arrays.copyOf(hsvs[t_color], 3);
//            hsv[2] *= 0.6f;
//            paint.setColor(Color.HSVToColor(hsv));
//            paint.setTypeface(Typeface.create("Arial", Typeface.BOLD));
//            canvas.drawText(brightness + "", center.x, center.y + 40, paint);
//        }

//        if (event == 2 || event == 3 || event == 4) {
//            if (rectfC.left < rectf.left)
//                event = -1;
//            else
//                rectfC.inset(-10, -10);
//        }
        invalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        x = (int) event.getX();
        y = (int) event.getY();
        double l = Math.sqrt((center.x - x) * (center.x - x) + (center.y - y) * (center.y - y));
        if (l < (rectBR_A.right - rectBR_A.left) / 2 + 100 && l > (rectBR_A.right - rectBR_A.left) / 2 - 80) {
            ug_1 = getUg(l);
            ug_1 -= 90 + 180 / length;
            if (ug_1 < 0)
                ug_1 += 360;
            if (ug_1 >= 155 || ug_1 <= 25) {
                int gf;
                if (ug_1 < 160 && ug_1 >= 155)
                    ug_1 = 160;
                if (ug_1 > 20 && ug_1 <= 25)
                    ug_1 = 20;
                if (ug_1 <= 20)
                    ug_1 += 200;
                else
                    ug_1 -= 160;
                createSlider();
                gf = (int) ((double) (ug_1) / 220 * 100);
                brightness = gf;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        this.event = 3;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        this.event = 4;
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        this.event = 2;
                        Thread myThread = new Thread(new ResThread(2), "ResThread");
                        myThread.start();
                        break;
                }
            }
        }
        if (l < (rectf.right - rectf.left) / 2 + 60) {
            ug = getUg(l);
            int last_t_c = t_color;
            t_color = (int) (ug / (360 / length));
            if (t_color == length)
                t_color--;
            if (t_color != last_t_c) {
                float[] hsv = new float[3];
                hsv[0] = color[t_color].getHsv().h;
                hsv[1] = (float) (color[t_color].getHsv().s * 0.01);
                float d = hsv[1] / 19;
                hsv[2] = (float) (color[t_color].getHsv().v * 0.01);
                hsvs = new float[20][3];
                for (int i = 0; i < hsvs.length; i++) {
                    hsvs[i][0] = hsv[0];
                    hsvs[i][1] = d * i;
                    hsvs[i][2] = hsv[2];

                }
                rectfD = new RectF(rectf);
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    this.event = 0;
                    rectfC = new RectF(rectf);
                    rectfC.inset(80, 80);
                    break;
                case MotionEvent.ACTION_MOVE:
                    this.event = 1;
                    rectfC = new RectF(rectf);
                    rectfC.inset(80, 80);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    this.event = 2;
                    Thread myThread = new Thread(new ResThread(1), "ResThread");
                    myThread.start();
                    break;
            }
        } else {
            if (this.event == 1) {
                this.event = 2;
                Thread myThread = new Thread(new ResThread(1), "ResThread");
                myThread.start();
            }
        }
        return true;
    }

    private Point getPoint(double ug, int l) {
        Point point = new Point();
        int r = (int) ((rectBR_A.right - rectBR_A.left) / 2) - l;
        if (ug <= 20) {
            ug = 20 - ug;
            point.x = center.x - (int) (Math.cos(ug / 180 * Math.PI) * r);
            point.y = center.y + (int) (Math.sin(ug / 180 * Math.PI) * r);
        } else if (ug <= 110) {
            ug -= 20;
            point.x = center.x - (int) (Math.cos(ug / 180 * Math.PI) * r);
            point.y = center.y - (int) (Math.sin(ug / 180 * Math.PI) * r);
        } else if (ug <= 200) {
            ug = 200 - ug;
            point.x = center.x + (int) (Math.cos(ug / 180 * Math.PI) * r);
            point.y = center.y - (int) (Math.sin(ug / 180 * Math.PI) * r);
        } else {
            ug = -200 + ug;
            point.x = center.x + (int) (Math.cos(ug / 180 * Math.PI) * r);
            point.y = center.y + (int) (Math.sin(ug / 180 * Math.PI) * r);
        }
        return point;
    }

    void createSlider() {
        point_c1 = getPoint(ug_1, 130 + 2 * (ug_1 - 12 < 0 ? ug_1 - 12 : (ug_1 + 12 > 220 ? 208 - ug_1 : 0)));
        point_a1 = getPoint(Math.max(ug_1 - 12, -2.8), (ug_1 - 12 < 0 ? px : 0));
        point_b1 = getPoint(Math.min(ug_1 + 12, 222.8), (ug_1 + 12 > 220 ? px : 0));
        point_c2 = getPoint(ug_1, -130 + 3 * px - 10 - (ug_1 - 12 < 0 ? ug_1 - 12 : (ug_1 + 12 > 220 ? 208 - ug_1 : 0)));
        point_a2 = getPoint(Math.max(ug_1 - 12, -2.8), (ug_1 - 12 < 0 ? px : px));
        point_b2 = getPoint(Math.min(ug_1 + 12, 222.8), (ug_1 + 12 > 220 ? px : px));
    }

    int getUg(double l) {
        double sin = (double) (x - center.x) / l;
        double cos = (double) (y - center.y) / l;
        int cosU = (int) (Math.acos(cos) / Math.PI * 180 - 90);
        int sinU = (int) (Math.asin(sin) / Math.PI * 180);
        int ug;
        if (sinU >= 0) {
            if (cosU >= 0)
                ug = 360 - cosU;
            else
                ug = -cosU;
        } else {
            if (cosU >= 0)
                ug = 180 + cosU;
            else
                ug = 90 - sinU;
        }
        ug += 90 + 180 / length;
        if (ug > 360)
            ug -= 360;
        return ug;
    }

    class ResThread implements Runnable {
        String url = Iot.HOST;

        public ResThread(int sos) {
            switch (sos) {
                case 1:
                    url += "set_color?device=" +
                            device_name + "&color=" + color[t_color].getId();
                    break;
                case 2:
                    url += "set_brightness?device=" +
                            device_name + "&value=" + brightness;
                    break;
            }

        }

        public void run() {
            try {
                doGet(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("gg");
        }
    }

}

