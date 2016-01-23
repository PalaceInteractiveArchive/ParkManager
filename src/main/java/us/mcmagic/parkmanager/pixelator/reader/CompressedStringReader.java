package us.mcmagic.parkmanager.pixelator.reader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class CompressedStringReader extends FileReader {

    public CompressedStringReader(String fileName, String directoryName) {
        super(fileName, directoryName);
    }

    public String readFromFile() throws Exception {
        if (!this.outputFile.exists()) {
            return null;
        } else {
            InflaterInputStream in = new InflaterInputStream(new FileInputStream(this.outputFile));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];

            int len;
            while ((len = in.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }

            in.close();
            return new String(baos.toByteArray(), "UTF-8");
        }
    }

    public boolean saveToFile(String s) {
        if (!this.outputFile.exists()) {
            (new File(this.outputPath)).mkdirs();
        }

        try {
            DeflaterOutputStream e = new DeflaterOutputStream(new FileOutputStream(this.outputFile));
            e.write(s.getBytes("UTF-8"));
            e.flush();
            e.close();
            return true;
        } catch (Exception var3) {
            return false;
        }
    }

    public void deleteFile() {
        super.deleteFile();
    }
}
