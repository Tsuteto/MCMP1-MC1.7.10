package tsuteto.mcmp.core.mcmpplayer.data;

public class PlayPosition {
	public int slotPlaying;
	public int playingInStack;
	public boolean changed = false;
	
	public String toString()
	{
		return String.format("PLAYER-POS(%d, %d)", slotPlaying, playingInStack);
	}
}
