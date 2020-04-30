package deprecated;


import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Command {
    @SerializedName(value = dict.COMMAND)
    String command;
}
