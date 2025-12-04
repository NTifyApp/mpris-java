package org.mpris;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.types.Variant;
import org.mpris.mpris.LoopStatus;
import org.mpris.mpris.PlaybackStatus;

import java.util.*;

public class MPRISBuilder {
    private final HashMap<String, Object> mediaPlayerValues = new HashMap<String, Object>() {{
        // Properties
        put("CanQuit", new Variant<>(false));
        put("Fullscreen", new Variant<>(false));
        put("CanSetFullscreen", new Variant<>(false));
        put("CanRaise", new Variant<>(false));
        put("HasTrackList", new Variant<>(false));
        put("Identity", new Variant<>(""));
        put("DesktopEntry", new Variant<>(""));
        put("SupportedUriSchemes", new Variant<>(Collections.emptyList(), "as"));
        put("SupportedMimeTypes", new Variant<>(Collections.emptyMap(), "as"));

        // Methods
        put("onRaise", new Runnable() {
            @Override
            public void run() {
            }
        });
        put("onQuit", new Runnable() {
            @Override
            public void run() {
            }
        });
    }};

    private final HashMap<String, Object> playerValues = new HashMap<String, Object>() {{
        // Properties
        put("PlaybackStatus", new Variant<>(PlaybackStatus.STOPPED.GetAsString()));
        put("LoopStatus", new Variant<>(LoopStatus.NONE.GetAsString()));
        put("Rate", new Variant<>(1.0));
        put("Shuffle", new Variant<>(false));
        put("Volume", new Variant<>(1.0));
        put("Position", new Variant<>(0, "x"));
        put("MinimumRate", new Variant<>(1.0));
        put("MaximumRate", new Variant<>(1.0));
        put("CanGoNext", new Variant<>(false));
        put("CanGoPrevious", new Variant<>(false));
        put("CanPlay", new Variant<>(false));
        put("CanPause", new Variant<>(false));
        put("CanSeek", new Variant<>(false));
        put("CanControl", new Variant<>(false));
        put("Metadata", new Variant<>(new HashMap<String, Variant<?>>(), "a{sv}"));

        // Methods
        put("OnNext", new Runnable() {
            @Override
            public void run() {
            }
        });
        put("OnPrevious", new Runnable() {
            @Override
            public void run() {
            }
        });
        put("OnPause", new Runnable() {
            @Override
            public void run() {
            }
        });
        put("OnPlayPause", new Runnable() {
            @Override
            public void run() {
            }
        });
        put("OnStop", new Runnable() {
            @Override
            public void run() {
            }
        });
        put("OnPlay", new Runnable() {
            @Override
            public void run() {
            }
        });
        put("OnSeek", new TypeRunnable<Integer>() {
            @Override
            public void run(Integer value) {
            }
        });
        put("OnSetPosition", new TypeRunnable<Position>() {
            @Override
            public void run(Position value) {
            }
        });
        put("OnOpenURI", new TypeRunnable<String>() {
            @Override
            public void run(String value) {
            }
        });
    }};
    private TrackListImpl trackList;
    private PlaylistsImpl playlists;
    private final DBusConnection connection;

    public MPRISBuilder() throws DBusException {
        this.connection = DBusConnection.newConnection(DBusConnection.DBusBusType.SESSION);
    }

    /**
     * Adds support for Playlists
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Playlists_Interface.html#Enum:Playlist_Ordering">freedesktop.org</a>
     */
    public MPRISBuilder addPlaylistsSupport(PlaylistsImpl playlists) {
        this.playlists = playlists;
        return this;
    }

    /**
     * Adds support for TrackList
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Track_List_Interface.html">freedesktop.org</a>
     */
    public MPRISBuilder addTrackListSupport(TrackListImpl trackList) {
        this.trackList = trackList;
        setHasTrackList(true);
        return this;
    }

    // Properties

    /**
     * If false, calling Quit will have no effect, and may raise a NotSupported error.
     * If true, calling Quit will cause the media application to attempt to quit
     * (although it may still be prevented from quitting by the user, for example).
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Media_Player.html#Property:CanQuit">freedesktop.org</a>
     */
    public MPRISBuilder setCanQuit(boolean canQuit) {
        this.mediaPlayerValues.put("CanQuit", new Variant<>(canQuit));
        return this;
    }

