package smartdoc.dashboard.testcase

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.junit4.SpringRunner
import smartdoc.dashboard.model.AccountStatus
import smartdoc.dashboard.model.User
import smartdoc.dashboard.util.IDUtil.now

@SpringBootTest
@RunWith(SpringRunner::class)
class UserTest {

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @Test
    fun insertNewUser() {
        val account = "Maple"
        val encryptPassword = smartdoc.dashboard.util.MD5Util.encryptPassword("Maple", account, 1024)
        val user = User(
                id = smartdoc.dashboard.util.IDUtil.id(),
                account = account,
                password = encryptPassword,
                createTime = now(),
                status = AccountStatus.NORMAL,
                teamId = "default"
        )
        mongoTemplate.save(user)
    }

    @Test
    fun delete(){
        val query = Query().addCriteria(Criteria("account").`is`("Maple"))
        val deleteResult = mongoTemplate.remove(query, User::class.java)
        println(deleteResult)
    }
}