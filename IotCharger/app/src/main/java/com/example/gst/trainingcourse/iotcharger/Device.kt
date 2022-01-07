package com.example.gst.trainingcourse.iotcharger

import java.io.Serializable

data class Device (
    var name: String? = null,
    var status: String? = null,
    var serverPass: String? = null,
    var clientPass: String? = null
): Serializable