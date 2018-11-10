package gildas.archeytime.Business;

import java.io.Serializable;

public class SequenceItem implements Serializable {
    private Types type;
    private int duration;
    private Lights light;
    private Integer sound;

    public SequenceItem(Types type, int duration) {
        this.type = type;
        this.duration = duration;
    }

    public SequenceItem(Types type, Lights light, int sound) {
        this.type = type;
        this.light = light;
        if (sound > 3){
            this.sound = 3;
        }
        else {
            this.sound = sound;
        }
    }


    public Types getType() {
        return type;
    }

    public void setType(Types type) {
        this.type = type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Lights getLight() {
        return light;
    }

    public void setLight(Lights light) {
        this.light = light;
    }

    public int getSound() {
        return sound;
    }

    public void setSound(Integer sound) {
        if (sound > 3){
            this.sound = 3;
        }
        else {
            this.sound = sound;
        }
    }
}
