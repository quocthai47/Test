package main.server.command;

import java.util.logging.Logger;

public class QuitCommand implements Command {
    private static Logger logger = Logger.getLogger(QuitCommand.class.getName());
    public void process() {
        logger.info("Executing quit command");
    }
}

