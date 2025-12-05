package org.mpris;

import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.errors.NotSupported;
import org.freedesktop.dbus.errors.PropertyReadOnly;
import org.freedesktop.dbus.errors.UnknownInterface;
import org.freedesktop.dbus.errors.UnknownProperty;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.Properties;
import org.freedesktop.dbus.types.Variant;
import org.mpris.mpris.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

@SuppressWarnings({"unused", "unchecked"})
public class MPRIS implements MediaPlayer2, Player, DBusProperties {
    private final HashMap<String, Object> mediaPlayerValues = new HashMap<>();
    private final HashMap<String, Object> playerValues = new HashMap<>();

    private final List<String> mediaPlayerReadWriteValues = new LinkedList<String>() {{
        add("Fullscreen");
    }};
    private final List<String> playerReadWriteValues = new LinkedList<String>() {{
        add("LoopStatus");
        add("Rate");
        add("Shuffle");
        add("Volume");
    }};

    private final DBusConnection connection;
    private boolean blockWriting = false;
    private final MPRIS self;
    private TrackListImpl trackList = null;
    private PlaylistsImpl playlists = null;

    MPRIS(
            HashMap<String, Object> mediaPlayerProperties,
            HashMap<String, Object> playerProperties,
            String playerName,
            TrackListImpl trackList,
            PlaylistsImpl playlists,
            DBusConnection connection
    ) throws DBusException {
        this.mediaPlayerValues.putAll(mediaPlayerProperties);
        this.playerValues.putAll(playerProperties);

        this.self = this;

        if(((Variant<Double>) this.playerValues.get("Rate")).getValue() < ((Variant<Double>) this.playerValues.get("MinimumRate")).getValue()
                || ((Variant<Double>) this.playerValues.get("Rate")).getValue() > ((Variant<Double>) this.playerValues.get("MaximumRate")).getValue())
            throw new IllegalArgumentException("Rate must be bigger than MinimumRate and less than MaximumRate");

        // If this is false, clients should assume that all properties on this interface are read-only
        // (and will raise errors if writing to them is attempted),
        // no methods are implemented and all other properties starting with "Can" are also false.
        if(!((Variant<Boolean>) this.playerValues.get("CanControl")).getValue()) {
            blockWriting = true;
            this.mediaPlayerValues.put("CanQuit", new Variant<>(false));
            this.mediaPlayerValues.put("CanSetFullscreen", new Variant<>(false));
            this.mediaPlayerValues.put("CanRaise", new Variant<>(false));
            this.playerValues.put("CanGoNext", new Variant<>(false));
            this.playerValues.put("CanGoPrevious", new Variant<>(false));
            this.playerValues.put("CanPlay", new Variant<>(false));
            this.playerValues.put("CanPause", new Variant<>(false));
            this.playerValues.put("CanSeek", new Variant<>(false));
        }

        ArrayList<Class<?>> exportedInterfaces = new ArrayList<Class<?>>() {{
            add(DBusProperties.class);
            add(MediaPlayer2.class);
            add(Player.class);
        }};

        if(((Variant<Boolean>) this.mediaPlayerValues.get("HasTrackList")).getValue()) {
            exportedInterfaces.add(TrackList.class);
            this.trackList = trackList;
            this.trackList.setConnection(connection);
        }

        if(playlists != null) {
            exportedInterfaces.add(Playlists.class);
            this.playlists = playlists;
            this.playlists.setConnection(connection);
        }

        this.connection = connection;
        connection.exportObject((MediaPlayer2) Proxy.newProxyInstance(
                self.getClass().getClassLoader(),
                exportedInterfaces.toArray(new Class<?>[exportedInterfaces.size()]),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return intercept(proxy, method, args);
                    }
                }
        ));
        connection.requestBusName("org.mpris.MediaPlayer2." + playerName);
    }

    private Object intercept(Object proxy, Method method, Object[] args) throws Throwable {
        if(trackList != null || playlists != null) {
            switch (method.getName()) {
                case "Get":
                    if(args == null) break;
                    if (args[0].toString().equals(MPRISObjectPaths.TRACKLIST.getPath())) {
                        return trackList.Get(args[0].toString(), args[1].toString());
                    } else if (args[0].toString().equals(MPRISObjectPaths.PLAYLISTS.getPath())) {
                        return playlists.Get(args[0].toString(), args[1].toString());
                    }
                    break;
                case "GetAll":
                    if(args == null) break;
                    if (args[0].toString().equals(MPRISObjectPaths.TRACKLIST.getPath())) {
                        return trackList.GetAll(args[0].toString());
                    } else if (args[0].toString().equals(MPRISObjectPaths.PLAYLISTS.getPath())) {
                        return playlists.GetAll(args[0].toString());
                    }
                    break;
                case "Set":
                    if(args == null) break;
                    if (args[0].toString().equals(MPRISObjectPaths.TRACKLIST.getPath())) {
                        trackList.Set(args[0].toString(), args[1].toString(), (Variant<?>) args[2]);
                        return null;
                    } else if (args[0].toString().equals(MPRISObjectPaths.PLAYLISTS.getPath())) {
                        playlists.Set(args[0].toString(), args[1].toString(), (Variant<?>) args[2]);
                        return null;
                    }
                    break;

                case "GetTracksMetadata":
                    if(trackList == null) throw new DBusException("Player doesn't support TrackList");
                    return trackList.GetTracksMetadata((List<DBusPath>) args[0]);
                case "AddTrack":
                    if(trackList == null) throw new DBusException("Player doesn't support TrackList");
                    trackList.AddTrack((String) args[0], (DBusPath) args[1], (boolean) args[2]);
                    return null;
                case "RemoveTrack":
                    if(trackList == null) throw new DBusException("Player doesn't support TrackList");
                    trackList.RemoveTrack((DBusPath) args[0]);
                    return null;
                case "GoTo":
                    if(trackList == null) throw new DBusException("Player doesn't support TrackList");
                    trackList.GoTo((DBusPath) args[0]);
                    return null;

                case "ActivatePlaylist":
                    if(playlists == null) throw new DBusException("Player doesn't support Playlists");
                    playlists.ActivatePlaylist((DBusPath) args[0]);
                    return null;
                case "GetPlaylists":
                    if(playlists == null) throw new DBusException("Player doesn't support Playlists");
                    return playlists.GetPlaylists((int) args[0], (int) args[1], (String) args[2], (boolean) args[3]);
            }
        }
        return method.invoke(self, args);
    }

    public PlaylistsImpl getPlaylists() {
        return playlists;
    }

    public TrackListImpl getTrackList() {
        return trackList;
    }

    /**
     * If false, calling Quit will have no effect, and may raise a NotSupported error.
     * If true, calling Quit will cause the media application to attempt to quit
     * (although it may still be prevented from quitting by the user, for example).
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Media_Player.html#Property:CanQuit">freedesktop.org</a>
     */
    public void setCanQuit(boolean canQuit) throws DBusException {
        this.mediaPlayerValues.put("CanQuit", new Variant<>(canQuit));
        update("CanQuit", (Variant<?>) mediaPlayerValues.get("CanQuit"), MPRISObjectPaths.MEDIAPLAYER2);
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
    public void setFullscreen(boolean fullscreen) throws DBusException {
        this.mediaPlayerValues.put("Fullscreen", new Variant<>(fullscreen));
        update("Fullscreen", (Variant<?>) mediaPlayerValues.get("Fullscreen"), MPRISObjectPaths.MEDIAPLAYER2);
    }

    /**
     *  If false, attempting to set Fullscreen will have no effect, and may raise an error. If true, attempting to set Fullscreen will not raise an error, and (if it is different from the current value) will cause the media player to attempt to enter or exit fullscreen mode.
     * Note that the media player may be unable to fulfil the request. In this case, the value will not change. If the media player knows in advance that it will not be able to fulfil the request, however, this property should be false.
     <br>
     <br>
     * This allows clients to choose whether to display controls for entering or exiting fullscreen mode.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Media_Player.html#Property:CanSetFullscreen">freedesktop.org</a>
     */
    public void setCanSetFullscreen(boolean canSetFullscreen) throws DBusException {
        this.mediaPlayerValues.put("CanSetFullscreen", new Variant<>(canSetFullscreen));
        update("CanSetFullscreen", (Variant<?>) mediaPlayerValues.get("CanSetFullscreen"), MPRISObjectPaths.MEDIAPLAYER2);
    }

    /**
     * If false, calling Raise will have no effect, and may raise a NotSupported error. If true, calling Raise will cause the media application to attempt to bring its user interface to the front, although it may be prevented from doing so (by the window manager, for example).
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Media_Player.html#Property:CanRaise">freedesktop.org</a>
     */
    public void setCanRaise(boolean canRaise) throws DBusException {
        this.mediaPlayerValues.put("CanRaise", new Variant<>(canRaise));
        update("CanRaise", (Variant<?>) mediaPlayerValues.get("CanRaise"), MPRISObjectPaths.MEDIAPLAYER2);
    }

    /**
     * Indicates whether the /org/mpris/MediaPlayer2 object implements the org.mpris.MediaPlayer2.TrackList interface.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Media_Player.html#Property:HasTrackList">freedesktop.org</a>
     */
    public void setHasTrackList(boolean hasTrackList) throws DBusException {
        this.mediaPlayerValues.put("HasTrackList", new Variant<>(hasTrackList));
        update("HasTrackList", (Variant<?>) mediaPlayerValues.get("HasTrackList"), MPRISObjectPaths.MEDIAPLAYER2);
    }

    public void setTrackList(TrackListImpl trackList) {
        this.trackList = trackList;
    }

    /**
     * A friendly name to identify the media player to users.
     * This should usually match the name found in .desktop files
     * (eg: "VLC media player").
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Media_Player.html#Property:Identity">freedesktop.org</a>
     */
    public void setIdentity(String identity) throws DBusException {
        this.mediaPlayerValues.put("Identity", new Variant<>(identity));
        update("Identity", (Variant<?>) mediaPlayerValues.get("Identity"), MPRISObjectPaths.MEDIAPLAYER2);
    }

    /**
     * The basename of an installed .desktop file which complies with the Desktop entry specification, with the ".desktop" extension stripped.
     * Example: The desktop entry file is "/usr/share/applications/vlc.desktop", and this property contains "vlc"
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Media_Player.html#Property:DesktopEntry">freedesktop.org</a>
     */
    public void setDesktopEntry(String desktopEntry) throws DBusException {
        this.mediaPlayerValues.put("DesktopEntry", new Variant<>(desktopEntry));
        update("DesktopEntry", (Variant<?>) mediaPlayerValues.get("DesktopEntry"), MPRISObjectPaths.MEDIAPLAYER2);
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
    public void setSupportedUriSchemes(String... supportedUriSchemes) throws DBusException {
        this.mediaPlayerValues.put("SupportedUriSchemes", new Variant<>(supportedUriSchemes, "as"));
        update("SupportedUriSchemes", (Variant<?>) mediaPlayerValues.get("SupportedUriSchemes"), MPRISObjectPaths.MEDIAPLAYER2);
    }

    /**
     * The mime-types supported by the media player.
     * Mime-types should be in the standard format (eg: audio/mpeg or application/ogg).
     <br>
     <br>
     * This is important for clients to know when using the editing capabilities of the Playlist interface, for example.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Media_Player.html#Property:SupportedMimeTypes">freedesktop.org</a>
     */
    public void setSupportedMimeTypes(String... supportedMimeTypes) throws DBusException {
        this.mediaPlayerValues.put("SupportedMimeTypes", new Variant<>(supportedMimeTypes, "as"));
        update("SupportedMimeTypes", (Variant<?>) mediaPlayerValues.get("SupportedMimeTypes"), MPRISObjectPaths.MEDIAPLAYER2);
    }

    /**
     * The current playback status.
     * May be "Playing", "Paused" or "Stopped".
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Property:PlaybackStatus">freedesktop.org</a>
     */
    public void setPlaybackStatus(PlaybackStatus playbackStatus) throws DBusException {
        this.playerValues.put("PlaybackStatus", new Variant<>(playbackStatus.GetAsString()));
        update("PlaybackStatus", new Variant<>(playbackStatus.GetAsString()), MPRISObjectPaths.PLAYER);
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
    public void setLoopStatus(LoopStatus loopStatus) throws DBusException {
        this.playerValues.put("LoopStatus", new Variant<>(loopStatus.GetAsString()));
        update("LoopStatus", new Variant<>(loopStatus.GetAsString()), MPRISObjectPaths.PLAYER);
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
    public void setRate(double rate) throws DBusException {
        this.playerValues.put("Rate", new Variant<>(rate));
        update("Rate", new Variant<>(rate), MPRISObjectPaths.PLAYER);
    }

    /**
     * A value of false indicates that playback is progressing linearly through a playlist, while true means playback is progressing through a playlist in some other order.
     * If CanControl is false, attempting to set this property should have no effect and raise an error.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Property:Shuffle">freedesktop.org</a>
     */
    public void setShuffle(boolean shuffle) throws DBusException {
        this.playerValues.put("Shuffle", new Variant<>(shuffle));
        update("Shuffle", new Variant<>(shuffle), MPRISObjectPaths.PLAYER);
    }

    /**
     * The metadata of the current element.
     * If there is a current track, this must have a "mpris:trackid" entry (of D-Bus type "o") at the very least, which contains a D-Bus path that uniquely identifies this track.
     * See the type documentation for more details.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Property:Metadata">freedesktop.org</a>
     */
    public void setMetadata(Metadata metadata) throws DBusException {
        this.playerValues.put("Metadata", new Variant<>(metadata.getInternalMap(), "a{sv}"));
        update("Metadata", new Variant<>(metadata.getInternalMap(), "a{sv}"), MPRISObjectPaths.PLAYER);
    }

    /**
     * The volume level.
     * When setting, if a negative value is passed, the volume should be set to 0.0.
     * If CanControl is false, attempting to set this property should have no effect and raise an error.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Property:Volume">freedesktop.org</a>
     */
    public void setVolume(double volume) throws DBusException {
        this.playerValues.put("Volume", new Variant<>(volume));
        update("Volume", new Variant<>(volume), MPRISObjectPaths.PLAYER);
    }

    /**
     * The current track position in microseconds, between 0 and the 'mpris:length' metadata entry (see Metadata).
     * Note: If the media player allows it, the current playback position can be changed either the SetPosition method or the Seek method on this interface. If this is not the case, the CanSeek property is false, and setting this property has no effect and can raise an error.
     * If the playback progresses in a way that is inconstistant with the Rate property, the Seeked signal is emited.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Property:Position">freedesktop.org</a>
     */
    public void setPosition(int microseconds) {
        this.playerValues.put("Position", new Variant<>(microseconds, "x"));
    }

    /**
     * The minimum value which the Rate property can take. Clients should not attempt to set the Rate property below this value.
     * Note that even if this value is 0.0 or negative, clients should not attempt to set the Rate property to 0.0.
     * This value should always be 1.0 or less.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Property:MinimumRate">freedesktop.org</a>
     */
    public void setMinimumRate(double minimumRate) throws DBusException {
        // This value should always be 1.0 or less.
        if(minimumRate > 1.0) minimumRate = 1.0;
        this.playerValues.put("MinimumRate", new Variant<>(minimumRate));
        update("MinimumRate", new Variant<>(minimumRate), MPRISObjectPaths.PLAYER);
    }

    /**
     * The maximum value which the Rate property can take. Clients should not attempt to set the Rate property above this value.
     * This value should always be 1.0 or greater.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html#Property:MaximumRate">freedesktop.org</a>
     */
    public void setMaximumRate(double maximumRate) throws DBusException {
        // This value should always be 1.0 or greater.
        if(maximumRate < 1.0) maximumRate = 1.0;
        this.playerValues.put("MaximumRate", new Variant<>(maximumRate));
        update("MaximumRate", new Variant<>(maximumRate), MPRISObjectPaths.PLAYER);
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
    public void setCanGoNext(boolean canGoNext) throws DBusException {
        this.playerValues.put("CanGoNext", new Variant<>(canGoNext));
        update("PlaybackStatus", new Variant<>(canGoNext), MPRISObjectPaths.PLAYER);
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
    public void setCanGoPrevious(boolean canGoPrevious) throws DBusException {
        this.playerValues.put("CanGoPrevious", new Variant<>(canGoPrevious));
        update("CanGoPrevious", new Variant<>(canGoPrevious), MPRISObjectPaths.PLAYER);
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
    public void setCanPlay(boolean canPlay) throws DBusException {
        this.playerValues.put("CanPlay", new Variant<>(canPlay));
        update("CanPlay", new Variant<>(canPlay), MPRISObjectPaths.PLAYER);
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
    public void setCanPause(boolean canPause) throws DBusException {
        this.playerValues.put("CanPause", new Variant<>(canPause));
        update("CanPause", new Variant<>(canPause), MPRISObjectPaths.PLAYER);
    }

    /**
     * Indicates that the track position has changed in a way that is inconsistant with the current playing state.
     * When this signal is not received, clients should assume that:
     *  - When playing, the position progresses according to the rate property.
     *  - When paused, it remains constant.
     * This signal does not need to be emitted when playback starts or when the track changes, unless the track is starting at an unexpected position. An expected position would be the last known one when going from Paused to Playing, and 0 when going from Stopped to Playing.
     */
    public void emitSeeked(int positionInMicroseconds) throws DBusException {
        connection.sendMessage(new Seeked(
                positionInMicroseconds
        ));
    }

    @Override
    public void Raise() {
        // In this case, the CanRaise property is false and this method does nothing.
        if(!((Variant<Boolean>) mediaPlayerValues.get("CanRaise")).getValue())
            // If false, calling Raise will have no effect, and may raise a NotSupported error
            throw new NotSupported("Raise is not supported");
        ((Runnable) mediaPlayerValues.get("OnRaise")).run();
    }

    @Override
    public void Quit() {
        // In this case, the CanQuit property is false and this method does nothing.
        if(!((Variant<Boolean>) mediaPlayerValues.get("CanQuit")).getValue())
            // If false, calling Quit will have no effect, and may raise a NotSupported error
            throw new NotSupported("Quit is not supported");
        ((Runnable) mediaPlayerValues.get("OnQuit")).run();
    }

    @Override
    public void Next() {
        // If CanGoNext is false, attempting to call this method should have no effect.
        if(!((Variant<Boolean>) playerValues.get("CanGoNext")).getValue())
            return;
        ((Runnable) playerValues.get("OnNext")).run();
    }

    @Override
    public void Previous() {
        // If CanGoPrevious is false, attempting to call this method should have no effect.
        if(!((Variant<Boolean>) playerValues.get("CanGoPrevious")).getValue())
            return;
        ((Runnable) playerValues.get("OnPrevious")).run();
    }

    @Override
    public void Pause() {
        // If CanPause is false, attempting to call this method should have no effect.
        if(!((Variant<Boolean>) playerValues.get("CanPause")).getValue())
            return;
        ((Runnable) playerValues.get("OnPause")).run();
    }

    @Override
    public void PlayPause() {
        // If CanPause is false, attempting to call this method should have no effect and raise an error.
        if(!((Variant<Boolean>) playerValues.get("CanPause")).getValue())
            return;
        ((Runnable) playerValues.get("OnPlayPause")).run();
    }

    @Override
    public void Stop() {
        // If CanControl is false, attempting to call this method should have no effect and raise an error.
        if(!((Variant<Boolean>) playerValues.get("CanControl")).getValue())
            return;
        ((Runnable) playerValues.get("OnStop")).run();
    }

    @Override
    public void Play() {
        // If CanPlay is false, attempting to call this method should have no effect.
        if(!((Variant<Boolean>) playerValues.get("CanPlay")).getValue())
            return;
        ((Runnable) playerValues.get("OnPlay")).run();
    }

    @Override
    public void Seek(long x) {
        // If the CanSeek property is false, this has no effect.
        if(!((Variant<Boolean>) playerValues.get("CanSeek")).getValue())
            return;
        ((TypeRunnable<Long>) playerValues.get("OnSeek")).run(x);
    }

    @Override
    public void SetPosition(DBusPath Track_Id, long x) throws DBusException {
        // If the CanSeek property is false, this has no effect.
        if(!((Variant<Boolean>) playerValues.get("CanSeek")).getValue())
            throw new NotSupported("SetPosition is not supported");

        ((TypeRunnable<Position>) playerValues.get("OnSetPosition")).run(new Position(
                Track_Id,
                x
        ));
        emitSeeked((int) x);
    }

    @Override
    public void OpenUri(String Uri) throws DBusException {
        try {
            // If the uri scheme of the uri to open is not supported, this method does nothing and may raise an error.
            if (!((Variant<List<String>>) mediaPlayerValues.get("SupportedUriSchemes")).getValue().contains(Uri.split(":")[0]))
                throw new DBusException("Unsupported uri scheme: " + Uri.split(":")[0]);
            ((TypeRunnable<String>) playerValues.get("OnOpenURI")).run(Uri);
        }catch (ArrayIndexOutOfBoundsException exception) {
            throw new DBusException("Not a valid uri scheme: " + Uri);
        }
    }

    @Override
    public String getObjectPath() {
        return "/org/mpris/MediaPlayer2";
    }

    @Override
    public Variant<?> Get(String interface_name, String property_name) throws DBusException {
        switch (interface_name) {
            case "org.mpris.MediaPlayer2": {
                for(Map.Entry<String, Object> entry : mediaPlayerValues.entrySet()) {
                    if(entry.getKey().equals(property_name)) return (Variant<?>) entry.getValue();
                }
                break;
            }
            case "org.mpris.MediaPlayer2.Player": {
                for(Map.Entry<String, Object> entry : playerValues.entrySet()) {
                    if(entry.getKey().equals(property_name)) return (Variant<?>) entry.getValue();
                }
                break;
            }
            default:
                throw new UnknownInterface(interface_name);
        }
        throw new UnknownProperty(property_name);
    }

    @Override
    public Map<String, Variant<?>> GetAll(String interface_name) throws DBusException {
        Map<String, Variant<?>> result = new HashMap<>();
        switch (interface_name) {
            case "org.mpris.MediaPlayer2": {
                for(Map.Entry<String, Object> entry : mediaPlayerValues.entrySet()) {
                    if(entry.getValue() instanceof Variant) {
                        result.put(entry.getKey(), (Variant<?>) entry.getValue());
                    }
                }
                break;
            }
            case "org.mpris.MediaPlayer2.Player": {
                for(Map.Entry<String, Object> entry : playerValues.entrySet()) {
                    if(entry.getValue() instanceof Variant) {
                        result.put(entry.getKey(), (Variant<?>) entry.getValue());
                    }
                }
                break;
            }
            default:
                throw new UnknownInterface(interface_name);
        }
        return result;
    }

    @Override
    public void Set(String interface_name, String property_name, Variant<?> value) throws DBusException {
        switch (interface_name) {
            case "org.mpris.MediaPlayer2": {
                if (!playerValues.containsKey(property_name))
                    throw new DBusException("Property " + property_name + " not found");

                if(!Objects.equals(((Variant<?>) mediaPlayerValues.get(property_name)).getSig(), value.getSig()))
                    throw new DBusException("Variant has an invalid type: " + value.getSig());

                if (!mediaPlayerReadWriteValues.contains(property_name))
                    throw new PropertyReadOnly(property_name + " is read-only");

                if (!((Variant<Boolean>) this.playerValues.get("CanSetFullscreen")).getValue())
                    throw new NotSupported("Loop status is not supported");

                // Fullscreen is the only read/write property
                setFullscreen((Boolean) value.getValue());

                if(mediaPlayerValues.containsKey("OnFullscreen")) {
                    ((TypeRunnable<Boolean>) mediaPlayerValues.get("OnFullscreen")).run((Boolean) value.getValue());
                }
                break;
            }
            case "org.mpris.MediaPlayer2.Player": {
                // If CanControl is false, all properties are read-only
                if(blockWriting) throw new PropertyReadOnly("CanControl blocked writing to all properties");

                if(!playerValues.containsKey(property_name))
                    throw new DBusException("Property " + property_name + " not found");

                if (!playerReadWriteValues.contains(property_name))
                    throw new PropertyReadOnly(property_name + " is read-only");

                if(!Objects.equals(((Variant<?>) playerValues.get(property_name)).getSig(), value.getSig()))
                    throw new DBusException("Variant has an invalid type: " + value.getSig());

                switch (property_name) {
                    case "LoopStatus":
                        if (!((Variant<Boolean>) this.playerValues.get("CanControl")).getValue())
                            throw new NotSupported("Loop status is not supported");

                        setLoopStatus(LoopStatus.valueOf((String) value.getValue()));
                        if(playerValues.containsKey("OnLoopStatus")) {
                            ((TypeRunnable<LoopStatus>) playerValues.get("OnLoopStatus")).run(LoopStatus.valueOf((String) value.getValue()));
                        }
                        break;
                    case "Rate":
                        double rate = (Double) value.getValue();

                        // The value must fall in the range described by MinimumRate and MaximumRate, and must not be 0.0
                        if(rate == 0.0) {
                            // A value of 0.0 should not be set by the client. If it is, the media player should act as though Pause was called.
                            ((Runnable) this.playerValues.get("OnPause")).run();
                            break;
                        }
                        if(rate < ((Variant<Double>) this.playerValues.get("MinimumRate")).getValue()
                                || rate > ((Variant<Double>) this.playerValues.get("MaximumRate")).getValue())
                            break;

                        setRate(rate);
                        if(playerValues.containsKey("OnRate")) {
                            ((TypeRunnable<Double>) playerValues.get("OnRate")).run((Double) value.getValue());
                        }
                        break;
                    case "Shuffle":
                        // If CanControl is false, attempting to set this property should have no effect and raise an error.
                        if(!((Variant<Boolean>) this.playerValues.get("CanControl")).getValue())
                            throw new DBusException("Player doesn't support controlling playback");

                        setShuffle((Boolean) value.getValue());
                        if(playerValues.containsKey("OnShuffle")) {
                            ((TypeRunnable<Boolean>) playerValues.get("OnShuffle")).run((Boolean) value.getValue());
                        }
                        break;
                    case "Volume":
                        double volume = (Double) value.getValue();

                        // When setting, if a negative value is passed, the volume should be set to 0.0.
                        if(volume < 0.0) volume = 0.0;

                        // If CanControl is false, attempting to set this property should have no effect and raise an error.
                        if(!((Variant<Boolean>) this.playerValues.get("CanControl")).getValue())
                            throw new DBusException("Player doesn't support controlling playback");

                        setVolume(volume);
                        if(playerValues.containsKey("OnVolume")) {
                            ((TypeRunnable<Double>) playerValues.get("OnVolume")).run((Double) value.getValue());
                        }
                }
                break;
            }
            default:
                throw new UnknownInterface(interface_name);
        }
        throw new UnknownProperty(property_name);
    }

    private void update(String propName, Variant<?> value, MPRISObjectPaths objectPaths) throws DBusException {
        Map<String, Variant<?>> changedProps = new HashMap<>();
        changedProps.put(propName, value);
        Properties.PropertiesChanged changed = new Properties.PropertiesChanged(
                getObjectPath(),
                objectPaths.getPath(),
                changedProps,
                Collections.emptyList()
        );
        connection.sendMessage(changed);
    }
}
