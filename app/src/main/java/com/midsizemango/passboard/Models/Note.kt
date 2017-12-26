package com.midsizemango.noteboard.Models

import java.io.Serializable
import java.util.*

/**
 * Created by prasads on 15/12/17.
 */
class Note: Serializable {

    var note_id: String? = null
    var note_name: String? = null
    var note_text: String? = null
    var note_color: Int? = null
    var note_user_id: String? = null
    var note_date_time: Date? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Note

        if (note_id != other.note_id) return false
        if (note_name != other.note_name) return false
        if (note_text != other.note_text) return false
        if (note_color != other.note_color) return false
        if (note_user_id != other.note_user_id) return false
        if (note_date_time != other.note_date_time) return false

        return true
    }

    override fun hashCode(): Int {
        var result = note_id?.hashCode() ?: 0
        result = 31 * result + (note_name?.hashCode() ?: 0)
        result = 31 * result + (note_text?.hashCode() ?: 0)
        result = 31 * result + (note_color ?: 0)
        result = 31 * result + (note_user_id?.hashCode() ?: 0)
        result = 31 * result + (note_date_time?.hashCode() ?: 0)
        return result
    }


}