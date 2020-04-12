package thuypham.ptithcm.spotifya

data class Comment(
    var id: String? = null,
    var userID: String? = null,
    var userComment: UserComment? = null,
    var description: String? = null,
    var replyComment: ArrayList<ReplyComment>? = null,
    var tagUsername: String? = null,
    var dayComment: String? = null
)

class ReplyComment(
    var id: String? = null,
    var userID: String? = null,
    var userComment: UserComment? = null,
    var dayComment: String? = null,
    var description: String? = null,
    var tagUsername: String? = null
)

data class UserComment(
    var userID: String? = null,
    var userName: String? = null,
    var profilePhoto: String? = null
)
