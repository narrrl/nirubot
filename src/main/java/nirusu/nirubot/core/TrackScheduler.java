package nirusu.nirubot.core;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class TrackScheduler extends AudioEventAdapter {
    private static final int FAIL_LIMIT = 3;
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private boolean repeat = false;
    private transient int failCounter = 0;

    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public synchronized void queue(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
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

    public synchronized ArrayList<AudioTrackInfo> getAllTrackInfos() {
        ArrayList<AudioTrackInfo> tmp = new ArrayList<>();

        for (Object o : queue.toArray()) {
            tmp.add(((AudioTrack) o).getInfo());
        }

        return tmp;
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public synchronized void nextTrack() {
        player.startTrack(queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {

        // fail counter to prevent fail loops with repeat
        if (endReason.equals(AudioTrackEndReason.LOAD_FAILED)) {
            failCounter++;

            if (failCounter > FAIL_LIMIT) {
                repeat = false;
            }
        }

        // requeue song if it played normally
        if (!endReason.equals(AudioTrackEndReason.LOAD_FAILED) && repeat) {
            queue(track.makeClone());
        }

        // start next track
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    public boolean setRepeat() {
        this.repeat = !this.repeat;
        return repeat;
    }

	public AudioTrack remove(final int num) {
        int i = 1;
        Iterator<AudioTrack> it = new ArrayList<>(queue).iterator();
        while(it.hasNext()) {
            if (i == num) {
                AudioTrack t = it.next();
                queue.remove(t);
                return t;
            }
            it.next();
            i++;
        }
        return null;
    }
    
    public AudioTrack remove(final String keyWord) {
        Iterator<AudioTrack> it = new ArrayList<>(queue).iterator();
        while (it.hasNext()) {
            AudioTrack tr = it.next();
            if (tr.getInfo().title.toLowerCase().contains(keyWord.toLowerCase())) {
                queue.remove(tr);
                return tr;
            }
        }
        return null;
    }

	public int size() {
		return queue.size();
	}
}
