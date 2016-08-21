package me.rei_m.hbfavmaterial.service.impl

import me.rei_m.hbfavmaterial.network.HatenaApiService
import me.rei_m.hbfavmaterial.service.UserService
import rx.Observable

class UserServiceImpl(private val hatenaApiService: HatenaApiService) : UserService {

    override fun confirmExistingUserId(id: String): Observable<Boolean> {
        return hatenaApiService.userCheck(id).map {
            // 原因はわからないがカンマ等の記号が入っている場合にTopページを取得しているケースがある
            // 基本的には入力時に弾く予定だが、Modelの仕様としては考慮してトップページが返ってきたら
            // 存在しないユーザー = 404として扱う
            return@map !it.contains("<title>はてなブックマーク</title>")
        }
    }
}