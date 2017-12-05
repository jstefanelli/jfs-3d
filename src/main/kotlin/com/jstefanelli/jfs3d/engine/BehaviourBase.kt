package com.jstefanelli.jfs3d.engine

class BehaviourBase {

    fun makeInstance() : BehaviourInstance{
        return BehaviourInstance(this)
    }

    class BehaviourInstance (val type: BehaviourBase){
            fun update(){

            }
    }
}