package com.jstefanelli.jfs3d.engine

class Mathf {
    companion object {
        private val Pif = Math.PI.toFloat()
        private val Ef = Math.E.toFloat()

        fun toRadians(angle: Float) : Float {
            return (angle * Pif) / 180.0f
        }
    }
}