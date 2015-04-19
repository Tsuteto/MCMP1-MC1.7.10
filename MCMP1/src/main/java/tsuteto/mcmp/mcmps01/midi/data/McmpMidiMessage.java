package tsuteto.mcmp.mcmps01.midi.data;

import com.google.common.collect.Maps;

import javax.sound.midi.MidiMessage;
import java.util.HashMap;

public class McmpMidiMessage
{
    private static HashMap<Integer, StatusCode> STATUS_MAPPING = Maps.newHashMap();

    public enum StatusCode
    {
        // System common messages
        MIDI_TIME_CODE(0xF1), // 241
        SONG_POSITION_POINTER(0xF2), // 242
        SONG_SELECT(0xF3), // 243
        TUNE_REQUEST(0xF6), // 246
        END_OF_EXCLUSIVE(0xF7), // 247

        // System real-time messages
        TIMING_CLOCK(0xF8), // 248
        START(0xFA), // 250
        CONTINUE(0xFB), // 251
        STOP(0xFC), // 252
        ACTIVE_SENSING(0xFE), // 254
        SYSTEM_RESET(0xFF), // 255

        // Channel voice message upper nibble defines
        NOTE_OFF(0x80),  // 128
        NOTE_ON(0x90),  // 144
        POLY_PRESSURE(0xA0),  // 160
        CONTROL_CHANGE(0xB0),  // 176
        PROGRAM_CHANGE(0xC0),  // 192
        CHANNEL_PRESSURE(0xD0),  // 208
        PITCH_BEND(0xE0)  // 224
        ;

        public final int code;

        StatusCode(int code)
        {
            this.code = code;
            STATUS_MAPPING.put(code, this);
        }
    }

    public MidiMessage msg;

    public McmpMidiMessage(MidiMessage msg)
    {
        this.msg = msg;
    }

    public boolean isShortMessage()
    {
        return this.msg.getMessage().length <= 3;
    }

    public boolean isChannelMessage()
    {
        return (this.msg.getStatus() & 0xf0) != 0xf0;
    }

    public int getChannel()
    {
        return this.msg.getStatus() & 0xf;
    }

    public StatusCode getStatus()
    {
        if (this.isChannelMessage())
        {
            return STATUS_MAPPING.get(this.msg.getStatus() & 0xf0);
        }
        else
        {
            return STATUS_MAPPING.get(this.msg.getStatus());
        }
    }

    public int getData1()
    {
        return this.msg.getMessage()[1];
    }

    public int getData2()
    {
        return this.msg.getMessage()[2];
    }
}
