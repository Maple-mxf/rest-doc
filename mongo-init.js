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