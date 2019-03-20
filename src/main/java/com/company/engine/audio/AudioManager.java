package com.company.engine.audio;

import com.company.engine.graph.rendering.Camera;
import com.company.engine.graph.Transformation;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class AudioManager {

    private final List<AudioBuffer> mAudioBufferList;
    private final Map<String, AudioSource> mAudioSourceMap;
    private final Matrix4f mCameraMatrix;

    private long mDevice;
    private long mContext;
    private AudioListener mAudioListener;

    public AudioManager() {
        mAudioBufferList = new ArrayList<>();
        mAudioSourceMap = new HashMap<>();
        mCameraMatrix = new Matrix4f();
    }

    public void init() {
        //open the default device
        mDevice = alcOpenDevice((ByteBuffer) null);

        if (mDevice == NULL) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }

        ALCCapabilities deviceCapabilities = ALC.createCapabilities(mDevice);

        mContext = alcCreateContext(mDevice, (IntBuffer) null);

        if (mContext == NULL) {
            throw new IllegalStateException("Failed to create OpenAL context.");
        }

        alcMakeContextCurrent(mContext);
        AL.createCapabilities(deviceCapabilities);
    }

    public void playAudioSource(String name) {
        AudioSource audioSource = mAudioSourceMap.get(name);

        if (canPlayAudioSource(audioSource)) {
            audioSource.play();
        }
    }

    protected boolean canPlayAudioSource(AudioSource audioSource) {
        return audioSource != null && !audioSource.isInState(AL_PLAYING);
    }

    public void pauseAudioSource(String name) {
        AudioSource audioSource = mAudioSourceMap.get(name);

        if (canPauseAudioSource(audioSource)) {
            audioSource.pause();
        }
    }

    protected boolean canPauseAudioSource(AudioSource audioSource) {
        return audioSource != null && audioSource.isInState(AL_PLAYING) &&
                !audioSource.isInState(AL_STOPPED);
    }

    public void stopAudioSource(String name) {
        AudioSource audioSource = mAudioSourceMap.get(name);

        if (canStopAudioSource(audioSource)) {
            audioSource.stop();
        }
    }

    protected boolean canStopAudioSource(AudioSource audioSource) {
        return audioSource != null && !audioSource.isInState(AL_STOPPED);
    }

    public void updateListenerPosition(Camera camera) {
        //update camera matrix wth camera data
        Transformation.updateGenericViewMatrix(
                camera.getPosition(),
                camera.getRotation(),
                mCameraMatrix
        );
        Vector3f at = new Vector3f();
        Vector3f up = new Vector3f();

        mAudioListener.setPosition(camera.getPosition());
        mCameraMatrix.positiveZ(at).negate();
        mCameraMatrix.positiveY(up);
        mAudioListener.setOrientation(at, up);
    }

    public void setAttenuationModel(int model) {
        alDistanceModel(model);
    }

    public void addAudioSource(String name, AudioSource audioSource) {
        mAudioSourceMap.put(name, audioSource);
    }

    public AudioSource getAudioSource(String name) {
        return mAudioSourceMap.get(name);
    }

    public void removeAudioSource(String name) {
        mAudioSourceMap.remove(name);
    }

    public void addAudioBufer(AudioBuffer audioBuffer) {
        mAudioBufferList.add(audioBuffer);
    }

    public void setAudioListener(AudioListener audioListener) {
        mAudioListener = audioListener;
    }

    public AudioListener getAudioListener() {
        return mAudioListener;
    }

    public void cleanUp() {
        //audio sources
        for (AudioSource audioSource : mAudioSourceMap.values()) {
            audioSource.cleanUp();
        }
        mAudioSourceMap.clear();

        //sound buffers
        for (AudioBuffer soundBuffer : mAudioBufferList) {
            soundBuffer.cleanUp();
        }
        mAudioBufferList.clear();

        if (mContext != NULL) {
            alcDestroyContext(mContext);
        }

        if (mDevice != NULL) {
            alcCloseDevice(mDevice);
        }
    }
}