    /**
     * Whether the media player is occupying the fullscreen.
     * This is typically used for videos. A value of true indicates that the media player is taking up the full screen.
     * Media centre software may well have this value fixed to true
     * If CanSetFullscreen is true, clients may set this property to true to tell the media player to enter fullscreen mode, or to false to return to windowed mode.
     * If CanSetFullscreen is false, then attempting to set this property should have no effect, and may raise an error. However, even if it is true, the media player may still be unable to fulfil the request, in which case attempting to set this property will have no effect (but should not raise an error).
     <br>
     <br>
     This allows remote control interfaces, such as LIRC or mobile devices like phones, to control whether a video is shown in fullscreen.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Media_Player.html#Property:Fullscreen">freedesktop.org</a>
     */
    public MPRISBuilder setFullscreen(boolean fullscreen) {
        this.mediaPlayerValues.put("Fullscreen", new Variant<>(fullscreen));
        return this;
    }

    /**
     *  If false, attempting to set Fullscreen will have no effect, and may raise an error. If true, attempting to set Fullscreen will not raise an error, and (if it is different from the current value) will cause the media player to attempt to enter or exit fullscreen mode.
     * Note that the media player may be unable to fulfil the request. In this case, the value will not change. If the media player knows in advance that it will not be able to fulfil the request, however, this property should be false.
     <br>
     <br>
     * This allows clients to choose whether to display controls for entering or exiting fullscreen mode.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Media_Player.html#Property:CanSetFullscreen">freedesktop.org</a>
     */
    public MPRISBuilder setCanSetFullscreen(boolean canSetFullscreen) {
        this.mediaPlayerValues.put("CanSetFullscreen", new Variant<>(canSetFullscreen));
        return this;
    }

    /**
     * If false, calling Raise will have no effect, and may raise a NotSupported error. If true, calling Raise will cause the media application to attempt to bring its user interface to the front, although it may be prevented from doing so (by the window manager, for example).
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Media_Player.html#Property:CanRaise">freedesktop.org</a>
     */
    public MPRISBuilder setCanRaise(boolean canRaise) {
        this.mediaPlayerValues.put("CanRaise", new Variant<>(canRaise));
        return this;
    }

    /**
     * Indicates whether the /org/mpris/MediaPlayer2 object implements the org.mpris.MediaPlayer2.TrackList interface.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Media_Player.html#Property:HasTrackList">freedesktop.org</a>
     */
    public MPRISBuilder setHasTrackList(boolean hasTrackList) {
        this.mediaPlayerValues.put("HasTrackList", new Variant<>(hasTrackList));
        return this;
    }

    /**
     * A friendly name to identify the media player to users.
     * This should usually match the name found in .desktop files
     * (eg: "VLC media player").
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Media_Player.html#Property:Identity">freedesktop.org</a>
     */
    public MPRISBuilder setIdentity(String identity) {
        this.mediaPlayerValues.put("Identity", new Variant<>(identity));
        return this;
    }

    /**
     * The basename of an installed .desktop file which complies with the Desktop entry specification, with the ".desktop" extension stripped.
     * Example: The desktop entry file is "/usr/share/applications/vlc.desktop", and this property contains "vlc"
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Media_Player.html#Property:DesktopEntry">freedesktop.org</a>
     */
    public MPRISBuilder setDesktopEntry(String desktopEntry) {
        this.mediaPlayerValues.put("DesktopEntry", new Variant<>(desktopEntry));
        return this;
    }

    /**
     * The URI schemes supported by the media player.
     * This can be viewed as protocols supported by the player in almost all cases. Almost every media player will include support for the "file" scheme. Other common schemes are "http" and "rtsp".
     * Note that URI schemes should be lower-case.
     <br>
     <br>
     * This is important for clients to know when using the editing capabilities of the Playlist interface, for example.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Media_Player.html#Property:SupportedUriSchemes">freedesktop.org</a>
     */
    public MPRISBuilder setSupportedUriSchemes(String... supportedUriSchemes) {
        List<String> stringList = new ArrayList<>(Arrays.asList(supportedUriSchemes));
        this.mediaPlayerValues.put("SupportedUriSchemes", new Variant<>(stringList, "as"));
        return this;
    }

