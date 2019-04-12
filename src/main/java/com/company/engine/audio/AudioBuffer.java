package com.company.engine.audio;

import com.company.engine.IUsesResources;
import com.company.engine.utils.FileUtils;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class AudioBuffer implements IUsesResources {

    private final int mBufferId;

    private ShortBuffer mPcmBuffer;
    private ByteBuffer mVorbisBuffer;

    public AudioBuffer(String fileName) throws Exception {
        mPcmBuffer = null;
        mVorbisBuffer = null;

        mBufferId = alGenBuffers();

        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
            ShortBuffer pcm = readVorbis(fileName, 32 * 1024, info);

            alBufferData(
                    mBufferId,
                    info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16,
                    pcm,
                    info.sample_rate()
            );
        }
    }

    public void cleanUp() {
        alDeleteBuffers(mBufferId);

        if (mPcmBuffer != null) {
            MemoryUtil.memFree(mPcmBuffer);
        }
    }

    private ShortBuffer readVorbis(
            String fileName,
            int bufferSize,
            STBVorbisInfo info
    ) throws Exception {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            mVorbisBuffer = FileUtils.ioResourceToByteBuffer(fileName, bufferSize);
            IntBuffer error = stack.mallocInt(1);
            long decoder = stb_vorbis_open_memory(mVorbisBuffer, error, null);

            if (decoder == NULL) {
                throw new RuntimeException(
                        "Failed to open .ogg Vorbis file. Error: " +
                                error.get(0)
                );
            }

            stb_vorbis_get_info(decoder, info);

            int channels = info.channels();
            int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);

            mPcmBuffer = MemoryUtil.memAllocShort(lengthSamples);
            mPcmBuffer.limit(
                    stb_vorbis_get_samples_short_interleaved(
                            decoder, channels, mPcmBuffer
                    ) * channels
            );

            stb_vorbis_close(decoder);

            return mPcmBuffer;
        }
    }

    public int getBufferId() {
        return mBufferId;
    }
}