package com.company.engine.audio;

import com.company.engine.IUsesResources;
import org.joml.Vector3f;

import java.util.Vector;

import static org.lwjgl.openal.AL10.*;

public class AudioSource implements IUsesResources {

    private final int mSourceId;

    private boolean mLooping;
    private boolean mRelative;

    public AudioSource(boolean looping, boolean relative) {
        mSourceId = alGenSources();
        mLooping = looping;
        mRelative = relative;

        initialiseAudioSource();
    }

    private void initialiseAudioSource() {
        configureLooping();
        configureRelative();
    }

    private void configureLooping() {
        alSourcei(mSourceId, AL_LOOPING, mLooping ? AL_TRUE : AL_FALSE);
    }

    private void configureRelative() {
        alSourcei(mSourceId, AL_SOURCE_RELATIVE, mRelative ? AL_TRUE : AL_FALSE);
    }

    public void play() {
        alSourcePlay(mSourceId);
    }

    public void pause() {
        alSourcePause(mSourceId);
    }

    public void stop() {
        alSourceStop(mSourceId);
    }

    public boolean isInState(int state) {
        return alGetSourcei(mSourceId, AL_SOURCE_STATE) == state;
    }

    public void setLooping(boolean looping) {
        mLooping = looping;
        configureLooping();
    }

    public void setRelative(boolean relative) {
        mRelative = relative;
        configureRelative();
    }

    public boolean isLooping() {
        return mLooping;
    }

    public boolean isRelative() {
        return mRelative;
    }

    public void setBuffer(int bufferId) {
        stop();
        alSourcei(mSourceId, AL_BUFFER, bufferId);
    }

    public void setPosition(Vector3f position) {
        alSource3f(
                mSourceId,
                AL_POSITION,
                position.x,
                position.y,
                position.z
        );
    }

    public void setVelocity(Vector3f velocity) {
        alSource3f(
                mSourceId,
                AL_VELOCITY,
                velocity.x,
                velocity.y,
                velocity.z
        );
    }

    @Override
    public void cleanUp() {
        stop();
        alDeleteBuffers(mSourceId);
    }

    public void setGain(float gain) {
        alSourcef(mSourceId, AL_GAIN, gain);
    }

    public void setAlProperty(int parameter, float gain) {
        alSourcef(mSourceId, parameter, gain);
    }
}