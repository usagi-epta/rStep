package com.studionex.jrStepGUI.rStep;

import java.io.File;

public class RStepPlayer {
	private PlayThread playThread;

	public void openFile(File gcodeFile, RStep rStep) {
		playThread = new PlayThread(gcodeFile, rStep);
	}
	
	public boolean hasPlayer() {
		return playThread != null;
	}
	
	public boolean playerIsPlaying() {
		return hasPlayer() && playThread.isPlaying();
	}
	
	public void playerPlay() {
		if(hasPlayer())
			playThread.play();
	}

	public void playerPause() {
		if(hasPlayer())
			playThread.pause();
	}
	
	public void playerAbort() {
		if(hasPlayer()) {
			playThread.abort();
			playThread = null;
		}
	}
}
