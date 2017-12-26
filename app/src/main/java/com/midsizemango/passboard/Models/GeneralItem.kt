package com.midsizemango.passboard.Models

import com.midsizemango.noteboard.Models.Note

/**
 * Created by ABC on 12/23/2017.
 */
class GeneralItem : ListItem() {

    var password: Password? = null
    var note: Note? = null

    override fun getType(): Int {
        return TYPE_GENERAL
    }
}