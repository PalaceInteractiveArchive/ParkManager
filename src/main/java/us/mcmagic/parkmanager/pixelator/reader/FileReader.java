package us.mcmagic.parkmanager.pixelator.reader;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public abstract class FileReader {

    protected String resourceFileName;
    protected String outputPath;
    protected String outputFileName;
    protected File outputFile;


    protected FileReader(String resourceFileName, String outputPath, String outputFileName) {
        this.resourceFileName = resourceFileName;
        this.outputPath = outputPath;
        this.outputFileName = outputFileName;
        this.outputFile = new File(outputPath + (outputPath.endsWith("/") ? "" : "/") + outputFileName);
    }

    protected FileReader(String fileName, String outputPath) {
        this.resourceFileName = fileName;
        this.outputPath = outputPath;
        this.outputFileName = fileName;
        this.outputFile = new File(outputPath + (outputPath.endsWith("/") ? "" : "/") + this.outputFileName);
    }

    protected void deleteFile() {
        if (this.outputFile.exists()) {
            this.outputFile.delete();
        }

    }

    protected boolean saveResourceFile(Plugin plugin) {
        InputStream in = plugin.getResource(this.resourceFileName);
        if (in == null) {
            return false;
        } else {
            (new File(this.outputPath)).mkdirs();

            try {
                FileOutputStream ex = new FileOutputStream(this.outputFile);
                byte[] buf = new byte[1024];

                int len;
                while ((len = in.read(buf)) > 0) {
                    ex.write(buf, 0, len);
                }

                ex.close();
                in.close();
                return true;
            } catch (Exception var6) {
                return false;
            }
        }
    }

    public String getResourceFileName() {
        return this.resourceFileName;
    }

    public String getOuputPath() {
        return this.outputPath;
    }

    public String getOuputFileName() {
        return this.outputFileName;
    }

    public File getOuputFile() {
        return this.outputFile;
    }
}
