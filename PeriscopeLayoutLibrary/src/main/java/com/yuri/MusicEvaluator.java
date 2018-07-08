
package com.yuri;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

public class MusicEvaluator implements TypeEvaluator<PointF> {

    PointF point = new PointF();//结果
    private PointF pointF1;
    private PointF pointF2;
    public MusicEvaluator(PointF pointF1, PointF pointF2){
        this.pointF1 = pointF1;
        this.pointF2 = pointF2;
    }
    @Override
    public PointF evaluate(float time, PointF startValue, PointF endValue) {
        float timeLeft = 1.0f - time;           //f2
        float time_2 = time * time;             //f3
        float time_3 = time_2 * time;           //f4
        float timeLeft_2 = timeLeft * timeLeft; //f5
        float timeLeft_3 = timeLeft_2 * timeLeft;//f6


        point.x = (((startValue.x * timeLeft_3) + (((3.0f * timeLeft_2) * time) * this.pointF1.x)) + (((3.0f * timeLeft) * time_2) * this.pointF2.x)) + (endValue.x * time_3);
        point.y = ((((timeLeft * 3.0f) * time_2) * this.pointF2.y) + ((((timeLeft_2 * 3.0f) * time) * this.pointF1.y) + (timeLeft_3 * startValue.y))) + (endValue.y * time_3);
        return point;
    }
}
