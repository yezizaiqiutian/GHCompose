package com.gh.ghcompose

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


//副作用               	触发时机	        主要用途	            是否协程	需要清理
//LaunchedEffect	    key 变化时	    异步操作、动画	        ✅ 是	❌ 否（自动取消）
//SideEffect	        每次重组	        与非 Compose 代码同步	❌ 否	❌ 否
//DisposableEffect	    key 变化时	    需要清理的资源	        ❌ 否	✅ 是（手动清理）
//produceState	        key 变化时	    转换非 Compose 状态	✅ 是	❌ 否（自动清理）
//derivedStateOf	    输入变化时	    计算派生值	        ❌ 否	❌ 否
//snapshotFlow	        State 变化时	    State → Flow 转换	✅ 是	✅ 是（需要收集）

@Composable
fun ProduceStateExample(userId: String) {

    val repository by lazy { UserRepository() }

    val userState by produceState<UserState>(
        initialValue = UserState.Loading,
        key1 = userId
    ) {
        val userFlow = repository.getUserFlow(userId)
        userFlow.collect { user ->
            value = UserState.Success(user)
        }
//        onDispose {
//            println("停止收集用户数据")
//        }
    }
    when (userState) {
        is UserState.Loading -> CircularProgressIndicator()
        is UserState.Success -> {}//UserProfile(user = (userState as UserState.Success).user)
        is UserState.Error -> Text("加载失败")
    }

}





data class User(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String? = null,
    val age: Int? = null,
    val bio: String? = null
)

// UserState.kt - 密封类表示状态
sealed class UserState {
    object Loading : UserState()
    data class Success(val user: User) : UserState()
    data class Error(val message: String, val retry: () -> Unit = {}) : UserState()
}

class UserRepository {
    // 模拟的 Flow 数据源
    fun getUserFlow(userId: String): Flow<User> = flow {
        // 模拟网络延迟
        delay(1000)

        // 模拟数据
        val user = when (userId) {
            "1" -> User(
                id = "1",
                name = "张三",
                email = "zhangsan@example.com",
                avatarUrl = "https://example.com/avatar1.jpg",
                age = 28,
                bio = "软件工程师"
            )

            "2" -> User(
                id = "2",
                name = "李四",
                email = "lisi@example.com",
                avatarUrl = "https://example.com/avatar2.jpg",
                age = 32,
                bio = "产品经理"
            )

            else -> throw IllegalArgumentException("用户不存在")
        }

        emit(user)
    }.flowOn(Dispatchers.IO)

    // 获取用户数据（一次性）
    suspend fun getUser(userId: String): User {
        delay(800)
        return getUserFlow(userId).first()
    }

    // 模拟更新用户
    fun updateUserFlow(userId: String, updates: Map<String, Any>): Flow<User> = flow {
        delay(500)
        val currentUser = getUserFlow(userId).first()
        val updatedUser = currentUser.copy(
            name = updates["name"] as? String ?: currentUser.name,
            bio = updates["bio"] as? String ?: currentUser.bio
        )
        emit(updatedUser)
    }
}