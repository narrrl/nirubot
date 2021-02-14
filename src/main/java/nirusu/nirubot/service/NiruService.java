package nirusu.nirubot.service;

public interface NiruService extends Runnable {
    void shutdown();
    default String name() {
        return this.getClass().getSimpleName();
    }
}
