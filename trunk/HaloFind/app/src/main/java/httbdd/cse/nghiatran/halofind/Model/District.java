package httbdd.cse.nghiatran.halofind.Model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by TranMinhNghia_512023 on 4/13/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class District {
    @JsonProperty("district")
    String district;

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
