package org.mpris;

import org.freedesktop.dbus.exceptions.DBusException;
import org.mpris.v2.MPRIS;
import org.mpris.v2.TypeRunnable;

public class Test {
    public static void main(String[] args) throws DBusException {
        /*new MPRIS.MPRISBuilder()
                .setCanQuit(true)
                .setCanRaise(true)
                .setOnRaise(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Raise");
                    }
                })
                .setOnQuit(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Quit");
                    }
                })
                .setCanControl(true)
                .setCanSetFullscreen(true)
                .setOnFullscreen(new TypeRunnable<Boolean>() {
                    @Override
                    public void run(Boolean value) {
                        System.out.println("Fullscreen changed: " + value);
                    }
                })
                .setIdentity("NTify")
                .setDesktopEntry("wine-extension-doc")
                .build("NTify");*/
    }
}
