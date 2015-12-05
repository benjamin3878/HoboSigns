package course.example.ben.hobosigns;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
/**
 * Created by Kevin on 12/4/2015.
 */
@ParseClassName("HoboSignPost")
public class HoboSignsPost extends ParseObject{

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser value) {
        put("user", value);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint value) {
        put("location", value);
    }

    public ParseFile getImageFile() {
        return getParseFile("image");
    }

    public void setImageFile(ParseFile file) {
        put("image", file);
    }

    public static ParseQuery<HoboSignsPost> getQuery() {
        return ParseQuery.getQuery(HoboSignsPost.class);
    }
}
