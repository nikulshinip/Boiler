package obj.setting

import obj.Logging

data class Setting(val name : String, var value : Int, val title : String) : Logging{

    override fun toString(): String = "{${javaClass.name} : title='$title', value='$value'}"

}