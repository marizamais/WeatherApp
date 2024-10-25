package gr.novidea.weatherpay.data

import io.realm.kotlin.Realm
import javax.inject.Inject
import kotlin.reflect.KClass

interface IMessage : RealmManager<Message>

class MessageManager @Inject constructor(
    r: Realm,
) : IMessage {
    override val realm: Realm = r
    override val clazz: KClass<Message> = Message::class
}