package com.phoxell.helper;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by sologram on 1/30/15.
 */

public class Beep {
	private final static int RAT = 8000;
	private final static short A = 12287;
	private final static short B = -A;

	private final static short wav[][] = {
		{A, A, B, B, A, A, B, B, A, A, B, B, A, A, B, B},
		{A, B, A, B, A, B, A, B, A, B, A, B, A, B, A, B},
	};
	final static AudioTrack aud[] = init();

	private static AudioTrack[] init() {
		AudioTrack[] re = new AudioTrack[wav.length];
		for (int i = 0; i < wav.length; i++) {
			short w[] = wav[i];
			AudioTrack a = new AudioTrack(AudioManager.STREAM_MUSIC,
				RAT, AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT, w.length + w.length,
				AudioTrack.MODE_STATIC);
			a.write(w, 0, w.length);
			a.setLoopPoints(0, w.length, -1);
			re[i] = a;
		}
		return re;
	}

	private static void beep(int index) {
		try {
			aud[index].play();
			Thread.sleep(12);
			aud[index].pause();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void b0() {
		beep(0);
	}

	public static void b1() {
		beep(1);
	}
}
