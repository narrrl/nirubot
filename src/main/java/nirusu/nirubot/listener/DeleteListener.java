package nirusu.nirubot.listener;

import nirusu.nirubot.util.DeleteThread;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public class DeleteListener implements NiruListener {
    private static DeleteListener listener;
    private ArrayList<DeleteThread> deleteRequests;

    private DeleteListener() {
        deleteRequests = new ArrayList<>();
    }

    public static DeleteListener getInstance() {
        if (listener == null) {
            listener = new DeleteListener();
        }
        return listener;
    }

    public void add(@Nonnull final DeleteThread t) {
        deleteRequests.add(t);
    }

    @Override
    public void shutdown() {
        deleteRequests.forEach(DeleteThread::shutdown);
    }


}
