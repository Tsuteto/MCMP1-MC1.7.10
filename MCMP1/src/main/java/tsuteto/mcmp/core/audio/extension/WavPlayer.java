/*
 * 11/19/04		1.0 moved to LGPL.
 *-----------------------------------------------------------------------
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *----------------------------------------------------------------------
 */

package tsuteto.mcmp.core.audio.extension;

import com.sun.media.sound.WaveFileReader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tsuteto.mcmp.core.util.McmpLog;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

@SideOnly(Side.CLIENT)
public class WavPlayer implements ExternalAudioPlayer
{
    private final int EXTERNAL_BUFFER_SIZE = 8 * 1024;

    private AudioInputStream stream;
    private SourceDataLine line;
    private FloatControl volumeControl;
    private boolean playing = false;

    public WavPlayer(File in) throws Exception
    {
        this.stream = new WaveFileReader().getAudioInputStream(in);
        AudioFormat format = stream.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        Line line = AudioSystem.getLine(info);

        if (line instanceof SourceDataLine)
        {
            this.line = (SourceDataLine) line;
            this.line.open(format);

            if (this.line.isControlSupported(FloatControl.Type.MASTER_GAIN))
            {
                volumeControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            }
            else if (this.line.isControlSupported(FloatControl.Type.VOLUME))
            {
                volumeControl = (FloatControl) line.getControl(FloatControl.Type.VOLUME);
            }
            else
            {
                McmpLog.warn("WAV Player: Unable to control the volume");
            }
        }
    }

    public void play() throws Exception
    {
        try
        {
            playing = true;
            line.start();

            int bytesRead = 0;
            byte[] buf = new byte[EXTERNAL_BUFFER_SIZE];
            try
            {
                while (bytesRead != -1)
                {
                    bytesRead = stream.read(buf, 0, buf.length);
                    if (bytesRead >= 0)
                    {
                        line.write(buf, 0, bytesRead);
                    }
                }
            }
            catch (IOException e)
            {
                McmpLog.warn(e, "Failed playing WAV");
            }
        }
        finally
        {
            this.stop();
        }
    }

    public synchronized void close()
    {
        if (line != null)
        {
            line.stop();
            line.close();
        }
        playing = false;
    }

    public void stop()
    {
        close();
    }

    public boolean playing()
    {
        return playing;
    }

    public void setVolume(float volume)
    {
        float volumeDb = 20 * (float)Math.log(volume);
        if (volumeControl != null)
        {
            if (volumeDb > volumeControl.getMinimum())
            {
                volumeControl.setValue(volumeDb);
            }
            else
            {
                volumeControl.setValue(volumeControl.getMinimum());
            }
        }
    }
}