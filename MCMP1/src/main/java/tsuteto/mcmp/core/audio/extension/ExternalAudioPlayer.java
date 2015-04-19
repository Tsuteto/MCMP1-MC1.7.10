package tsuteto.mcmp.core.audio.extension;

/**
 * Created by Tsuteto on 15/03/08.
 */
public interface ExternalAudioPlayer
{
    void play() throws Exception;
    void pause();
    void resume();
    void stop();
    boolean playing();
    boolean paused();
    void setGain(float volume);
    void setPan(float ratio);
}
