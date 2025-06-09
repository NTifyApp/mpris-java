package org.mpris;

import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.errors.PropertyReadOnly;
import org.freedesktop.dbus.errors.UnknownInterface;
import org.freedesktop.dbus.errors.UnknownProperty;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.Properties;
import org.freedesktop.dbus.types.Variant;
import org.mpris.mpris.DBusProperties;
import org.mpris.mpris.TrackList;

import java.util.*;

@SuppressWarnings({"unused", "unchecked"})
public class TrackListImpl implements DBusProperties, TrackList {
    private final Map<String, Object> values;
    private DBusConnection connection;

    TrackListImpl(Map<String, Object> values) {
        this.values = values;
    }

    public static class TrackListBuilder {
        private final Map<String, Object> values = new HashMap<String, Object>() {{
            put("CanEditTracks", new Variant<>(false));
            put("Tracks", new Variant<>(Collections.emptyList(), "ao"));
            put("OnGetTracksMetadata", new ReturnableTypeRunnable<List<Metadata>, List<DBusPath>>() {
                @Override
                public List<Metadata> run(List<DBusPath> value) {
                    return Collections.emptyList();
                }
            });
            put("OnAddTrack", new TypeRunnable<AddTrack>() {
                @Override
                public void run(AddTrack value) {
                }
            });
            put("OnRemoveTrack", new TypeRunnable<DBusPath>() {
                @Override
                public void run(DBusPath value) {
                }
            });
            put("OnGoTo", new TypeRunnable<DBusPath>() {
                @Override
                public void run(DBusPath value) {
                }
            });
        }};

        // Properties

        /**
         * An array which contains the identifier of each track in the tracklist, in order.
         * The org.freedesktop.DBus.Properties.PropertiesChanged signal is emited every time this property changes, but the signal message does not contain the new value. Client implementations should rather rely on the TrackAdded, TrackRemoved and TrackListReplaced signals to keep their representation of the tracklist up to date.
         * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Track_List_Interface.html#Property:Tracks">freedesktop.org</a>
         */
        public TrackListBuilder setTracks(List<DBusPath> tracks) {
            values.put("Tracks", new Variant<>(tracks, "ao"));
            return this;
        }

        /**
         * If false, calling AddTrack or RemoveTrack will have no effect, and may raise a NotSupported error.
         * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Track_List_Interface.html#Property:CanEditTracks">freedesktop.org</a>
         */
        public TrackListBuilder setCanEditTracks(boolean canEditTracks) {
            values.put("CanEditTracks", new Variant<>(canEditTracks));
            return this;
        }

        // Methods

        /**
         * Gets all the metadata available for a set of tracks.
         * Each set of metadata must have a "mpris:trackid" entry at the very least, which contains a string that uniquely identifies this track within the scope of the tracklist.
         * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Track_List_Interface.html#Method:GetTracksMetadata">freedesktop.org</a>
         */
        public TrackListBuilder setOnGetTracksMetadata(ReturnableTypeRunnable<List<Metadata>, List<DBusPath>> OnGetTracksMetadata) {
            values.put("OnGetTracksMetadata", new Variant<>(OnGetTracksMetadata));
            return this;
        }

        /**
         * Adds a URI in the TrackList.
         * If the CanEditTracks property is false, this has no effect.
         * Note: Clients should not assume that the track has been added at the time when this method returns. They should wait for a TrackAdded (or TrackListReplaced) signal.
         * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Track_List_Interface.html#Method:AddTrack">freedesktop.org</a>
         */
        public TrackListBuilder setOnAddTracks(TypeRunnable<AddTrack> OnAddTracks) {
            values.put("OnAddTracks", new Variant<>(OnAddTracks));
            return this;
        }

        /**
         * Removes an item from the TrackList.
         * If the track is not part of this tracklist, this has no effect.
         * If the CanEditTracks property is false, this has no effect.
         * Note: Clients should not assume that the track has been removed at the time when this method returns. They should wait for a TrackRemoved (or TrackListReplaced) signal.
         * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Track_List_Interface.html#Method:RemoveTrack">freedesktop.org</a>
         */
        public TrackListBuilder setOnRemoveTrack(TypeRunnable<DBusPath> OnRemoveTrack) {
            values.put("OnRemoveTrack", new Variant<>(OnRemoveTrack));
            return this;
        }

        /**
         * Skip to the specified TrackId.
         * If the track is not part of this tracklist, this has no effect.
         * If this object is not /org/mpris/MediaPlayer2, the current TrackList's tracks should be replaced with the contents of this TrackList, and the TrackListReplaced signal should be fired from /org/mpris/MediaPlayer2.
         * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Track_List_Interface.html#Method:GoTo">freedesktop.org</a>
         */
        public TrackListBuilder setOnGoTo(TypeRunnable<DBusPath> OnGoTo) {
            values.put("OnGoTo", new Variant<>(OnGoTo));
            return this;
        }

        /**
         * Builds and instance of TrackList
         */
        public TrackListImpl build() {
            return new TrackListImpl(values);
        }
    }

    public static class AddTrack {
        private final String uri;
        private final DBusPath afterTrack;
        private final boolean setAsCurrent;

