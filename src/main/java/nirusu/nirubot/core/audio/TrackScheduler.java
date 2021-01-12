package nirusu.nirubot.core.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

/**
 * This class schedules tracks for the audio player. It contains the queue of
 * tracks.
 */
public class TrackScheduler extends AudioEventAdapter {
    private final List<AudioTrack> queue;
    private final AudioPlayer player;
    private boolean repeat;

  public TrackScheduler(final AudioPlayer player) {
    // The queue may be modifed by different threads so guarantee memory safety
    // This does not, however, remove several race conditions currently present
    queue = Collections.synchronizedList(new LinkedList<>());
    this.player = player;
  }

    public List<AudioTrack> getQueue() {
        return queue;
    }

    public boolean play(final AudioTrack track) {
        return play(track, false);
    }

    public boolean play(final AudioTrack track, final boolean force) {
        final boolean playing = player.startTrack(track, !force);

        if (!playing) {
            queue.add(track);
        }

        return playing;
    }

    public boolean skip() {
        AudioTrack track = null;
        boolean skipped = !queue.isEmpty() && play((track = queue.remove(0)), true);
        if (skipped && track != null && repeat) {
            play(track.makeClone());
        }
        return skipped;
    }

    @Override
    public void onTrackEnd(final AudioPlayer player, final AudioTrack track, final AudioTrackEndReason endReason) {
        // Advance the player if the track completed naturally (FINISHED) or if the
        // track cannot play (LOAD_FAILED)
        if (endReason.mayStartNext) {
            skip();
        }
    }

    public synchronized void shuffle() {
        ArrayList<AudioTrack> tmp = new ArrayList<>();

        for (Object o : queue.toArray()) {
            tmp.add((AudioTrack) o);
        }

        Collections.shuffle(tmp);

        queue.clear();

        for (AudioTrack t : tmp) {
            queue.add(t);
        }

    }

    public synchronized List<AudioTrackInfo> getAllTrackInfos() {
        ArrayList<AudioTrackInfo> tmp = new ArrayList<>();

        for (Object o : queue.toArray()) {
            tmp.add(((AudioTrack) o).getInfo());
        }

        return tmp;
    }

    public boolean setRepeat() {
        this.repeat = !this.repeat;
        return repeat;
    }

    public Optional<AudioTrack> remove(final int num) {
        int i = 1;
        Iterator<AudioTrack> it = new ArrayList<>(queue).iterator();
        while (it.hasNext()) {
            if (i == num) {
                AudioTrack t = it.next();
                queue.remove(t);
                return Optional.of(t);
            }
            it.next();
            i++;
        }
        return Optional.empty();
    }

    public Optional<AudioTrack> remove(final String keyWord) {
        Iterator<AudioTrack> it = new ArrayList<>(queue).iterator();
        while (it.hasNext()) {
            AudioTrack tr = it.next();
            if (tr.getInfo().title.toLowerCase().contains(keyWord.toLowerCase())) {
                queue.remove(tr);
                return Optional.of(tr);
            }
        }
        return Optional.empty();
    }

    public int size() {
        return queue.size();
    }
}
