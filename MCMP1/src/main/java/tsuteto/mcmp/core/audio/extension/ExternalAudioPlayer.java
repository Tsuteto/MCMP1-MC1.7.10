package tsuteto.mcmp.core.audio.extension;

/**
 * Created by Tsuteto on 15/03/08.
 */
public interface ExternalAudioPlayer
{
    void play() throws Exception;
    void stop();
    boolean playing();
    public void setVolume(float volume);
}