    /**
     * The mime-types supported by the media player.
     * Mime-types should be in the standard format (eg: audio/mpeg or application/ogg).
     <br>
     <br>
     * This is important for clients to know when using the editing capabilities of the Playlist interface, for example.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Media_Player.html#Property:SupportedMimeTypes">freedesktop.org</a>
     */
    public MPRISBuilder setSupportedMimeTypes(String... supportedMimeTypes) {
        List<String> stringList = new ArrayList<>(Arrays.asList(supportedMimeTypes));
        this.mediaPlayerValues.put("SupportedMimeTypes", new Variant<>(stringList, "as"));
        return this;
    }

    /**
     * The current playback status.
     * May be "Playing", "Paused" or "Stopped".
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Property:PlaybackStatus">freedesktop.org</a>
     */
    public MPRISBuilder setPlaybackStatus(PlaybackStatus playbackStatus) {
        this.playerValues.put("PlaybackStatus", new Variant<>(playbackStatus.GetAsString()));
        return this;
    }

    /**
     * The current loop / repeat status
     * May be:
     * "None" if the playback will stop when there are no more tracks to play
     * "Track" if the current track will start again from the begining once it has finished playing
     * "Playlist" if the playback loops through a list of tracks
     * If CanControl is false, attempting to set this property should have no effect and raise an error.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Property:LoopStatus">freedesktop.org</a>
     */
    public MPRISBuilder setLoopStatus(LoopStatus loopStatus) {
        this.playerValues.put("LoopStatus", new Variant<>(loopStatus.GetAsString()));
        return this;
    }

    /**
     * The current playback rate.
     * The value must fall in the range described by MinimumRate and MaximumRate, and must not be 0.0. If playback is paused, the PlaybackStatus property should be used to indicate this. A value of 0.0 should not be set by the client. If it is, the media player should act as though Pause was called.
     * If the media player has no ability to play at speeds other than the normal playback rate, this must still be implemented, and must return 1.0. The MinimumRate and MaximumRate properties must also be set to 1.0.
     * Not all values may be accepted by the media player. It is left to media player implementations to decide how to deal with values they cannot use; they may either ignore them or pick a "best fit" value. Clients are recommended to only use sensible fractions or multiples of 1 (eg: 0.5, 0.25, 1.5, 2.0, etc).
     <br>
     <br>
     * This allows clients to display (reasonably) accurate progress bars without having to regularly query the media player for the current position.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Property:Rate">freedesktop.org</a>
     */
    public MPRISBuilder setRate(double rate) {
        this.playerValues.put("Rate", new Variant<>(rate));
        return this;
    }

    /**
     * A value of false indicates that playback is progressing linearly through a playlist, while true means playback is progressing through a playlist in some other order.
     * If CanControl is false, attempting to set this property should have no effect and raise an error.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Property:Shuffle">freedesktop.org</a>
     */
    public MPRISBuilder setShuffle(boolean shuffle) {
        this.playerValues.put("Shuffle", new Variant<>(shuffle));
        return this;
    }

    /**
     * The metadata of the current element.
     * If there is a current track, this must have a "mpris:trackid" entry (of D-Bus type "o") at the very least, which contains a D-Bus path that uniquely identifies this track.
     * See the type documentation for more details.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Property:Metadata">freedesktop.org</a>
     */
    public MPRISBuilder setMetadata(Metadata metadata) {
        this.playerValues.put("Metadata", new Variant<>(metadata.getInternalMap(), "a{sv}"));
        return this;
    }

    /**
     * The volume level.
     * When setting, if a negative value is passed, the volume should be set to 0.0.
     * If CanControl is false, attempting to set this property should have no effect and raise an error.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Property:Volume">freedesktop.org</a>
     */
    public MPRISBuilder setVolume(double volume) {
        // When setting, if a negative value is passed, the volume should be set to 0.0.
        if(volume < 0.0) volume = 0.0;
        this.playerValues.put("Volume", new Variant<>(volume));
        return this;
    }

