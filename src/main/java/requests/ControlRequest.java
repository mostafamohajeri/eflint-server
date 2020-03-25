package requests;


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
}
