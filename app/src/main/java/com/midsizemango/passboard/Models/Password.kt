package com.midsizemango.passboard.Models

import java.io.Serializable

/**
 * Created by prasads on 15/12/17.
 */

class Password: Serializable {

    var pass_id: String? = null
    var pass_name: String? = null
    var pass_pass: String? = null
    var pass_link: String? = null
    var pass_text: String? = null
    var pass_color: Int? = null
    var pass_user_id: String? = null
    var pass_email: String? = null
    var pass_category: String? = null
    var pass_id_copy: String? = null

    fun getPassCategory(): String? {
        return pass_category
    }

    fun setPassCategory(pass_category: String) {
        this.pass_category = pass_category
    }

    fun getPassId(): String?{
        return pass_id
    }

    fun setPassId(pass_id: String) {
        this.pass_id = pass_id
    }

    fun getPassColor(): Int?{
        return pass_color
    }

    fun setPassColor(pass_color: Int) {
        this.pass_color = pass_color
    }

    fun getPassName(): String?{
        return pass_name
    }

    fun setPassName(pass_name: String) {
        this.pass_name = pass_name
    }

    fun getPassPass(): String?{
        return pass_pass
    }

    fun setPassPass(pass_pass: String) {
        this.pass_pass = pass_pass
    }

    fun getPassLink(): String?{
        return pass_link
    }

    fun setPassLink(pass_link: String) {
        this.pass_link = pass_link
    }


    fun getPassText(): String?{
        return pass_text
    }

    fun setPassText(pass_text: String) {
        this.pass_text = pass_text
    }

    fun getPassUserId(): String?{
        return pass_user_id
    }

    fun setPassUserId(pass_user_id: String) {
        this.pass_user_id = pass_user_id
    }

    fun getPassEmail(): String?{
        return pass_email
    }

    fun setPassEmail(pass_email: String) {
        this.pass_email = pass_email
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Password

        if (pass_id != other.pass_id) return false
        if (pass_name != other.pass_name) return false
        if (pass_pass != other.pass_pass) return false
        if (pass_link != other.pass_link) return false
        if (pass_text != other.pass_text) return false
        if (pass_color != other.pass_color) return false
        if (pass_user_id != other.pass_user_id) return false
        if (pass_email != other.pass_email) return false
        if (pass_category != other.pass_category) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pass_id?.hashCode() ?: 0
        result = 31 * result + (pass_name?.hashCode() ?: 0)
        result = 31 * result + (pass_pass?.hashCode() ?: 0)
        result = 31 * result + (pass_link?.hashCode() ?: 0)
        result = 31 * result + (pass_text?.hashCode() ?: 0)
        result = 31 * result + (pass_color ?: 0)
        result = 31 * result + (pass_user_id?.hashCode() ?: 0)
        result = 31 * result + (pass_email?.hashCode() ?: 0)
        result = 31 * result + (pass_category?.hashCode() ?: 0)
        return result
    }


}