package org.mpris;

import org.freedesktop.dbus.DBusPath;

public class Position {
    private DBusPath path;
    private Long position;

    /**
     * @param path
     *
    The currently playing track's identifier.
    <br>
    If this does not match the id of the currently-playing track, the call is ignored as "stale".
    <br>
    /org/mpris/MediaPlayer2/TrackList/NoTrack is not a valid value for this argument.
     * @param position
    Track position in microseconds.
    <br>
    This must be between 0 and <track_length>.
     */
    public Position(DBusPath path, Long position) {
        this.path = path;
        this.position = position;
    }

    /**
     * @return The currently playing track's identifier.
     */
    public DBusPath getPath() {
        return path;
    }

    /**
     * @return Time in microseconds
     */
    public Long getPosition() {
        return position;
    }
}
