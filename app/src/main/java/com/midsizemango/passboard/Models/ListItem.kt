package com.midsizemango.passboard.Models

/**
 * Created by ABC on 12/23/2017.
 */
abstract class ListItem {

   val TYPE_NAME: Int = 0
   val TYPE_GENERAL: Int = 1

    abstract fun getType(): Int
}