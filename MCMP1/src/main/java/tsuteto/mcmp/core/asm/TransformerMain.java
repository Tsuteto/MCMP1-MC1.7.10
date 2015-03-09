package tsuteto.mcmp.core.asm;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import tsuteto.mcmp.core.asm.entry.TEntryMusicType;
import tsuteto.mcmp.core.util.McmpLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransformerMain implements IClassTransformer, Opcodes
{
    private static final String TENTRY_PKG = "tsuteto.mcmp.core.asm.entry";
    private static boolean debug = McmpLog.isDebug;
    private final Map<String, List<ITransformerEntry>> classToEntryMapping;

    public TransformerMain()
    {
        ArrayList<ITransformerEntry> registry = new ArrayList<ITransformerEntry>();
        registry.add(new TEntryMusicType());

        classToEntryMapping = new HashMap<String, List<ITransformerEntry>>();
        for (ITransformerEntry entry : registry)
        {
            String className = entry.getTargetClass();
            List list = classToEntryMapping.get(className);
            if (list == null)
            {
                list = new ArrayList<ITransformerEntry>();
                list.add(entry);
                classToEntryMapping.put(className, list);
            }
            else
            {
                list.add(entry);
            }
        }
    }

    @Override
    public byte[] transform(String actual, String transformed, byte[] bytes)
    {
        // FMLLaunchHandler.side().equals(Side.SERVER)
        if (!classToEntryMapping.containsKey(transformed))
        {
            return bytes;
        }

        try
        {
            List<ITransformerEntry> list = classToEntryMapping.get(transformed);
            for (ITransformerEntry entry : list)
            {
                if (entry.getSide().contains(FMLLaunchHandler.side()))
                {
                    bytes = this.transformClass(bytes, entry, actual);
                }
            }
            classToEntryMapping.remove(transformed);
            return bytes;
        }
        catch (Exception e)
        {
            throw new RuntimeException("failed : Transformer loading", e);
        }
    }

    private byte[] transformClass(byte[] bytes, ITransformerEntry entry, String actualClassName)
    {

        ClassNode cnode = new ClassNode();
        ClassReader reader = new ClassReader(bytes);
        reader.accept(cnode, 0);

        String targetMethodNameDeobf = entry.getTargetMethodDeobf();
        String targetMethodNameObf = entry.getTargetMethodObf();

        String targetMethoddesc = entry.getTargetMethodDesc();

        MethodNode mnode = null;
        for (MethodNode curMnode : cnode.methods)
        {
            if (debug) System.out.printf("Class: %s, Method: %s, Desc: %s%n", cnode.name, curMnode.name, curMnode.desc);
            String srgClass = FMLDeobfuscatingRemapper.INSTANCE.map(actualClassName);
            String srgMethod = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(actualClassName, curMnode.name, curMnode.desc);
            String srgDesc = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(curMnode.desc);
            if (debug) System.out.printf("[SRG] Class: %s, Method: %s, Desc: %s%n", srgClass, srgMethod, srgDesc);

            if ((targetMethodNameDeobf.equals(srgMethod) || targetMethodNameObf.equals(srgMethod))
                    && targetMethoddesc.equals(srgDesc))
            {
                if (debug) System.out.println("-> * Detected! *");
                mnode = curMnode;
                break;
            }
        }

        if (mnode != null)
        {
            entry.transform(mnode, cnode);

            if (debug) System.out.println("-> Transformer applying to " + cnode.name);
            ClassWriter cw = new ClassWriter(0);
            cnode.accept(cw);
            bytes = cw.toByteArray();
            if (debug) dumpClassFile(bytes, actualClassName);
        }

        return bytes;
    }

    private static void dumpClassFile(byte[] bytes, String className)
    {
        File file = new File(System.getProperty("mcmp1.dumpClsDir"), className + ".class");

        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(file);
            fos.write(bytes);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (fos != null) fos.close();
            }
            catch (IOException ignored) {}
        }
    }
}
