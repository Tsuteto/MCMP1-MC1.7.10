package tsuteto.mcmp.core.mcmpplayer;

public class PlayPosition {
	public int slotPlaying;
	public int playingInStack;
	
	public String toString()
	{
		return String.format("PLAYER-POS(%d, %d)", slotPlaying, playingInStack);
	}
}
