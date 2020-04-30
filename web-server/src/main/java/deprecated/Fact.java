package deprecated;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

public class Fact {
    @Getter @Setter
    @SerializedName(value = dict.FACT_TYPE)
    String type;

    @Getter @Setter
    @SerializedName(value = dict.FACT_VALUE)
    String value;

}
