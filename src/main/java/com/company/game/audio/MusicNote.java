package com.company.game.audio;

public enum MusicNote {

    C_1(32.7f, 1054.94f),
    C_SHARP_1(34.65f, 995.73f),
    D_1(36.71f, 939.85f),
    D_SHARP_1(38.89f, 887.1f),
    E_1(41.2f, 837.31f),
    F_1(43.65f, 790.31f),
    F_SHARP_1(46.25f, 745.96f),
    G_1(49.0f, 704.09f),
    G_SHARP_1(51.91f, 664.57f),
    A_1(55.0f, 627.27f),
    A_SHARP_1(58.27f, 592.07f),
    B_1(61.74f, 558.84f);

    private float mFrequency;
    private float mWaveLength;

    MusicNote(float frequency, float waveLength) {
        mFrequency = frequency;
        mWaveLength = waveLength;
    }

    public float getFrequency() {
        return mFrequency;
    }

    public float getWaveLength() {
        return mWaveLength;
    }
}
