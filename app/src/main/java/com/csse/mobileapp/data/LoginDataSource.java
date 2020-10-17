package com.csse.mobileapp.data;

import com.csse.mobileapp.data.model.LoggedInUser;
import com.csse.mobileapp.utilities.DBConnection;
import com.mongodb.client.MongoCursor;

import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.Objects;


/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication

            MongoCursor<Document> result = new DBConnection().GetUserData(username);
            LoggedInUser loggedInUser = null;

            if (result != null) {
                if (result.hasNext()) {
                    Document user = result.next();

                    if (BCrypt.checkpw(password, Objects.requireNonNull(user.get("password")).toString())) {
                        loggedInUser = new LoggedInUser(Objects.requireNonNull(user.get("email")).toString(), Objects.requireNonNull(user.get("name")).toString());
                        return new Result.Success<>(loggedInUser);
                    } else {
                        return new Result.Error("Invalid Password");
                    }


                } else {
                    return new Result.Error("Invalid Email");
                }
            } else {
                return new Result.Error("Connection Failed");
            }



        } catch (Exception e) {
            return new Result.Error("Exception Occurred");
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}