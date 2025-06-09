package org.mpris;

import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.errors.PropertyReadOnly;
import org.freedesktop.dbus.errors.UnknownInterface;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.Properties;
import org.freedesktop.dbus.types.Variant;
import org.mpris.mpris.DBusProperties;
import org.mpris.mpris.PlaylistOrdering;
import org.mpris.mpris.Playlists;

import java.util.*;

@SuppressWarnings({"unused", "unchecked"})
public class PlaylistsImpl implements DBusProperties, Playlists {
    private DBusConnection connection;
    private final Map<String, Object> values;

    PlaylistsImpl(Map<String, Object> values) {
        this.values = values;
    }

    public static class PlaylistsBuilder {
        private final Map<String, Object> values = new HashMap<String, Object>() {{
            put("PlaylistCount", new Variant<>(0, "u"));
            put("Orderings", new Variant<>(new String[] {PlaylistOrdering.ModifiedDate.GetAsString()}, "as"));
            put("ActivePlaylist", new Variant<>(new Maybe_Playlist(false, new Playlist(new DBusPath("/"), "", "")), "(b(oss))"));
            put("OnActivatePlaylist", new TypeRunnable<DBusPath>() {
                @Override
                public void run(DBusPath value) {
                }
            });
            put("OnGetPlaylists", new ReturnableTypeRunnable<List<Playlist>, GetPlaylists>() {
                @Override
                public List<Playlist> run(GetPlaylists value) {
                    return Collections.emptyList();
                }
            });
        }};

        // Properties

        /**
         * The number of playlists available.
         * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Playlists_Interface.html#Property:PlaylistCount">freedesktop.org</a>
         */
        public PlaylistsBuilder setPlaylistCount(int count) {
            values.put("PlaylistCount", new Variant<>(count, "u"));
            return this;
        }

        /**
         * The available orderings. At least one must be offered.
         <br>
         <br>
         Media players may not have access to all the data required for some orderings. For example, creation times are not available on UNIX filesystems (don't let the ctime fool you!). On the other hand, clients should have some way to get the "most recent" playlists.
         * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Playlists_Interface.html#Property:Orderings">freedesktop.org</a>
         */
        public PlaylistsBuilder setOrderings(PlaylistOrdering... ordering) {
            List<String> orderings = new ArrayList<>();
            for (PlaylistOrdering order : ordering) {
                orderings.add(order.GetAsString());
            }
            values.put("Orderings", new Variant<>(orderings, "as"));
            return this;
        }

        /**
         * The currently-active playlist.
         * If there is no currently-active playlist, the structure's Valid field will be false, and the Playlist details are undefined.
         * Note that this may not have a value even after ActivatePlaylist is called with a valid playlist id as ActivatePlaylist implementations have the option of simply inserting the contents of the playlist into the current tracklist.
         * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Playlists_Interface.html#Property:ActivePlaylist">freedesktop.org</a>
         */
        public PlaylistsBuilder setActivePlaylist(Maybe_Playlist playlist) {
            values.put("ActivePlaylist", new Variant<>(playlist, "(b(oss))"));
            return this;
        }

        // Methods

        /**
         * Starts playing the given playlist.
         * Note that this must be implemented. If the media player does not allow clients to change the playlist, it should not implement this interface at all.
         * It is up to the media player whether this completely replaces the current tracklist, or whether it is merely inserted into the tracklist and the first track starts. For example, if the media player is operating in a "jukebox" mode, it may just append the playlist to the list of upcoming tracks, and skip to the first track in the playlist.
         * @see <a href="@see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Playlists_Interface.html#Property:ActivePlaylist">freedesktop.org</a>">freedesktop.org</a>
         */
        public PlaylistsBuilder setOnActivatePlaylist(TypeRunnable<DBusPath> onActivatePlaylist) {
            values.put("OnActivatePlaylist", onActivatePlaylist);
            return this;
        }

        /**
         * Gets a set of playlists
         */
        public PlaylistsBuilder setOnGetPlaylists(ReturnableTypeRunnable<List<Playlist>, GetPlaylists> onGetPlaylists) {
            values.put("OnGetPlaylists", onGetPlaylists);
            return this;
        }

        /**
         * Builds and instance of PlaylistsImpl
         * @return Built instance of PlaylistsImpl
         */
        public PlaylistsImpl build() {
            return new PlaylistsImpl(values);
        }
    }

    public static class GetPlaylists {
        private int index;
        private int maxCount;
        private PlaylistOrdering ordering;
        private boolean reverseOrder;

