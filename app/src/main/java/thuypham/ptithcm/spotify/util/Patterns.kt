package thuypham.ptithcm.spotify.util

import java.util.regex.Pattern


object Patterns {

    val EMAIL_ADDRESS: Pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    val PASSWORD: Pattern = Pattern.compile("^[a-zA-Z0-9_-]{6,30}\$")

    val USER_NAME: Pattern = Pattern.compile("^[a-z0-9_-]{1,30}\$")

    val PHONE_NUMBER: Pattern = Pattern.compile("^\\+?(?:[0-9] ?){6,14}[0-9]\$|[0-9]{8,14}")

    val URL: Pattern =
        Pattern.compile("^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$")
}