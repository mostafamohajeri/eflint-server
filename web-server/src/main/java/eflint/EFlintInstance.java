package eflint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class EFlintInstance {

    @Setter @Getter
    int port;

    @Setter @Getter
    String uuid;

    @Getter @Setter
    Thread thread;

    @Getter @Setter
    String filename;
}
