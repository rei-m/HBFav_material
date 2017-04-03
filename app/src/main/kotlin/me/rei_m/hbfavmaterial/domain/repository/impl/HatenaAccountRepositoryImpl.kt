package me.rei_m.hbfavmaterial.domain.repository.impl

import io.reactivex.Single
import me.rei_m.hbfavmaterial.domain.repository.HatenaAccountRepository
import me.rei_m.hbfavmaterial.exception.NetworkFailureException
import me.rei_m.hbfavmaterial.infra.network.HatenaApiService
import retrofit2.HttpException
import java.net.HttpURLConnection

class HatenaAccountRepositoryImpl(private val hatenaApiService: HatenaApiService) : HatenaAccountRepository {
    override fun contains(userId: String): Single<Boolean> {
        return hatenaApiService.userCheck(userId).map {
            // 原因はわからないがカンマ等の記号が入っている場合にTopページを取得しているケースがある
            // 基本的には入力時に弾く予定だが、Modelの仕様としては考慮してトップページが返ってきたら
            // 存在しないユーザー = 404として扱う
            return@map !it.contains("<title>はてなブックマーク</title>")
        }.onErrorResumeNext {
            return@onErrorResumeNext if (it is HttpException) {
                when (it.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        Single.just(false)
                    }
                    else -> {
                        Single.error(NetworkFailureException())
                    }
                }
            } else {
                Single.error(it)
            }
        }
    }
}
