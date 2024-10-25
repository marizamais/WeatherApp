package gr.novidea.weatherpay.data

import android.util.Log
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.isManaged
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmObject
import kotlin.reflect.KClass

interface RealmManager<T : RealmObject> {
    val realm: Realm
    val clazz: KClass<T>

    suspend fun getId(id: String): RealmTransaction {
        var returnValue = RealmTransaction()
        realm.write {
            val transactions = query<RealmTransaction>().find()
            // Iterate over each Transaction item and set the unsettled property to settled
            transactions.forEach { transaction ->
                Log.v("ID", transaction.id)
                if (transaction.id == id) {
                    returnValue = transaction
                }
            }
        }
        return returnValue
    }
    suspend fun upsert(entity: T) {
        realm.write {
            copyToRealm(entity)
        }
    }

    suspend fun upsertAll(entities: List<T>) {
        realm.write {
            for (entity in entities) {
                copyToRealm(entity)
            }
        }
    }

    suspend fun findAll(): RealmResults<T> {
        return realm.query(clazz).find()
    }

    suspend fun delete(entity: Message) {
        realm.write {
            // Ensure the object is managed before attempting to delete
            val managedEntity = if (entity.isManaged()) {
                // If the object is managed, ensure it's live (not frozen)
                findLatest(entity)
            } else {
                // If the object is unmanaged, query the realm to get the managed version
                query<Message>("id == $0", entity.id).find().firstOrNull()
            }

            managedEntity?.let {
                // Proceed to delete the live (managed) entity
                delete(it)
            } ?: Log.e("RealmManager", "Unable to delete: Entity not found in Realm")
        }
    }

    suspend fun deleteAll() {
        realm.write {
            val all = this.query(clazz).find()
            delete(all)
        }
    }

    suspend fun updateIndices(list: MutableList<Message>) {
        realm.write {
            list.forEachIndexed { index, item ->
                // Query the object in Realm and ensure it is managed
                val realmItem = query<Message>("id == $0", item.id).find().firstOrNull()

                if (realmItem != null) {
                    val managedItem = if (realmItem.isManaged()) realmItem else copyToRealm(realmItem)
                    managedItem.index = index
                } else {
                    Log.e("RealmManager", "No item found with id: ${item.id}")
                }
            }
        }
    }


    suspend fun updateId(id: String, userInput: String) {
        realm.write {
            val item = query<Message>("id == $0", id).find().first()
            item.string = userInput
        }
    }

    suspend fun updateChecked(id: String, isChecked: Boolean) {
        realm.write {
            val item = query<Message>("id == $0", id).find().first()
            item.checked = isChecked
        }
    }

    suspend fun closeBatch() {
        realm.write {
            val transactions = query<RealmTransaction>().find()

            // Iterate over each Transaction item and set the unsettled property to settled
            transactions.forEach { transaction ->
                transaction.status = "Settled"
            }
        }
    }
}