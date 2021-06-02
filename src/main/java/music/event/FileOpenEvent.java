package music.event;

import java.io.File;

public class FileOpenEvent {
    File file;

    public FileOpenEvent(File file) {
        super();
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }


}
