package course.example.ben.hobosigns;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
/**
 * Created by Kevin on 12/4/2015.
 */
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

    public static ParseQuery<HoboSignsPost> getQuery() {
        return ParseQuery.getQuery(HoboSignsPost.class);
    }
}
