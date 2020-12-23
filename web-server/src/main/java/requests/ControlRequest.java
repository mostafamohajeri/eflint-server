package requests;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

public class ControlRequest {

    @SerializedName(value = "request-type")
    @Getter
    @Setter
    String requestType;

    @SerializedName(value = "data")
    @Getter
    @Setter
    JsonElement data;

    public static EFlintRequest crateKillCommand(String uuid) {
        return
                new Gson().fromJson(String.format("{\n" +
                        "    \"uuid\": \"%s\",\n" +
                        "    \"request-type\": \"command\",\n" +
                        "    \"data\": {\n" +
                        "        \"command\": \"kill\"\n" +
                        "    }\n" +
                        "}",uuid), EFlintRequest.class);
    }
}
