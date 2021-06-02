package music.event;

import java.io.File;

public class FileSaveEvent {
    File file;

    public FileSaveEvent(File file) {
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
