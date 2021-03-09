package me.ichun.mods.hats.common.hats;

import com.google.common.base.Splitter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.ichun.mods.hats.common.Hats;
import me.ichun.mods.ichunutil.common.module.tabula.formats.ImportList;
import me.ichun.mods.ichunutil.common.module.tabula.project.Project;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class HatResourceHandler
{
    public static final HashMap<String, HatInfo> HATS = new HashMap<>(); //Our reliance on Tabula is staggering.
    public static final Splitter COLON_SPLITTER = Splitter.on(":").trimResults().omitEmptyStrings();

    private static Path hatsDir;
    private static boolean init;
    public static synchronized boolean init()
    {
        if(!init)
        {
            init = true;

            try
            {
                hatsDir = FMLPaths.MODSDIR.get().resolve(Hats.MOD_ID);
                if(!Files.exists(hatsDir)) Files.createDirectory(hatsDir);

                File extractedMarker = new File(hatsDir.toFile(), "files.extracted");
                if(!extractedMarker.exists()) //presume we haven't extracted anything yet
                {
                    InputStream in = Hats.class.getResourceAsStream("/hats.zip");
                    if(in != null)
                    {
                        ZipInputStream zipStream = new ZipInputStream(in);
                        ZipEntry entry = null;

                        while((entry = zipStream.getNextEntry()) != null)
                        {
                            File file = new File(hatsDir.toFile(), entry.getName());
                            if(file.exists() && file.length() > 3L)
                            {
                                continue;
                            }

                            if(entry.isDirectory())
                            {
                                if(!file.exists())
                                {
                                    file.mkdirs();
                                }
                            }
                            else
                            {
                                FileOutputStream out = new FileOutputStream(file);

                                byte[] buffer = new byte[8192];
                                int len;
                                while((len = zipStream.read(buffer)) != -1)
                                {
                                    out.write(buffer, 0, len);
                                }
                                out.close();
                            }
                        }
                        zipStream.close();
                    }

                    FileUtils.writeStringToFile(extractedMarker, "", StandardCharsets.UTF_8);
                }
            }
            catch(IOException e)
            {
                Hats.LOGGER.fatal("Error initialising Hats resources!");
                e.printStackTrace();
                return false;
            }
        }
        return init;
    }

    public static Path getHatsDir()
    {
        return hatsDir;
    }

    public static void loadAllHats() //TODO hat accessory clash layer
    {
        HATS.clear();

        int count = 0;
        File dir = getHatsDir().toFile();
        count += scourForHats(dir);

        Hats.LOGGER.info("Loaded {} hats.", count);
    }

    private static int scourForHats(File dir)
    {
        int count = 0;
        File[] files = dir.listFiles();
        for(File file : files)
        {
            if(file.isDirectory())
            {
                count += scourForHats(file);
            }
            else if(file.getName().endsWith(".tbl") && readHat(file)) //tabula format
            {
                count++;
            }
        }
        return count;
    }

    public static boolean readHat(File file)
    {
        Project project = ImportList.createProjectFromFile(file);
        if(project == null)
        {
            Hats.LOGGER.warn("Error reading Tabula file: {}", file);
            return false;
        }
        else
        {
            if(project.tampered)
            {
                Hats.LOGGER.warn("This hat file was tampered (which will be loaded anyway): {}", file);
            }

            if(project.isOldTabula)
            {
                repairOldHat(file, project);
                Hats.LOGGER.warn("Loaded an old Tabula file. Updating to new Tabula & Hats format: {}", file);
            }

//            if(file.getName().startsWith("(C) ")) //it's a contributor hat
//            {
//                parseMeta(file, project);
//            }

            String hatName = file.getName().substring(0, file.getName().length() - 4);

            HATS.put(hatName, createHatInfoFor(hatName, project));

            return true;
        }
    }

    private static HatInfo createHatInfoFor(@Nonnull String name, @Nonnull Project project)
    {
        return new HatInfo(name, project);
    }

    public static HatInfo getAndSetAccessories(String details)
    {
        List<String> strings = COLON_SPLITTER.splitToList(details);
        if(!strings.isEmpty())
        {
            HatInfo info = HATS.get(strings.get(0));
            if(info != null)
            {
                ArrayList<String> accessories = new ArrayList<>();
                for(int i = 1; i < strings.size(); i++)
                {
                    accessories.add(strings.get(i));
                }
                info.setAccessoriesState(accessories);

                return info;
            }
        }
        return null;
    }

    private static void repairOldHat(File file, Project project)
    {
        project.name = file.getName().substring(0, file.getName().length() - 4);
        project.author = "";
        project.isOldTabula = false;
        project.isDirty = true;
        ArrayList<Project.Part> allParts = project.getAllParts();
        for(Project.Part part : allParts)
        {
            part.rotPY -= 16F; //we move the hat template up
        }
        project.save(file);
    }

    private static void parseMeta(File file, Project project)
    {
        boolean hasForcePool = false;
        boolean hasPool = false;
        for(int i = project.notes.size() - 1; i >= 0; i--)
        {
            String note = project.notes.get(i);
            if(note.startsWith("Hats:{"))//we found the meta
            {
                project.notes.remove(i);
                try
                {
                    JsonObject element = new JsonParser().parse(note.substring(5).trim()).getAsJsonObject();
                    if(element.has("uuid"))
                    {
                        project.notes.add("hats-contributor-uuid:" + element.get("uuid").getAsString());
                    }
                    if(element.has("isMiniMe"))
                    {
                        project.notes.add("hats-contributor-mini-me:" + element.get("isMiniMe").getAsBoolean());
                    }
                }
                catch(Throwable t)
                {
                    t.printStackTrace();
                }
            }
            if(note.startsWith("hats-rarity"))
            {
                hasForcePool = true;
            }
            if(note.startsWith("hats-pool"))
            {
                hasPool = true;
            }
        }
        if(!hasForcePool)
        {
            project.notes.add("hats-rarity:legendary");
        }
        if(!hasPool)
        {
            project.notes.add("hats-pool:contributors");
        }
        project.save(file);
    }
}
