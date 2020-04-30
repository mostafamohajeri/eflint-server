package requests;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class CreateEFlintInstanceRequest {
    @Getter @Setter @SerializedName("template-name")
    String modelName;
    @Getter @Setter @SerializedName("values")
    Map<String,String> values;
}
