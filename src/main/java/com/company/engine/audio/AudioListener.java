package com.company.engine.audio;

import org.joml.Vector3f;

import static org.lwjgl.openal.AL10.*;

public class AudioListener {

    public AudioListener() {
        this(new Vector3f(0, 0, 0));
    }

    public AudioListener(Vector3f position) {
        setPosition(position);
        setVelocity(new Vector3f(0, 0, 0));
    }

    public void setPosition(Vector3f position) {
        alListener3f(AL_POSITION, position.x, position.y, position.z);
    }

    public void setVelocity(Vector3f velocity) {
        alListener3f(AL_VELOCITY, velocity.x, velocity.y, velocity.z);
    }

    /*
    the 'at' parameter is where the listener is pointing
    the 'up' parameter is the value of "up" to the listener (by default is set to (0, 1, 0)
     */
    public void setOrientation(Vector3f at, Vector3f up) {
        float[] data = new float[6];

        data[0] = at.x;
        data[1] = at.y;
        data[2] = at.z;
        data[3] = up.x;
        data[4] = up.y;
        data[5] = up.z;

        alListenerfv(AL_ORIENTATION, data);
    }
}