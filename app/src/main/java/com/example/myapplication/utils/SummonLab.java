package com.example.myapplication.utils;

import android.content.Context;

import com.example.myapplication.model.Summon;

import java.util.ArrayList;
import java.util.List;

public class SummonLab {
    private static SummonLab    sSummonLab;
    private        List<Summon> mSummons;

    public static SummonLab get(Context context) {
        if (sSummonLab == null) {
            sSummonLab = new SummonLab(context);
        }
        return sSummonLab;
    }

    private SummonLab(Context context) {
        mSummons = new ArrayList<>();
//        mRealSummons = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            RealSummon realSummon = new RealSummon();
//
//        }
    }

    public void addSummon(Summon a) {
        ArrayList<String> ids   = new ArrayList<>();
        int               count = 0;
        for (Summon summon : mSummons) {
            ids.add(summon.getServer_id());
        }
        if (!ids.contains(a.getServer_id())) {
            mSummons.add(a);
        }
    }

    public List<Summon> getRealSummons() {
        return mSummons;
    }

    public Summon getRealSummon(String id) {
        for (Summon summon : mSummons) {
            if (summon.getServer_id() == id) {
                return summon;
            }
        }

        return null;
    }

}


