package main.server.command;

import java.io.IOException;

public interface Command {
    void process() throws IOException;
}

