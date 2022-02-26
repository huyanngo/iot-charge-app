package com.example.gst.trainingcourse.iotcharger.`object`

import java.io.Serializable
import java.util.*

data class Device(
    var name: String? = null,
    var status: Int = 0,
    var serverPass: String? = null,
    var clientPass: String? = null,
    var dateOn: String? = null
): Serializable