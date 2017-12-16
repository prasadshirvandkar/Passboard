package com.midsizemango.noteboard.Models

import java.io.Serializable
import java.util.*

/**
 * Created by prasads on 15/12/17.
 */
class Note(note_id: String, note_name: String, note_text: String, note_color: Int, note_user_id: String, note_date_time: Date): Serializable {

    var note_id: String? = note_id
    var note_name: String? = note_name
    var note_text: String? = note_text
    var note_color: Int? = note_color
    var note_user_id: String? = note_user_id
    var note_date_time: Date? = note_date_time

    fun getNoteId(): String?{
        return note_id
    }

    fun getNoteColor(): Int?{
        return note_color
    }

    fun getNoteName(): String?{
        return note_name
    }

    fun getNoteText(): String?{
        return note_text
    }

    fun getNoteUserId(): String?{
        return note_user_id
    }

    fun getNoteDateTime(): Date?{
        return note_date_time;
    }

}