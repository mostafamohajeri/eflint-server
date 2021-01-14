package eflint;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

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
    @SerializedName("flint-search-paths")
    List<String> searchPaths;

    @Getter
    Timestamp timestamp;

}