    /**
     * The current track position in microseconds, between 0 and the 'mpris:length' metadata entry (see Metadata).
     * Note: If the media player allows it, the current playback position can be changed either the SetPosition method or the Seek method on this interface. If this is not the case, the CanSeek property is false, and setting this property has no effect and can raise an error.
     * If the playback progresses in a way that is inconstistant with the Rate property, the Seeked signal is emited.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Property:Position">freedesktop.org</a>
     */
    public MPRISBuilder setPosition(int microseconds) {
        this.playerValues.put("Position", new Variant<>(microseconds, "x"));
        return this;
    }

    /**
     * The minimum value which the Rate property can take. Clients should not attempt to set the Rate property below this value.
     * Note that even if this value is 0.0 or negative, clients should not attempt to set the Rate property to 0.0.
     * This value should always be 1.0 or less.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Property:MinimumRate">freedesktop.org</a>
     */
    public MPRISBuilder setMinimumRate(double minimumRate) {
        // This value should always be 1.0 or less.
        if(minimumRate > 1.0) minimumRate = 1.0;
        this.playerValues.put("MinimumRate", new Variant<>(minimumRate));
        return this;
    }

    /**
     * The maximum value which the Rate property can take. Clients should not attempt to set the Rate property above this value.
     * This value should always be 1.0 or greater.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Property:MaximumRate">freedesktop.org</a>
     */
    public MPRISBuilder setMaximumRate(double maximumRate) {
        // This value should always be 1.0 or greater.
        if(maximumRate < 1.0) maximumRate = 1.0;
        this.playerValues.put("MaximumRate", new Variant<>(maximumRate));
        return this;
    }

    /**
     * Whether the client can call the Next method on this interface and expect the current track to change.
     * If it is unknown whether a call to Next will be successful (for example, when streaming tracks), this property should be set to true.
     * If CanControl is false, this property should also be false.
     <br>
     <br>
     * Even when playback can generally be controlled, there may not always be a next track to move to.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Property:CanGoNext">freedesktop.org</a>
     */
    public MPRISBuilder setCanGoNext(boolean canGoNext) {
        this.playerValues.put("CanGoNext", new Variant<>(canGoNext));
        return this;
    }

    /**
     * Whether the client can call the Previous method on this interface and expect the current track to change.
     * If it is unknown whether a call to Previous will be successful (for example, when streaming tracks), this property should be set to true.
     * If CanControl is false, this property should also be false.
     <br>
     <br>
     * Even when playback can generally be controlled, there may not always be a next previous to move to.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Property:CanGoPrevious">freedesktop.org</a>
     */
    public MPRISBuilder setCanGoPrevious(boolean canGoPrevious) {
        this.playerValues.put("CanGoPrevious", new Variant<>(canGoPrevious));
        return this;
    }

    /**
     * Whether playback can be started using Play or PlayPause.
     * Note that this is related to whether there is a "current track": the value should not depend on whether the track is currently paused or playing. In fact, if a track is currently playing (and CanControl is true), this should be true.
     * If CanControl is false, this property should also be false.
     <br>
     <br>
     * Even when playback can generally be controlled, it may not be possible to enter a "playing" state, for example if there is no "current track".
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Property:CanPlay">freedesktop.org</a>
     */
    public MPRISBuilder setCanPlay(boolean canPlay) {
        this.playerValues.put("CanPlay", new Variant<>(canPlay));
        return this;
    }

    /**
     * Whether playback can be paused using Pause or PlayPause.
     * Note that this is an intrinsic property of the current track: its value should not depend on whether the track is currently paused or playing. In fact, if playback is currently paused (and CanControl is true), this should be true.
     * If CanControl is false, this property should also be false.
     <br>
     <br>
     * Not all media is pausable: it may not be possible to pause some streamed media, for example.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Property:CanPause">freedesktop.org</a>
     */
    public MPRISBuilder setCanPause(boolean canPause) {
        this.playerValues.put("CanPause", new Variant<>(canPause));
        return this;
    }

