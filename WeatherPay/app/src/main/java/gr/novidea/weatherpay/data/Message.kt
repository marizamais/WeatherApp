package gr.novidea.weatherpay.data

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.UUID

class Message(): RealmObject {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var string: String = ""
    var checked: Boolean = false
    var index: Int = 0


    constructor(string: String, checked: Boolean, index: Int) : this(){
        this.string = string
        this.checked = checked
        this.index = index
    }
}
