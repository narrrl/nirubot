package nirusu.nirubot.util;

import java.io.File;

import nirusu.nirubot.Nirubot;

public class DeleteThread extends Thread {
    private boolean isRunning = true;
    private File f;

    public DeleteThread(final File fileToDelete) {
        f = fileToDelete;
    }

    public void shutdown() {
        isRunning = false;
        interrupt();
    }

    public DeleteThread startAndReturn() {
        super.start();
        return this;
    }

    @Override
    public void run() {
        while(isRunning && f.exists()) {
            if (!f.exists() && f.delete()) {
                Nirubot.warning(String.format("Deleted file %s successfully",
                    f.getAbsolutePath()));
            }
            if (f.exists()) {
                try {
                    sleep(5000L);
                } catch (InterruptedException e) {
                    Nirubot.warning(String.format("Couldn't delete file %s",
                        f.getAbsolutePath()));
                    isRunning = false;
                }

            }
        }
    }
}
