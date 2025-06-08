package org.mpris.v2;

import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.types.Variant;
import org.mpris.mpris.DBusProperties;
import org.mpris.mpris.TrackList;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TrackListImpl implements DBusProperties, TrackList {
    @Override
    public List<Map<String, Variant<?>>> GetTracksMetadata(List<DBusPath> TrackIds) {
        return Collections.emptyList();
    }

    @Override
    public void AddTrack(String Uri, DBusPath AfterTrack, boolean SetAsCurrent) {

    }

    @Override
    public void RemoveTrack(DBusPath TrackId) {

    }

    @Override
    public void GoTo(DBusPath TrackId) {

    }

    @Override
    public String getObjectPath() {
        return "";
    }

    @Override
    public Variant<?> Get(String interface_name, String property_name) throws DBusException {
        return new Variant<>("Bitch");
    }

    @Override
    public Map<String, Variant<?>> GetAll(String interface_name) throws DBusException {
        return new TreeMap<String, Variant<?>>() {{
            put("WeeWeeTrackList", new Variant<>("Penis"));
        }};
    }

    @Override
    public void Set(String interface_name, String property_name, Variant<?> value) throws DBusException {

    }
}
