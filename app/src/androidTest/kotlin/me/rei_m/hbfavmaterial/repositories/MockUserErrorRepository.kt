package me.rei_m.hbfavmaterial.repositories

import android.content.Context
import me.rei_m.hbfavmaterial.entities.UserEntity
import rx.Observable

class MockUserErrorRepository : UserRepository() {

    override fun find(context: Context): UserEntity? {
        return UserEntity(id = "MockUser")
    }

    override fun save(context: Context, userEntity: UserEntity) {
        // 何もしない
    }

    override fun delete(context: Context) {
        // 何もしない
    }

    override fun checkId(id: String): Observable<Boolean> {
        return Observable.just(false)
    }
}
