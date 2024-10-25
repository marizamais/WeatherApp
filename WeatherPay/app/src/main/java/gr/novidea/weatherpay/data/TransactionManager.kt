package gr.novidea.weatherpay.data

import io.realm.kotlin.Realm
import javax.inject.Inject
import kotlin.reflect.KClass

interface ITransaction : RealmManager<RealmTransaction>

class TransactionManager @Inject constructor(
    r: Realm,
) : ITransaction {
    override val realm: Realm = r
    override val clazz: KClass<RealmTransaction> = RealmTransaction::class
}