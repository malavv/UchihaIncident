package com.ninja.exMenu;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class SoundManager {
	private SoundPool mSound;
	private HashMap<Integer, Integer> mSoundPoolMap;
	private AudioManager mAudioManager;
	private Context mContext;
	private static MediaPlayer mPlayer_;
	
	public static void InitBG(Context ctx, int resId) {
	   if (mPlayer_ == null)
	      mPlayer_ = MediaPlayer.create(ctx, resId);
	   mPlayer_.setLooping(true);
	}
	
	public static void PlayBGMusic() {
	  mPlayer_.start();
	}
	public static void PauseBGMusic() {
	  if (mPlayer_.isPlaying())  mPlayer_.pause();
	}
	public static void ReleaseBG() {
	  mPlayer_.seekTo(0);
	}
	
	public void Init(Context c) {
		mContext = c;
		mSound = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		mSoundPoolMap = new HashMap<Integer, Integer>();
		mAudioManager = (AudioManager)c.getSystemService(Context.AUDIO_SERVICE);
	}
	public void Load(int index, int id) { mSoundPoolMap.put(index, mSound.load(mContext, id, 1)); }
	public void playSound(int index) {
	  float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	  streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	  mSound.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, 1f);
	}
	 
	public void playLoopedSound(int index) {
	    float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	    streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	    mSound.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, -1, 1f);
	}
}
