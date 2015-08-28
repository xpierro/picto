# PicTo - Pico biTorrent client

## Goal

Providing a Java-base bittorrent server using latest technologies (maven multi-modules, Spring, Hibernate, Git).

## Technical challenges

Implementing the protocol
File storage
Good interface (à la rutorrent)
Socket communication
Must be fast
Fully implemented in java and modular: it must not be simply a wrapper around a cli client like rutorrent
Use of RxJava for reactive socket, RxNetty
Experimentation with the Chrome Raw Socket API
Fast database (MongoDB)
JS Download in memory ? (à la mega)

## Functionnalities

Server-side client
Should it be a tracker of sort ?
Remote API - Creating a C++/Qt client communicating with the API remotely
Web interface - can user extjs, better to try something new (angular, etc)
Mobile version, either remote control or full client,... or both ?
Multi account, with administration interface
Must be multi-language ready
Provides extensive informations about the download: peer ip, etc, could be useful for some analysis purposes

## Architecture

The software is divided in several parts:
  - Torrent file parser
  - Tracker connection
  - Swarm management
  - Download management
  - Database
  
For the first iteration, which has to produce a simple java client, the database will have to store:
  - list of torrent files
  - location of file

