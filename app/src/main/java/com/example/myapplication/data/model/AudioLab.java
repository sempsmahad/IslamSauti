package com.example.myapplication.data.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class AudioLab {
    private static AudioLab        sAudioLab;
    private        List<RealAudio> mRealAudios;

    public static AudioLab get(Context context) {
        if (sAudioLab == null) {
            sAudioLab = new AudioLab(context);
        }
        return sAudioLab;
    }

    private AudioLab(Context context) {
        mRealAudios = new ArrayList<>();
//        mRealAudios = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            RealAudio realAudio = new RealAudio();
//
//        }
    }

    public void addAudio(RealAudio a) {
        ArrayList<Integer> ids   = new ArrayList<>();
        int                count = 0;
        for (RealAudio realAudio : mRealAudios) {
            ids.add(realAudio.id);
        }
        if (!ids.contains(a.getId())) {
            mRealAudios.add(a);
        }
    }

    public List<RealAudio> getRealAudios() {
        return mRealAudios;
    }

    public RealAudio getRealAudio(int id) {
        for (RealAudio realAudio : mRealAudios) {
            if (realAudio.getId() == id) {
                return realAudio;
            }
        }

        return null;
    }

}