        /**
         * @param uri The uri of the item to add. Its uri scheme should be an element of the org.mpris.MediaPlayer2.SupportedUriSchemes property and the mime-type should match one of the elements of the org.mpris.MediaPlayer2.SupportedMimeTypes
         * @param afterTrack The identifier of the track after which the new item should be inserted. The path /org/mpris/MediaPlayer2/TrackList/NoTrack indicates that the track should be inserted at the start of the track list.
         * @param setAsCurrent Whether the newly inserted track should be considered as the current track. Setting this to true has the same effect as calling GoTo afterwards.
         */
        public AddTrack(String uri, DBusPath afterTrack, boolean setAsCurrent) {
            this.uri = uri;
            this.afterTrack = afterTrack;
            this.setAsCurrent = setAsCurrent;
        }

        /**
         * The uri of the item to add. Its uri scheme should be an element of the org.mpris.MediaPlayer2.SupportedUriSchemes property and the mime-type should match one of the elements of the org.mpris.MediaPlayer2.SupportedMimeTypes
         */
        public String getUri() {
            return uri;
        }

        /**
         * The identifier of the track after which the new item should be inserted. The path /org/mpris/MediaPlayer2/TrackList/NoTrack indicates that the track should be inserted at the start of the track list.
         */
        public DBusPath getAfterTrack() {
            return afterTrack;
        }

        /**
         * Whether the newly inserted track should be considered as the current track. Setting this to true has the same effect as calling GoTo afterwards.
         */
        public boolean isSetAsCurrent() {
            return setAsCurrent;
        }
    }

    /**
     * An array which contains the identifier of each track in the tracklist, in order.
     * The org.freedesktop.DBus.Properties.PropertiesChanged signal is emited every time this property changes, but the signal message does not contain the new value. Client implementations should rather rely on the TrackAdded, TrackRemoved and TrackListReplaced signals to keep their representation of the tracklist up to date.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Track_List_Interface.html#Property:Tracks">freedesktop.org</a>
     */
    public void setTracks(List<DBusPath> tracks) throws DBusException {
        values.put("Tracks", new Variant<>(tracks, "ao"));
        update("Tracks", null);
    }

    /**
     * If false, calling AddTrack or RemoveTrack will have no effect, and may raise a NotSupported error.
     * @see <a href="https://specifications.freedesktop.org/mpris-spec/latest/Track_List_Interface.html#Property:CanEditTracks">freedesktop.org</a>
     */
    public void setCanEditTracks(boolean canEditTracks) throws DBusException {
        values.put("CanEditTracks", new Variant<>(canEditTracks));
        update("CanEditTracks", new Variant<>(canEditTracks));
    }

    protected void setConnection(DBusConnection connection) {
        this.connection = connection;
    }

    @Override
    public List<Map<String, Variant<?>>> GetTracksMetadata(List<DBusPath> TrackIds) {
        List<Metadata> metadata = ((ReturnableTypeRunnable<List<Metadata>, List<DBusPath>>) values.get("OnGetTracksMetadata")).run(TrackIds);
        List<Map<String, Variant<?>>> convertedMetadata = new ArrayList<>();
        for(Metadata m : metadata) {
            convertedMetadata.add(m.getInternalMap());
        }
        return convertedMetadata;
    }

    @Override
    public void AddTrack(String Uri, DBusPath AfterTrack, boolean SetAsCurrent) {
        if(!((Variant<Boolean>) values.get("CanEditTracks")).getValue())
            return;
        ((TypeRunnable<AddTrack>) values.get("OnAddTrack")).run(new AddTrack(Uri, AfterTrack, SetAsCurrent));
    }

    @Override
    public void RemoveTrack(DBusPath TrackId) {
        if(!((Variant<Boolean>) values.get("CanEditTracks")).getValue())
            return;
        ((TypeRunnable<DBusPath>) values.get("OnRemoveTrack")).run(TrackId);
    }

    @Override
    public void GoTo(DBusPath TrackId) {
        ((TypeRunnable<DBusPath>) values.get("OnGoTo")).run(TrackId);
    }

    @Override
    public String getObjectPath() {
        return "";
    }

    @Override
    public Variant<?> Get(String interface_name, String property_name) throws DBusException {
        if(!interface_name.equals("org.mpris.MediaPlayer2.TrackList")) throw new UnknownInterface(interface_name);
        switch (property_name) {
            case "Tracks":
                return (Variant<?>) values.get("Tracks");
            case "CanEditTracks":
                return (Variant<?>) values.get("CanEditTracks");
        }
        throw new UnknownProperty(property_name);
    }

    @Override
    public Map<String, Variant<?>> GetAll(String interface_name) throws DBusException {
        Map<String, Variant<?>> converted = new HashMap<>();
        for(Map.Entry<String, Object> s : values.entrySet()) {
            if(s.getValue() instanceof Variant) {
                converted.put(s.getKey(), (Variant<?>) s.getValue());
            }
        }
        return converted;
    }

    @Override
    public void Set(String interface_name, String property_name, Variant<?> value) throws DBusException {
        throw new PropertyReadOnly(property_name);
    }

    private void update(String propName, Variant<?> value) throws DBusException {
        Map<String, Variant<?>> changedProps = new HashMap<>();
        changedProps.put(propName, value);
        org.freedesktop.dbus.interfaces.Properties.PropertiesChanged changed = new Properties.PropertiesChanged(
                getObjectPath(),
                MPRISObjectPaths.TRACKLIST.getPath(),
                changedProps,
                Collections.emptyList()
        );
        connection.sendMessage(changed);
    }
}
