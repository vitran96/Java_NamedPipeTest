﻿# Cross platform (Win-Linux) named pipe on Java

This is a test to do cross-platform named pipe transaction on both Windows and Linux.
The code here are mainly for experiencing so it is not really clean.

## Named pipe on Windows
Since it is not possible to create named pipe, I have to to use windows api to create it.

    CreateNamedPipe(...)

Named pipe on Windows is placed at a special location. So when creating named pipe file, the name must be change to this format:

    \\.\pipe\<Pipe file name>

As soon as the named pipe is created, most of the trouble is done since java can connect to named pipe with <code>RandomFileAccess</code>
However, if you want to keep the code consistent (like using windows api for everything related to named pipe on windows), you can use <code>CreateFile(...)</code>

## Named pipe on Linux
Currently I cannot find a way to use linux api on java so I execute Linux command to create named pipe <code>mkfifo &lt;Pipe file name&gt;</code>. After that, the named pipe file will be created at the specified given path (or at the current working directory if only the name was stated). The file will remain and will not be delete unless we want to delete it.

Named pipe on Linux (or FIFO) does not have server-client (bidirectional with blocking) named pipe like client, it is unidirectional. Because of that, it can be hard to read-write on the same.

The solution I used for named pipe on Linux is having 2 named pipe file to handle 2-way transaction (1 file to read only and 1 file to write only).