        GetPlaylists parse(int Index, int MaxCount, String Order, boolean ReverseOrder) {
            this.index = Index;
            this.maxCount = MaxCount;
            this.ordering = PlaylistOrdering.valueOf(Order);
            this.reverseOrder = ReverseOrder;
            return this;
        }

        public int getIndex() {
            return index;
        }

        public int getMaxCount() {
            return maxCount;
        }

        public PlaylistOrdering getOrdering() {
            return ordering;
        }

        public boolean isReverseOrder() {
            return reverseOrder;
        }
    }

    protected void setConnection(DBusConnection connection) {
        this.connection = connection;
    }

    /**
     * The number of playlists available.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Playlists_Interface.html#Property:PlaylistCount">freedesktop.org</a>
     */
    public void setPlaylistCount(int count) throws DBusException {
        values.put("PlaylistCount", new Variant<>(count, "u"));
        update("PlaylistCount", new Variant<>(count, "u"));
    }

    /**
     * The available orderings. At least one must be offered.
     <br>
     <br>
     Media players may not have access to all the data required for some orderings. For example, creation times are not available on UNIX filesystems (don't let the ctime fool you!). On the other hand, clients should have some way to get the "most recent" playlists.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Playlists_Interface.html#Property:Orderings">freedesktop.org</a>
     */
    public void setOrderings(PlaylistOrdering... ordering) throws DBusException {
        List<String> orderings = new ArrayList<>();
        for (PlaylistOrdering order : ordering) {
            orderings.add(order.GetAsString());
        }
        values.put("Orderings", new Variant<>(orderings, "as"));
        update("Orderings", new Variant<>(orderings, "as"));
    }

    /**
     * The currently-active playlist.
     * If there is no currently-active playlist, the structure's Valid field will be false, and the Playlist details are undefined.
     * Note that this may not have a value even after ActivatePlaylist is called with a valid playlist id as ActivatePlaylist implementations have the option of simply inserting the contents of the playlist into the current tracklist.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Playlists_Interface.html#Property:ActivePlaylist">freedesktop.org</a>
     */
    public void setActivePlaylist(Maybe_Playlist playlist) throws DBusException {
        values.put("ActivePlaylist", new Variant<>(playlist, "(b(oss))"));
        update("ActivatePlaylists", new Variant<>(playlist, "(b(oss))"));
    }

    public void emitPlaylistChanged(PlaylistSignal playlist) {
        connection.sendMessage(playlist);
    }

    @Override
    public void ActivatePlaylist(DBusPath PlaylistId) {
        ((TypeRunnable<DBusPath>) values.get("OnActivatePlaylist")).run(PlaylistId);
    }

    @Override
    public List<Playlist> GetPlaylists(int Index, int MaxCount, String Order, boolean ReverseOrder) {
        return ((ReturnableTypeRunnable<List<Playlist>, GetPlaylists>) values.get("OnGetPlaylists")).run(
                new GetPlaylists().parse(Index, MaxCount, Order, ReverseOrder)
        );
    }

    @Override
    public Variant<?> Get(String interface_name, String property_name) throws DBusException {
        if(!interface_name.equals("org.mpris.MediaPlayer2.Playlists")) throw new UnknownInterface(interface_name);
        switch (property_name) {
            case "PlaylistCount":
                return (Variant<?>) values.get("PlaylistCount");
            case "Ordering":
                return (Variant<?>) values.get("Ordering");
            case "ReverseOrder":
                return (Variant<?>) values.get("ReverseOrder");
        }
        throw new DBusException("Unknown property: " + property_name);
    }

    @Override
    public Map<String, Variant<?>> GetAll(String interface_name) {
        Map<String, Variant<?>> castedValues = new HashMap<>();
        for(Map.Entry<String, Object> s : values.entrySet()) {
            if(s.getValue() instanceof Variant) {
                castedValues.put(s.getKey(), (Variant<?>) s.getValue());
            }
        }
        return castedValues;
    }

    @Override
    public void Set(String interface_name, String property_name, Variant<?> value) {
        throw new PropertyReadOnly(property_name);
    }

    @Override
    public String getObjectPath() {
        return "";
    }

    private void update(String propName, Variant<?> value) throws DBusException {
        Map<String, Variant<?>> changedProps = new HashMap<>();
        changedProps.put(propName, value);
        org.freedesktop.dbus.interfaces.Properties.PropertiesChanged changed = new Properties.PropertiesChanged(
                getObjectPath(),
                MPRISObjectPaths.PLAYLISTS.getPath(),
                changedProps,
                Collections.emptyList()
        );
        connection.sendMessage(changed);
    }
}
