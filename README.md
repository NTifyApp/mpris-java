# mpris-java

100% Pure java implementation of MPRIS (MPRIS D-Bus Interface Specification)
[Go To Spec](https://specifications.freedesktop.org/mpris/)

## Description
Taken from the freedesktop.org site:

The Media Player Remote Interfacing Specification is a standard D-Bus interface which aims to provide a common programmatic API for controlling media players.

It provides a mechanism for discovery, querying and basic playback control of compliant media players, as well as a tracklist interface which is used to add context to the active media item.

## Example

### Basic Player

Taken from [NTify](https://github.com/NTifyApp/NTify)
```java
new MPRISBuilder()
    // Handle MPRIS Quit command
    .setOnQuit(new Runnable() {
        @Override
        public void run() {
            System.exit(0);
        }
    })
    // Handle MPRIS Raise command (bring the app to the foreground)
    .setOnRaise(new Runnable() {
        @Override
        public void run() {
            ContentPanel.frame.toFront();
        }
    })
    // Specify supported URI schemes (e.g., "spotify")
    .setSupportedUriSchemes("spotify")
    // Indicates that the application can be quit via MPRIS
    .setCanQuit(true)
    // Indicates that the application can be raised (brought to foreground) via MPRIS
    .setCanRaise(true)
    // Set the display name of the player
    .setIdentity(ApplicationUtils.getName())
    // Path to the .desktop file for the player (used for icon and identification)
    .setDesktopEntry(ApplicationUtils.getName())
    // Handle open URI requests for the supported schemes
    .setOnOpenURI(new TypeRunnable<String>() {
        @Override
        public void run(String value) {
            if (value.split(":").length == 3) {
                InstanceManager.getSpotifyPlayer()
                    .load(value, true, PublicValues.shuffle);
            }
        }
    })
    // Indicates that the player supports seeking
    .setCanSeek(true)
    // Indicates that the player can be controlled via MPRIS
    .setCanControl(true)
    // Indicates that playback can be started via MPRIS
    .setCanPlay(true)
    // Indicates that playback can be paused via MPRIS
    .setCanPause(true)
    // Indicates that the player can skip to the next track via MPRIS
    .setCanGoNext(true)
    // Indicates that the player can skip to the previous track via MPRIS
    .setCanGoPrevious(true)
    // Handle MPRIS seek command (adjust current playback position)
    .setOnSeek(new TypeRunnable<Long>() {
        @Override
        public void run(Long value) {
            InstanceManager.getPlayer().getPlayer().seek(
                (int) (InstanceManager.getPlayer().getPlayer().time()
                    + TimeUnit.MICROSECONDS.toMillis(value))
            );
        }
    })
    // Handle MPRIS Play/Pause toggle
    .setOnPlayPause(new Runnable() {
        @Override
        public void run() {
            InstanceManager.getPlayer().getPlayer().playPause();
        }
    })
    // Handle MPRIS Play command
    .setOnPlay(new Runnable() {
        @Override
        public void run() {
            InstanceManager.getPlayer().getPlayer().play();
        }
    })
    // Handle MPRIS Pause command
    .setOnPause(new Runnable() {
        @Override
        public void run() {
            InstanceManager.getPlayer().getPlayer().pause();
        }
    })
    // Handle MPRIS Next Track command
    .setOnNext(new Runnable() {
        @Override
        public void run() {
            InstanceManager.getPlayer().getPlayer().next();
        }
    })
    // Handle MPRIS Previous Track command
    .setOnPrevious(new Runnable() {
        @Override
        public void run() {
            InstanceManager.getPlayer().getPlayer().previous();
        }
    })
    // Handle MPRIS SetPosition command (set track position in microseconds)
    .setOnSetPosition(new TypeRunnable<Position>() {
        @Override
        public void run(Position value) {
            InstanceManager.getPlayer().getPlayer()
                .seek((int) TimeUnit.MICROSECONDS.toMillis(value.getPosition()));
        }
    })
    .build(ApplicationUtils.getName());
```