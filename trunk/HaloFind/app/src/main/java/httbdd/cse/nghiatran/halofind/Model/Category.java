package httbdd.cse.nghiatran.halofind.Model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by TranMinhNghia_512023 on 4/13/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Category {
    @JsonProperty("image")
    String image;
    @JsonProperty("title")
    String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
