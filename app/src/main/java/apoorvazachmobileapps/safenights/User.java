package apoorvazachmobileapps.safenights;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Apoorva on 3/31/2017.
 */

public class User {
    @SerializedName("passed")
    @Expose
    private String passed;
    public String getPassed() {
        return passed;
    }
    public void setPassed(String passed) {
        this.passed = passed;
    }
}
