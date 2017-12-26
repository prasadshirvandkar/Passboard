package com.midsizemango.passboard.Models

/**
 * Created by ABC on 12/23/2017.
 */
class NameItem: ListItem(){

    var name:String? = null

    override fun getType(): Int {
        return TYPE_NAME
    }

}