    /**
     * Whether the client can control the playback position using Seek and SetPosition. This may be different for different tracks.
     * If CanControl is false, this property should also be false.
     * @see <a href="https://specifications.freedesktop.org/mpris/latest/Player_Interface.html#Property:CanSeek">freedesktop.org</a>
     */
    public MPRISBuilder setCanSeek(boolean canSeek) {
        this.playerValues.put("CanSeek", new Variant<>(canSeek));
        return this;
    }

    /**
     * Whether the client can control the playback position using Seek and SetPosition. This may be different for different tracks.
     * If CanControl is false, this property should also be false.
     <br>
     <br>
     * Not all media is seekable: it may not be possible to seek when playing some streamed media, for example.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Property:CanSeek">freedesktop.org</a>
     */
    public MPRISBuilder setCanControl(boolean canControl) {
        this.playerValues.put("CanControl", new Variant<>(canControl));
        return this;
    }

    // Methods

    /**
     * Brings the media player's user interface to the front using any appropriate mechanism available.
     * The media player may be unable to control how its user interface is displayed, or it may not have a graphical user interface at all. In this case, the CanRaise property is false and this method does nothing.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Media_Player.html#Method:Raise">freedesktop.org</a>
     */
    public MPRISBuilder setOnRaise(Runnable onRaise) {
        this.mediaPlayerValues.put("OnRaise", onRaise);
        return this;
    }

    /**
     * Causes the media player to stop running.
     * The media player may refuse to allow clients to shut it down. In this case, the CanQuit property is false and this method does nothing.
     * Note: Media players which can be D-Bus activated, or for which there is no sensibly easy way to terminate a running instance (via the main interface or a notification area icon for example) should allow clients to use this method. Otherwise, it should not be needed.
     * If the media player does not have a UI, this should be implemented.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Media_Player.html#Method:Quit">freedesktop.org</a>
     */
    public MPRISBuilder setOnQuit(Runnable onQuit) {
        this.mediaPlayerValues.put("OnQuit", onQuit);
        return this;
    }

    /**
     * Skips to the next track in the tracklist.
     * If there is no next track (and endless playback and track repeat are both off), stop playback.
     * If playback is paused or stopped, it remains that way.
     * If CanGoNext is false, attempting to call this method should have no effect.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Method:Next">freedesktop.org</a>
     */
    public MPRISBuilder setOnNext(Runnable onNext) {
        this.playerValues.put("OnNext", onNext);
        return this;
    }

    /**
     * Skips to the previous track in the tracklist.
     * If there is no previous track (and endless playback and track repeat are both off), stop playback.
     * If playback is paused or stopped, it remains that way.
     * If CanGoPrevious is false, attempting to call this method should have no effect.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Method:Previous">freedesktop.org</a>
     */
    public MPRISBuilder setOnPrevious(Runnable onPrevious) {
        this.playerValues.put("OnPrevious", onPrevious);
        return this;
    }

    /**
     * Pauses playback.
     * If playback is already paused, this has no effect.
     * Calling Play after this should cause playback to start again from the same position.
     * If CanPause is false, attempting to call this method should have no effect.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Method:Pause">freedesktop.org</a>
     */
    public MPRISBuilder setOnPause(Runnable onPause) {
        this.playerValues.put("OnPause", onPause);
        return this;
    }

    /**
     * Pauses playback.
     * If playback is already paused, resumes playback.
     * If playback is stopped, starts playback.
     * If CanPause is false, attempting to call this method should have no effect and raise an error.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Method:PlayPause">freedesktop.org</a>
     */
    public MPRISBuilder setOnPlayPause(Runnable onPlayPause) {
        this.playerValues.put("OnPlayPause", onPlayPause);
        return this;
    }

    /**
     * Stops playback.
     * If playback is already stopped, this has no effect.
     * Calling Play after this should cause playback to start again from the beginning of the track.
     * If CanControl is false, attempting to call this method should have no effect and raise an error.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Method:Stop">freedesktop.org</a>
     */
    public MPRISBuilder setOnStop(Runnable onStop) {
        this.playerValues.put("OnStop", onStop);
        return this;
    }

