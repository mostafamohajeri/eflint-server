package deprecated;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;


public class RevertCommand extends Command {

    public RevertCommand() {
        this.command = dict.COMMAND_REVERT;
    }

    @Setter
    @Getter
    @SerializedName(value = dict.PARAM)
    Integer value;
}
