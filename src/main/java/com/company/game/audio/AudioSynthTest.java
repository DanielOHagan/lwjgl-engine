package com.company.game.audio;

import com.company.engine.IGameLogic;
import com.company.engine.input.KeyboardInput;
import com.company.engine.input.MouseInput;
import com.company.engine.window.Window;
/*
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.io.JavaSoundAudioIO;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.Glide;
import net.beadsproject.beads.ugens.WavePlayer;
*/
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class AudioSynthTest /*implements IGameLogic */{

    /*
    Simple Monophonic audio synth with 12 notes and 8 pitch levels
    REQUIRES BEADS PROJECT
     */

//    private static final float MIN_GAIN = 0;
//    private static final float MAX_GAIN = 2;
//    private static final float DEFAULT_GAIN = 1;
//
//    private static final int MAX_PITCH = 8;
//    private static final int MIN_PITCH = 1;
//    private static final int DEFAULT_PITCH = 4;
//
//    private static final boolean MONOPHONIC = true;
//
//    private Mixer.Info[] mMixerInfoArray;
//    private ArrayList<Mixer.Info> mUsableMixersInfo;
//    private int[] mSuitableMixerIndexes;
//
//    private AudioContext mAudioContext;
//    private JavaSoundAudioIO mJavaSoundAudioIo;
//
//    //private Map<MusicNote, WavePlayer> mWavePlayerMap;
//    private WavePlayer mWavePlayer;
//    private Gain mGain;
//    private Glide mGlide;
//
//    private int mPitch;
//    private int mPitchInc;
//    private float mGainValue;
//    private float mGainValueInc;
//
//    private boolean mUpPressed;
//    private boolean mDownPressed;
//
//    private MusicNote mLastNotePressed;
//
//    @Override
//    public void init(Window window) throws Exception {
//        mMixerInfoArray = AudioSystem.getMixerInfo();
//        mUsableMixersInfo = new ArrayList<>();
//        mSuitableMixerIndexes = getSuitableMixerIndexes(mMixerInfoArray);
//        mWavePlayerMap = new HashMap<>();
//
//        mGainValue = 1;
//        mGainValueInc = 0.01f;
//
//        mPitch = DEFAULT_PITCH;
//        mPitchInc = 1;
//
//        setUpAudioContext();
//
//        setUpAudioInput();
//
//        //begin audio processing
//        mAudioContext.start();
//    }
//
//    @Override
//    public void input(Window window, MouseInput mouseInput, KeyboardInput keyboardInput) {
//        //pitch controls
//        if (window.isKeyPressed(GLFW_KEY_UP) && !mDownPressed && !mUpPressed) {
//            mPitch += mPitchInc;
//
//            mUpPressed = true;
//            mDownPressed = false;
//
//            if (mPitch > MAX_PITCH) {
//                mPitch = MAX_PITCH;
//            }
//        } else if (window.isKeyPressed(GLFW_KEY_DOWN) && !mDownPressed && !mUpPressed) {
//            mPitch -= mPitchInc;
//
//            mUpPressed = false;
//            mDownPressed = true;
//
//            if (mPitch < MIN_PITCH) {
//                mPitch = MIN_PITCH;
//            }
//        }
//
//        if (window.isKeyReleased(GLFW_KEY_UP) && mUpPressed && !mDownPressed) {
//            mUpPressed = false;
//        } else if (window.isKeyReleased(GLFW_KEY_DOWN) && !mUpPressed && mDownPressed) {
//            mDownPressed = false;
//        }
//
//        //gain controls
//        if (window.isKeyPressed(GLFW_KEY_BACKSPACE)) {
//            mGainValue += mGainValueInc;
//
//            if (mGainValue > MAX_GAIN) {
//                mGainValue = MAX_GAIN;
//            }
//        } else if (window.isKeyPressed(GLFW_KEY_ENTER)) {
//            mGainValue -= mGainValueInc;
//
//            if (mGainValue < MIN_GAIN) {
//                mGainValue = MIN_GAIN;
//            }
//        }
//
//        //notes
//        //c
//        if (window.isKeyPressed(GLFW_KEY_Q)) {
//            playNote(MusicNote.C_1);
//        } else if (mLastNotePressed == MusicNote.C_1){
//            stopPlayingNote(MusicNote.C_1);
//        }
//
//        //c#
//        if (window.isKeyPressed(GLFW_KEY_W)) {
//            playNote(MusicNote.C_SHARP_1);
//        } else if (mLastNotePressed == MusicNote.C_SHARP_1) {
//            stopPlayingNote(MusicNote.C_SHARP_1);
//        }
//
//        //d
//        if (window.isKeyPressed(GLFW_KEY_E)) {
//            playNote(MusicNote.D_1);
//        } else if (mLastNotePressed == MusicNote.D_1) {
//            stopPlayingNote(MusicNote.D_1);
//        }
//
//        //d#
//        if (window.isKeyPressed(GLFW_KEY_R)) {
//            playNote(MusicNote.D_SHARP_1);
//        } else if (mLastNotePressed == MusicNote.D_SHARP_1) {
//            stopPlayingNote(MusicNote.D_SHARP_1);
//        }
//
//        //e
//        if (window.isKeyPressed(GLFW_KEY_T)) {
//            playNote(MusicNote.E_1);
//        } else if (mLastNotePressed == MusicNote.E_1) {
//            stopPlayingNote(MusicNote.E_1);
//        }
//
//        //f
//        if (window.isKeyPressed(GLFW_KEY_Y)) {
//            playNote(MusicNote.F_1);
//        } else if (mLastNotePressed == MusicNote.F_1) {
//            stopPlayingNote(MusicNote.F_1);
//        }
//
//        //f#
//        if (window.isKeyPressed(GLFW_KEY_U)) {
//            playNote(MusicNote.F_SHARP_1);
//        } else if (mLastNotePressed == MusicNote.F_SHARP_1) {
//            stopPlayingNote(MusicNote.F_SHARP_1);
//        }
//
//        //g
//        if (window.isKeyPressed(GLFW_KEY_I)) {
//            playNote(MusicNote.G_1);
//        } else if (mLastNotePressed == MusicNote.G_1) {
//            stopPlayingNote(MusicNote.G_1);
//        }
//
//        //g#
//        if (window.isKeyPressed(GLFW_KEY_O)) {
//            playNote(MusicNote.G_SHARP_1);
//        } else if (mLastNotePressed == MusicNote.G_SHARP_1) {
//            stopPlayingNote(MusicNote.G_SHARP_1);
//        }
//
//        //a
//        if (window.isKeyPressed(GLFW_KEY_P)) {
//            playNote(MusicNote.A_1);
//        } else if (mLastNotePressed == MusicNote.A_1) {
//            stopPlayingNote(MusicNote.A_1);
//        }
//
//        //a#
//        if (window.isKeyPressed(GLFW_KEY_LEFT_BRACKET)) {
//            playNote(MusicNote.A_SHARP_1);
//        } else if (mLastNotePressed == MusicNote.A_SHARP_1) {
//            stopPlayingNote(MusicNote.A_SHARP_1);
//        }
//
//        //b
//        if (window.isKeyPressed(GLFW_KEY_RIGHT_BRACKET)) {
//            playNote(MusicNote.B_1);
//        } else if (mLastNotePressed == MusicNote.B_1) {
//            stopPlayingNote(MusicNote.B_1);
//        }
//    }
//
//    @Override
//    public void update(float interval, MouseInput mouseInput, KeyboardInput keyboardInput) {
//        mGain.setGain(mGainValue);
//    }
//
//    @Override
//    public void render(Window window) {
//
//    }
//
//    @Override
//    public void cleanUp() {
//        mAudioContext.stop();
//        mAudioContext = null;
//    }
//
//    private void playNote(MusicNote note) {
//        WavePlayer wavePlayer = mWavePlayerMap.get(note);
//        WavePlayer wavePlayer = mWavePlayer;
//
//        if (wavePlayer != null) {
//            wavePlayer.setFrequency(note.getFrequency() * mPitch);
//        }
//
//        if (mGlide != null && mLastNotePressed == note) {
//            mGlide.setValue(0.9f);
//        }
//
//        mLastNotePressed = note;
//    }
//
//    private void stopPlayingNote(MusicNote note) {
//        WavePlayer wavePlayer = mWavePlayerMap.get(note);
//        WavePlayer wavePlayer = mWavePlayer;
//
//        if (wavePlayer != null) {
//            wavePlayer.setFrequency(0);
//        }
//
//        if (mGlide != null) {
//            mGlide.setValue(0);
//        }
//    }
//
//    private void setUpAudioContext() {
//        mJavaSoundAudioIo = new JavaSoundAudioIO();
//
//        //select a mixer from mSuitableMixerIndexes
//        mJavaSoundAudioIo.selectMixer(3);
//
//        //create an audio context
//        mAudioContext = new AudioContext(mJavaSoundAudioIo);
//    }
//
//    private void setUpAudioInput() {
//        for (MusicNote note : MusicNote.values()) {
//            mWavePlayerMap.put(note, new WavePlayer(
//                    mAudioContext,
//                    0,
//                    Buffer.SINE
//            ));
//        }
//        mWavePlayer = new WavePlayer(
//                mAudioContext,
//                0,
//                Buffer.SINE
//        );
//
//        mGlide = new Glide(
//                mAudioContext,
//                0,
//                50
//        );
//
//        mGain = new Gain(
//                mAudioContext,
//                1,
//                mGlide
//        );
//
//        for (WavePlayer wavePlayer : mWavePlayerMap.values()) {
//            mGain.addInput(wavePlayer);
//        }
//        mGain.addInput(mWavePlayer);
//
//
//        mAudioContext.out.addInput(mGain);
//    }
//
//    public static int[] getSuitableMixerIndexes(Mixer.Info[] array) {
//        int[] mixerIndexes = new int[array.length];
//        int nonNullIndexPointersIndex = 0;
//
//        for (int i = 0; i < array.length; i++) {
//            if (isMixerSuitable(array[i])) {
//                mixerIndexes[nonNullIndexPointersIndex] = i;
//                nonNullIndexPointersIndex++;
//            }
//        }
//
//        int[] suitableMixerIndexes = new int[nonNullIndexPointersIndex];
//
//        System.arraycopy(
//                mixerIndexes,
//                0,
//                suitableMixerIndexes,
//                0,
//                suitableMixerIndexes.length
//        );
//
//        /*
//        Need to add more checks as this doesn't always work
//         */
//
//        return suitableMixerIndexes;
//    }
//
//    private static boolean isMixerSuitable(Mixer.Info mixerInfo) {
//        return mixerInfo != null && !mixerInfo.getName().equals("Port Unknown Name");
//    }
}