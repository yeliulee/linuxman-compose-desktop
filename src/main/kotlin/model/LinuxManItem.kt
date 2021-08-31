package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * LinuxManItem
 * @author yeliulee
 * Created at 2021/8/31 00:21
 */
@Serializable
class LinuxManItem(
    @SerialName("n") val name: String,
    @SerialName("p") val path: String,
    @SerialName("d") val desc: String
)