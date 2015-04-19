package tsuteto.mcmp.core.asm.entry;

import cpw.mods.fml.relauncher.Side;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import tsuteto.mcmp.core.asm.AsmPetitUtil;
import tsuteto.mcmp.core.asm.ITransformerEntry;

import java.util.EnumSet;

public class TEntryMusicType implements ITransformerEntry, Opcodes
{
    @Override
    public String getTargetClass()
    {
        return "net.minecraft.client.Minecraft";
    }

    @Override
    public String getTargetMethodDeobf()
    {
        return "func_147109_W";
    }

    @Override
    public String getTargetMethodObf()
    {
        return "func_147109_W";
    }

    @Override
    public String getTargetMethodDesc()
    {
        return "()Lnet/minecraft/client/audio/MusicTicker$MusicType;";
    }

    @Override
    public void transform(MethodNode mnode, ClassNode cnode)
    {
        String musicType = AsmPetitUtil.getActualClass("net/minecraft/client/audio/MusicTicker$MusicType");

        InsnList overrideList = new InsnList();

        overrideList.add(new MethodInsnNode(INVOKESTATIC,
                "tsuteto/mcmp/core/audio/McmpSoundManager",
                "getInstance",
                "()Ltsuteto/mcmp/core/audio/McmpSoundManager;", false));
        overrideList.add(new MethodInsnNode(INVOKEVIRTUAL,
                "tsuteto/mcmp/core/audio/McmpSoundManager",
                "isBgmPlaying",
                "()Z", false));
        LabelNode l1 = new LabelNode();
        overrideList.add(new JumpInsnNode(IFEQ, l1));
        overrideList.add(new FieldInsnNode(GETSTATIC, "tsuteto/mcmp/core/Mcmp1Core", "MUSIC_TYPE_MCMP1", "L" + musicType + ";"));
        overrideList.add(new InsnNode(ARETURN));
        overrideList.add(l1);

//        INVOKESTATIC tsuteto/mcmp/core/audio/McmpSoundManager.getInstance ()Ltsuteto/mcmp/core/audio/McmpSoundManager;
//        INVOKEVIRTUAL tsuteto/mcmp/core/audio/McmpSoundManager.playing ()Z
//        IFEQ L1
//        L2
//        LINENUMBER 16 L2
//        GETSTATIC tsuteto/mcmp/core/McmpMain.MUSIC_TYPE_MCMP1 : Lnet/minecraft/client/audio/MusicTicker$MusicType;
//        ARETURN
//        L1

        mnode.instructions.insert(mnode.instructions.get(1), overrideList);
    }

    @Override
    public EnumSet<Side> getSide()
    {
        return EnumSet.of(Side.CLIENT);
    }
}
