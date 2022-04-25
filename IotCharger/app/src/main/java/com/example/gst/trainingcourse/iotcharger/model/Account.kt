package com.example.gst.trainingcourse.iotcharger.model

import java.io.Serializable

data class Account (
    var account: String ?= null,
    var password: String ?= null
): Serializable