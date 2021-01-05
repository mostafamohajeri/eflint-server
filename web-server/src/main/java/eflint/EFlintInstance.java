package eflint;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@AllArgsConstructor(staticName = "from")
public class EFlintInstance {


    @Getter
    int port;

    @Getter
    String uuid;


    @Getter
    transient Thread thread;

    @Getter
    @SerializedName("source-file-name")
    String sourceFileName;

    @Getter
    Timestamp timestamp;

}
