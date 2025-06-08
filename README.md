# mpris-java

100% Pure java implementation of MPRIS (MPRIS D-Bus Interface Specification)

### Note: 
#### This library is only almost fully compliant because this library can't optionally implement a method e.g.  if the list of available uri schemes is empty, this method may not be implemented.
Taken from the freedesktop.org site:

The Media Player Remote Interfacing Specification is a standard D-Bus interface which aims to provide a common programmatic API for controlling media players.

It provides a mechanism for discovery, querying and basic playback control of compliant media players, as well as a tracklist interface which is used to add context to the active media item.
