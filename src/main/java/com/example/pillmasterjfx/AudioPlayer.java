package com.example.pillmasterjfx;

import com.adonax.audiocue.AudioCue;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class AudioPlayer {
    AudioCue audioCue;

    public AudioPlayer() throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        URL url = new File("sound.wav").toURI().toURL();
        audioCue = AudioCue.makeStereoCue(url, 4);
        audioCue.open();
    }
    public void play() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        audioCue.play();
    }

    public void close() {
        audioCue.close();
    }
}
