package com.example.tour3D;

import javax.media.j3d.Behavior;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupOnElapsedTime;
import java.util.Enumeration;

public class TimeBehavior extends Behavior {

    private WakeupCondition timeOut;
    private AlienSprite alienSprite;

    public TimeBehavior(int timeDelay, AlienSprite as) {
        alienSprite = as;
        timeOut = new WakeupOnElapsedTime(timeDelay);
    }

    @Override
    public void initialize() {
        wakeupOn(timeOut);
    }

    @Override
    public void processStimulus(Enumeration enumeration) {
        alienSprite.update();
        wakeupOn(timeOut);
    }
}
