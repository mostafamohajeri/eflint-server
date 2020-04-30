package deprecated;

import com.google.gson.Gson;

import java.util.Optional;

public class CommandFactory {

    public static Optional<Command> createCommand(String msg) {
        System.out.println("hello");
        Gson gson = new Gson();

        Command c = gson.fromJson(msg,Command.class);

        switch (c.getCommand()) {
            case dict.COMMAND_ACTION:
                return Optional.of(gson.fromJson(msg,ActionCommand.class));
            case dict.COMMAND_REVERT:
                return Optional.of(gson.fromJson(msg,RevertCommand.class));
            case dict.COMMAND_TEST_PRESENT:
                return Optional.of(gson.fromJson(msg, TestPresentCommand.class));
            default:
                return Optional.empty();

        }
    }

}
