package deprecated;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;


public class TestPresentCommand extends Command {

    public TestPresentCommand() {
        this.command = dict.COMMAND_TEST_PRESENT;
    }

    @Setter
    @Getter
    @SerializedName(value = dict.PARAM)
    Fact value;
}
