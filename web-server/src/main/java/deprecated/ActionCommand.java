package deprecated;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


public class ActionCommand extends Command {

    public ActionCommand() {
        this.command = dict.COMMAND_ACTION;
    }

    @Setter
    @Getter
    @SerializedName(value = dict.RECIPIENT)
    String recipient;

    @Setter
    @Getter
    @SerializedName(value = dict.ACTOR)
    String actor;

    @Setter
    @Getter
    @SerializedName(value = dict.PARAMS)
    List<Fact> objects;

    @Setter
    @Getter
    @SerializedName(value = dict.ACT_TYPE)
    String actType;
}
