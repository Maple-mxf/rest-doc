db.createUser(
        {
            user: "front_biz",
            pwd: "qmbxfrontbizStorage",
            roles: [
                {
                    role: "readWrite",
                    db: "non_standard"
                }
            ]
        }
);
db.getCollection("restdoc_user").insert( {
    _id: "768659280285929472",
    account: "Maple",
    password: "fc0e0ddf1f6a4f4d26750ca8f797a4e0",
    createTime: NumberLong("1603333053419"),
    status: "NORMAL",
    teamId: "default",
    _class: "restdoc.web.model.User"
} );