    /**
     * Starts or resumes playback.
     * If already playing, this has no effect.
     * If paused, playback resumes from the current position.
     * If there is no track to play, this has no effect.
     * If CanPlay is false, attempting to call this method should have no effect.
     */
    public MPRISBuilder setOnPlay(Runnable onPlay) {
        this.playerValues.put("OnPlay", onPlay);
        return this;
    }

    /**
     * Seeks forward in the current track by the specified number of microseconds.
     * A negative value seeks back. If this would mean seeking back further than the start of the track, the position is set to 0.
     * If the value passed in would mean seeking beyond the end of the track, acts like a call to Next.
     * If the CanSeek property is false, this has no effect.
     * @param onSeek The number of microseconds to seek forward.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Method:Seek">freedesktop.org</a>
     */
    public MPRISBuilder setOnSeek(TypeRunnable<Long> onSeek) {
        this.playerValues.put("OnSeek", onSeek);
        return this;
    }

    /**
     * Sets the current track position in microseconds.
     * If the Position argument is less than 0, do nothing.
     * If the Position argument is greater than the track length, do nothing.
     * If the CanSeek property is false, this has no effect.
     <br>
     <br>
     * The reason for having this method, rather than making Position writable, is to include the TrackId argument to avoid race conditions where a client tries to seek to a position when the track has already changed.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Method:SetPosition">freedesktop.org</a>
     */
    public MPRISBuilder setOnSetPosition(TypeRunnable<Position> onSetPosition) {
        this.playerValues.put("OnSetPosition", onSetPosition);
        return this;
    }

    /**
     * Called when the Fullscreen property changes
     */
    public MPRISBuilder setOnFullscreen(TypeRunnable<Boolean> onFullscreen) {
        this.mediaPlayerValues.put("OnFullscreen", onFullscreen);
        return this;
    }

    /**
     * Called when the LoopStatus property changes
     */
    public MPRISBuilder setOnLoopStatus(TypeRunnable<LoopStatus> onLoopStatus) {
        this.playerValues.put("OnLoopStatus", onLoopStatus);
        return this;
    }

    /**
     * Called when the Rate property changes
     */
    public MPRISBuilder setOnRate(TypeRunnable<Double> onRate) {
        this.playerValues.put("OnRate", onRate);
        return this;
    }

    /**
     * Called when the Shuffle property changes
     */
    public MPRISBuilder setOnShuffle(TypeRunnable<Boolean> onShuffle) {
        this.playerValues.put("OnShuffle", onShuffle);
        return this;
    }

    /**
     * Called when the Volume property changes
     */
    public MPRISBuilder setOnVolume(TypeRunnable<Double> onVolume) {
        this.playerValues.put("OnVolume", onVolume);
        return this;
    }

    /**
     * Opens the Uri given as an argument
     * If the playback is stopped, starts playing
     * If the uri scheme or the mime-type of the uri to open is not supported, this method does nothing and may raise an error. In particular, if the list of available uri schemes is empty, this method may not be implemented.
     * Clients should not assume that the Uri has been opened as soon as this method returns. They should wait until the mpris:trackid field in the Metadata property changes.
     * If the media player implements the TrackList interface, then the opened track should be made part of the tracklist, the org.mpris.MediaPlayer2.TrackList.TrackAdded or org.mpris.MediaPlayer2.TrackList.TrackListReplaced signal should be fired, as well as the org.freedesktop.DBus.Properties.PropertiesChanged signal on the tracklist interface.
     * @param onOpenURI Uri of the track to load. Its uri scheme should be an element of the org.mpris.MediaPlayer2.SupportedUriSchemes property and the mime-type should match one of the elements of the org.mpris.MediaPlayer2.SupportedMimeTypes.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Method:OpenUri">freedesktop.org</a>
     */
    public MPRISBuilder setOnOpenURI(TypeRunnable<String> onOpenURI) {
        this.playerValues.put("OnOpenURI", onOpenURI);
        return this;
    }

    /**
     *
     * @param playerName The name of the player e.g., NTify or Spotify
     * @return Instance of MPRIS
     * @throws DBusException
     */
    public MPRIS build(String playerName) throws DBusException {
        return new MPRIS(mediaPlayerValues, playerValues, playerName, trackList, playlists, this.connection);
    }
}