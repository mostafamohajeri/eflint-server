package requests;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

public class CreateEFlintInstanceRequest {
    @Getter @Setter @SerializedName(value = "model-name")
    String modelName;
}
